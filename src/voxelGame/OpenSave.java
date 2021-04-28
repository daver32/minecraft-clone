package voxelGame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import tools.BytePacker;
import voxelEngine.Chunk;

public class OpenSave {
	
	public static final int REGION_WIDTH = 32;
	public static final int REGION_SIZE = REGION_WIDTH * REGION_WIDTH;
	
	public static final int SECTOR_SIZE = 2048;
	
	private final String name;
	
	private ArrayList<ChunkRegion> openRegions = new ArrayList<ChunkRegion>();
	
	public OpenSave(String saveName){
		name = saveName;
	}
	
	public boolean saveChunk(Chunk c){
		try {
			ChunkRegion r = getRegion(c);
			r.saveChunk(c);
			return true;
		} catch (IOException e) {
			System.err.println("Saving failed...");
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean loadChunk(Chunk c){
		try {
			ChunkRegion r = getRegion(c);
			return r.loadChunk(c);
		} catch (IOException e) {
			System.err.println("Loading failed...");
			e.printStackTrace();
			return false;
		}
	}
	
	private ChunkRegion getRegion(Chunk c) throws IOException{
		int rx = Math.floorDiv(c.wPosX / Chunk.WIDTH, REGION_WIDTH);
		int rz = Math.floorDiv(c.wPosZ / Chunk.WIDTH, REGION_WIDTH);
		
		for(ChunkRegion r : openRegions){
			if(r.x == rx && r.z == rz){
				return r;
			}
		}
		return createRegion(rx, rz);
	}
	
	private ChunkRegion createRegion(int rx, int rz) throws IOException{
		ChunkRegion reg = new ChunkRegion(rx, rz);
		openRegions.add(reg);
		return reg;
	}
	
	@SuppressWarnings("unchecked")
	public void update(){
		long time = System.currentTimeMillis();
		for(ChunkRegion r : (ArrayList<ChunkRegion>)openRegions.clone()){
			if(time - r.lastOperationTime > ChunkRegion.MAX_IDLE_TIME){
				r.close();
				openRegions.remove(r);
			}
		}
	}
	
	public void close(){
		for(ChunkRegion r : openRegions){
			r.close();
		}
	}
	
	
	private class ChunkRegion{
		
		public final File file;
		
		public final int x, z;
		
		public final int LOOKUP_TABLE_SIZE = REGION_SIZE * 4;
		
		public long lastOperationTime;
		public static final long MAX_IDLE_TIME = 600000;
		
		
		public ChunkRegion(int x, int z) throws IOException{
			this.x = x;
			this.z = z;
			
			String dirPath = "saves/" + name + "/";
			String fullPath = dirPath + x + " " + z + ".rgn";
			
			File dir = new File(dirPath);
			dir.mkdirs();
			
			file = new File(fullPath);
			boolean newFile = false;
			if(!file.exists()){
				file.createNewFile();
				newFile = true;
			}

			if(newFile){
				write(0, extendChunkData(genLookupTable()));
			}
			
			lastOperationTime = System.currentTimeMillis();
		}
		
		public boolean loadChunk(Chunk c) throws IOException{
			int cx = Math.abs(c.wPosX / Chunk.WIDTH - this.x * REGION_WIDTH);
			int cz = Math.abs(c.wPosZ / Chunk.WIDTH - this.z * REGION_WIDTH);
			
			byte[] lookup = read(0, LOOKUP_TABLE_SIZE);
			
			int tableIndex = (cx * REGION_WIDTH + cz) * 4;
			
			int sectorPos = (int)BytePacker.unpackShortFromArray(lookup, tableIndex) - Short.MIN_VALUE;
			int sectorSize = (int)BytePacker.unpackShortFromArray(lookup, tableIndex + 2) - Short.MIN_VALUE;
				
			if(sectorSize == 0){
				return false;
			}else{
				byte[] data = read((sectorPos + 1) * SECTOR_SIZE, (sectorPos + sectorSize + 2) * SECTOR_SIZE);
				ChunkSaver.parseChunkData(data, c, data.length);
				return true;
			}
		}
		
		public void saveChunk(Chunk c) throws IOException{
			int cx = Math.abs(c.wPosX / Chunk.WIDTH - this.x * REGION_WIDTH);
			int cz = Math.abs(c.wPosZ / Chunk.WIDTH - this.z * REGION_WIDTH);
			
			byte[] lookup = read(0, LOOKUP_TABLE_SIZE);
			
			int tableIndex = (cx * REGION_WIDTH + cz) * 4;
			
			int sectorPos = (int)BytePacker.unpackShortFromArray(lookup, tableIndex) - Short.MIN_VALUE;
			int sectorSize = (int)BytePacker.unpackShortFromArray(lookup, tableIndex + 2) - Short.MIN_VALUE;

			
			byte[] chunkData = extendChunkData(ChunkSaver.genChunkData(c));
			int sectorsNeeded = chunkData.length / SECTOR_SIZE + 1;
			
			if(sectorSize == 0 || sectorSize < sectorsNeeded){
				sectorPos = findEmptySpot(lookup, sectorsNeeded);
			}
			
		
			write((sectorPos + 1) * SECTOR_SIZE, chunkData);
			
			
			BytePacker.packShortToArray(lookup, (short) (sectorPos + Short.MIN_VALUE), tableIndex);
			BytePacker.packShortToArray(lookup, (short) (sectorsNeeded + Short.MIN_VALUE), tableIndex + 2);
			
			write(0, lookup);
		}
		
		private int findEmptySpot(byte[] lookup, int sectorsNeeded){ //returns the first empty sector index
			ArrayList<Integer> takenSpots = new ArrayList<Integer>();
			int biggest = 0;
			
			for(int i = 0; i < REGION_SIZE; i++){
				int tableIndex = i*4;
				
				int sectorPos = BytePacker.unpackShortFromArray(lookup, tableIndex) - Short.MIN_VALUE;
				int sectorSize = BytePacker.unpackShortFromArray(lookup, tableIndex + 2) - Short.MIN_VALUE;
				
				if(sectorSize != 0){
					for(int s = 0; s < sectorSize; s++){
						int currSector = sectorPos + s;
						
						takenSpots.add(currSector);
						if(currSector > biggest){
							biggest = currSector;
						}
					}
				}
			}
			
			for(int i = 0; i < biggest; i++){
				boolean valid = true;
				for(int s = 0; s < sectorsNeeded; s++){
					if(takenSpots.contains(i + s)){
						valid = false;
						break;
					}
				}
				if(valid){
					return i;
				}
			}

			return biggest + 1;
		}

		
		private byte[] genLookupTable(){
			byte[] res = new byte[LOOKUP_TABLE_SIZE];
			for(int i = 0; i < res.length; i+=2){
				res[i] = 0;
				res[i+1] = -128;
			}
			return res;
		}
		
		private void write(int pos, byte[] data) throws IOException{
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			FileChannel fc = raf.getChannel();
			fc.position(pos);
			ByteBuffer buffer = ByteBuffer.wrap(data);
			fc.write(buffer);
			buffer.clear();
			raf.close();
		}
		
		private byte[] read(int start, int end) throws IOException{
			ByteBuffer buffer = ByteBuffer.allocate(end - start + 1);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			FileChannel fc = raf.getChannel();
			fc.position(start);
			fc.read(buffer);
			byte[] data = buffer.array();
			raf.close();
			buffer.clear();
			return data;
		}

		private byte[] extendChunkData(byte[] data){
			int missing = SECTOR_SIZE - ((data.length + SECTOR_SIZE) % SECTOR_SIZE);
			missing = 2;
			byte[] nData = new byte[data.length + missing];
			for(int i = 0; i < data.length; i++){
				nData[i] = data[i];
			}
			for(int i = data.length; i < nData.length; i++){
				nData[i] = -128;
			}
			return nData;
		}
		
		
		private void extendRaf(){
			lastOperationTime = System.currentTimeMillis();
		}
		
		public void close(){

		}
		
	}
}


