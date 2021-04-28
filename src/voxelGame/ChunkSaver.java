package voxelGame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import tools.BytePacker;
import voxelEngine.Chunk;
import voxelGame.blocks.Block;
import voxelGame.blocks.EmptyBlock;

public class ChunkSaver {
	
	public static OpenSave currentSave;
	
	private static int range16 = (int) Math.pow(2, 16);

	public static void saveChunk(Chunk c){
		//currentSave.saveChunk(genChunkData(c), c.wPosX / c.WIDTH, c.wPosZ / c.WIDTH);
		String saveName = "save1";
		String filename = "saves/" + saveName + "/" + c.wPosX/Chunk.WIDTH + " " + c.wPosZ/Chunk.WIDTH + ".chunk";
		File dir = new File("saves/" + saveName);
		if(!dir.exists()){
			dir.mkdirs();
		}
	
		FileOutputStream s = null;
		
		try{
			s = new FileOutputStream(filename);
			byte[] data = genChunkData(c);
			s.write(data);
		}catch(IOException e){}
		
		if(s != null){
			try {
				s.close();
			} catch (IOException e) {}
		}

		
	}
	
	public static boolean loadChunk(Chunk c){
		String filename = "saves/save1/" + c.wPosX/Chunk.WIDTH + " " + c.wPosZ/Chunk.WIDTH + ".chunk";
		try{
			FileInputStream s = new FileInputStream(filename);
			byte[] bytes = new byte[1000000];
			int length = s.read(bytes);
			parseChunkData(bytes, c, length);
			s.close();
			return true;
		}catch(IOException e){
			return false;
		}


		
		
		/*
		byte[] data = currentSave.loadChunk(c.wPosX / c.WIDTH, c.wPosZ / c.WIDTH);
		if(data != null && data.length > 0){
			parseChunkData(data, c);
			return true;
		}else{

		}
		return false;*/
	}
	
	public static void parseChunkData(byte[] data, Chunk c, int length){
		
		if(length == 0)return;
		
		int pointer = 0;
		int currID = 0;
		int bytesLength = 0;
		int stackSize;
		
		int ix = 0, iy = 0, iz = 0;
		
		boolean stop = false;
		
		
		while(pointer < length && !stop){
			//System.out.println(pointer);
			try{
				bytesLength = (int)data[pointer] + 128;
				if(bytesLength == 0) return;
				
				currID = (int)data[pointer+1] + 128;
				stackSize = BytePacker.unpackShortFromArray(data, pointer+2) - Short.MIN_VALUE;
				//System.out.println("s"+stackSize);
				pointer += bytesLength + 1;
			}catch(ArrayIndexOutOfBoundsException e){
				break;
			}

			
			//System.out.println(currID + " " + b);
			Block block = Block.getByID(currID);
			if(block == null || block.isStackable()){

				for(int i = 0; i < stackSize; i++){
					
					c.setAtWithBounds(ix, iy, iz, block);
					
					iz++;
					if(iz == Chunk.WIDTH){
						iz = 0;
						iy++;
						if(iy == Chunk.HEIGHT){
							iy = 0;
							ix++;
							if(ix == Chunk.WIDTH){
								stop = true;
							}
						}
					}
				}
			}else{
				//NON-STACKABLE NOT SUPPORTED NOW
			}
		}

	}
	
	public static byte[] genChunkData(Chunk c){
		//byte[] foo = shortToBytes(30000);
		//System.out.println((int)BytePacker.unpackShortFromArray(foo, 0) - Short.MIN_VALUE);
		
		
		ArrayList<Byte> array = new ArrayList<Byte>();
		
		int stackSize = 0;
		int stackID = 0;
		boolean stacking = false;

		
		for(int x = 0; x < Chunk.WIDTH; x++){
			for(int y = 0; y < Chunk.HEIGHT; y++){
				for(int z = 0; z < Chunk.WIDTH; z++){
					
					Block b = (Block) c.getAt(x, y, z);
					int currID;
					if(b == null){
						currID = 0;
						b = new EmptyBlock();
					}else{
						currID = Block.findIdOf(b);
					}
					
					if(stacking){

						if(stackSize < range16 && stackID == currID){
							//add to stack
							stackSize++;

						}else{
							//end stack
							byte[] size = shortToBytes(stackSize);
							addBytes(array, stackID - 128, size[0], size[1]);
							stacking = false;
							
							if(b.isStackable()){
								//start stack
								stacking = true;
								stackSize = 1;
								stackID = currID;
							}else{
								//add single block
								addBytes(array, b.getData());
								stacking = false;
							}
						}
					}else if(b.isStackable()){
						//start stack
						stacking = true;
						stackSize = 1;
						stackID = currID;
					}else{
						//add single block
						addBytes(array, b.getData());
						stacking = false;
					}
				}
			}
		}
		
		
		
		return decomposeByteArray(array);
	}
	
	private static byte[] shortToBytes(int n){
		short s = (short) (n + Short.MIN_VALUE);
		byte[] r = new byte[2];
		BytePacker.packShortToArray(r, s, 0);
		return r;
	}
	
	private static void addBytes(ArrayList<Byte> array, int... bytes){
		array.add((byte) (bytes.length - 128));
		for(int b : bytes){
			array.add((byte) (b));
		}
	}
	
	private static byte[] decomposeByteArray(ArrayList<Byte> bytes){
		byte[] result = new byte[bytes.size()];
		int index = 0;
		for(Byte b : bytes){
			result[index++] = b;
		}
		return result;
	}
}
