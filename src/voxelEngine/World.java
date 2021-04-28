package voxelEngine;

import java.util.ArrayList;

import vectors.Vec3D;
import voxelGame.ChunkSaver;
import voxelGame.OpenSave;
import voxelGame.entities.Entity;
import voxelGame.threads.GeometryGeneratorThread;
import voxelGame.threads.GraphicsThread;
import voxelGame.threads.WorldLoaderThread;
import voxelGame.threads.WorldManagerThread;

public class World {
	public static final int DEF_RENDER_DISTANCE = 8;
	public static final int CHUNKS_SIZE = 8;
	
	private int renderDistance = DEF_RENDER_DISTANCE;
	
	public Chunk[][] chunks;
	

	
	private int offsetX = 0, offsetZ = 0; //offset in chunks
	
	public World(){
		chunks = new Chunk[CHUNKS_SIZE*2][CHUNKS_SIZE*2];
	}
	
	private boolean chunksLocked = false;
	
	private static final int[][] neighborDirs = {
		{1, 0}, {-1, 0}, {0, 1}, {0, -1},
	};
	
	private static final int[][] updateDirs = {
		{0, 0, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}
	};
	
	public void init(){
		GeometryGeneratorThread.pause();
		ChunkSaver.currentSave = new OpenSave("world1");
		
		for(int x = 0; x < chunks.length; x++){
			for(int z = 0; z < chunks.length; z++){
				Chunk c = new Chunk(Chunk.WIDTH*(offsetX - CHUNKS_SIZE + x), Chunk.WIDTH*(offsetZ - CHUNKS_SIZE + z));
				
				if(!WorldLoaderThread.chunksToLoad.contains(c)){
					WorldLoaderThread.chunksToLoad.add(c);
				}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				chunks[x][z] = c;
			}
		}
		
		while(WorldLoaderThread.getQueueLength() > 0){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		setNeighbors(chunks);


		GeometryGeneratorThread.unpause();
		
		for(int x = 0; x < chunks.length; x++){
			for(int z = 0; z < chunks.length; z++){
				Chunk c = chunks[x][z];
				

				if(c != null){
					
					double dist = Math.hypot(x - CHUNKS_SIZE, z - CHUNKS_SIZE);
					
					if(dist <= renderDistance){
						for(int i = 0; i < Chunk.NUM_ARRAYS; i++){
							GeometryGeneratorThread.addArray(c.voxelArrays[i], false);
						}
					}
				}
			}
		}
		
	}
	
	private void setNeighbors(Chunk[][] chunkArray){
		for(int x = 0; x < chunks.length; x++){
			for(int z = 0; z < chunks.length; z++){
				Chunk c = chunkArray[x][z];
				if(c != null){
					for(int dir = 0; dir < 4; dir++){
						try{
							Chunk neighbor = chunkArray[x + neighborDirs[dir][0]][z + neighborDirs[dir][1]];
							if(neighbor != null){
								c.addNeighbor(neighbor, dir);
							}
						}catch(ArrayIndexOutOfBoundsException e){}

						
					}
				}
			}
		}
	}
	
	public ArrayList<VoxelVao> getVaos(Vec3D centerPos){ //graphics thread
		long t1 = System.currentTimeMillis();
		while(chunksLocked){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}
		chunksLocked = true;
		long t2 = System.currentTimeMillis() - t1;
		if(t2 > 10){
			System.out.println("t " + t2);
		}
		
		Chunk[][] chunksCopy = chunks.clone();

		ArrayList<VoxelVao> res = new ArrayList<VoxelVao>();
		for(int x = 0; x < chunks.length; x++){
			for(int z = 0; z < chunks.length; z++){
				
				double dist = Math.hypot(x + 0.5 - CHUNKS_SIZE, z + 0.5 - CHUNKS_SIZE);
				
				if(dist <= renderDistance){
					
					Chunk c = chunksCopy[x][z];
					if(c != null && c.generated){
						for(int i = 0; i < Chunk.NUM_ARRAYS; i++){
							VoxelArray array = c.voxelArrays[i];
							VoxelVao v = array.vao;
							if(v != null && v.vertexCount != 0){
								v.updateOffsetPosition(centerPos);
								res.add(v);
							}else if(v == null && c.allNeighborsSet()){
								VoxelGeomArray a = array.voxelGeomArray;
								if(a == null){
									GeometryGeneratorThread.addArray(array, false);
								}
								
							}
						}	
					}
				}
			}
		}
		
		chunksLocked = false;

		return res;
	}

	public void shiftChunks(int shiftX, int shiftZ){


		
		if(shiftX != 0 || shiftZ != 0){
			while(chunksLocked){
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			}
			chunksLocked = true;
			
			
			offsetX += shiftX;
			offsetZ += shiftZ;
			
			Chunk[][] newChunks = new Chunk[CHUNKS_SIZE*2][CHUNKS_SIZE*2];
			Chunk[][] oldChunks = chunks.clone();
			
			for(int x = 0; x < newChunks.length; x++){
				for(int z = 0; z < newChunks.length; z++){
					try{
						newChunks[x][z] = oldChunks[x+shiftX][z+shiftZ];
						oldChunks[x+shiftX][z+shiftZ] = null;
					}catch(ArrayIndexOutOfBoundsException e){}
				}
			}
			
			for(int x = 0; x < newChunks.length; x++){
				for(int z = 0; z < newChunks.length; z++){
					unloadChunk(oldChunks[x][z]);
				}
			}
			
			for(int x = 0; x < newChunks.length; x++){
				for(int z = 0; z < newChunks.length; z++){
					if(newChunks[x][z] == null){
						newChunks[x][z] = new Chunk(Chunk.WIDTH*(offsetX - CHUNKS_SIZE + x), Chunk.WIDTH*(offsetZ - CHUNKS_SIZE + z));
						WorldLoaderThread.chunksToLoad.add(newChunks[x][z]);
					}
				}
			}
			setNeighbors(newChunks);
			

			chunks = newChunks;

			
			chunksLocked = false;
		}

	}
	
	private void unloadChunk(Chunk c){
		if(c != null){
			//ChunkSaver.saveChunk(c);
			WorldLoaderThread.chunksToSave.add(c);
			
			for(VoxelVao v : c.vaos){
				if(v != null && v.vaoID != 0){
					GraphicsThread.vaosToRemove.add(v);
				}
			}
		}
	}
	
	public void saveAll(){
		for(int x = 0; x < chunks.length; x++){
			for(int z = 0; z < chunks.length; z++){
				unloadChunk(chunks[x][z]);
			}
		}
	}
	
	public boolean setAt(Entity causer, int x, int y, int z, Voxel v, boolean vaoUpdateNeeded, boolean priority){ // world manager thread
		try{
			int ix = x + CHUNKS_SIZE * Chunk.WIDTH;
			int iz = z + CHUNKS_SIZE * Chunk.WIDTH;
			
			int chunkX = ix / Chunk.WIDTH - offsetX;
			int chunkZ = iz / Chunk.WIDTH - offsetZ;
			Chunk c = chunks[chunkX][chunkZ];
			
			if(c.setAt(causer, ix % Chunk.WIDTH, y, iz % Chunk.WIDTH, v, vaoUpdateNeeded, priority)){
				updateVoxelsAround(x, y, z, priority, vaoUpdateNeeded);
				return true;
			}
		}catch(Exception e){

		}
		return false;
	}
	
	public Voxel getAt(int x, int y, int z){ // world manager thread
		try{
			x += CHUNKS_SIZE * Chunk.WIDTH;
			z += CHUNKS_SIZE * Chunk.WIDTH;
			
			int chunkX = x / Chunk.WIDTH - offsetX;
			int chunkZ = z / Chunk.WIDTH - offsetZ;
			Chunk c = chunks[chunkX][chunkZ];
			return c.getAt(x % Chunk.WIDTH, y, z % Chunk.WIDTH);
		}catch(Exception e){
		}
		return null;
	}
	
	private void updateVoxelsAround(int x, int y, int z, boolean vaoUpdateNeeded, boolean priority){
		for(int[] dir : updateDirs){
			int ix = x + dir[0];
			int iy = y + dir[1];
			int iz = z + dir[2];

			WorldManagerThread.updateBlock(ix, iy, iz, vaoUpdateNeeded, 0, priority);
		}
	}
	
	public void updateVoxel(int x, int y, int z, boolean vaoUpdateNeeded, boolean priority){ // world manager thread
		Voxel v = getAt(x, y, z);
		VoxelArray arr = getArrayAt(x, y, z);
		
		if(vaoUpdateNeeded){
			updateVaoAt(x, y, z, priority);
		}
		
		if(v != null){
			v.update(this, x, y, z, arr, priority);
		}
	}
	
	private void updateVaoAt(int x, int y, int z, boolean priority){
		try{
			x += CHUNKS_SIZE * Chunk.WIDTH;
			z += CHUNKS_SIZE * Chunk.WIDTH;
			
			int chunkX = x / Chunk.WIDTH - offsetX;
			int chunkZ = z / Chunk.WIDTH - offsetZ;
			Chunk c = chunks[chunkX][chunkZ];
			c.updateVaoAt(y, priority);
			
			//System.out.println(x + " " + y + " " + z + ", " + chunkX + " " + chunkZ);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public VoxelArray getArrayAt(int x, int y, int z){
		try{
			x += CHUNKS_SIZE * Chunk.WIDTH;
			z += CHUNKS_SIZE * Chunk.WIDTH;
			
			int chunkX = x / Chunk.WIDTH - offsetX;
			int chunkZ = z / Chunk.WIDTH - offsetZ;
			Chunk c = chunks[chunkX][chunkZ];
			return c.voxelArrays[y / Chunk.WIDTH];
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}

	}
	
	public void updateChunks(){
		for(int x = 0; x < chunks.length; x++){
			for(int z = 0; z < chunks.length; z++){

				
				for(int i = 0; i < 5; i++){
					try{Thread.sleep(0);}catch(InterruptedException e){}
					
					for(int i2 = 0; i2 < 5; i2++){

						
						int ix = (int) (Math.random() * Chunk.WIDTH);
						int iy = (int) (Math.random() * Chunk.HEIGHT);
						int iz = (int) (Math.random() * Chunk.WIDTH);
						
						Chunk c = chunks[x][z];
						if(c != null && c.generated){
							
							Voxel v = c.getAt(ix, iy, iz);
							if(v != null){
								v.onRandTick(this, c.wPosX + ix, iy, c.wPosZ + iz);
							}
						}
					}
				}
				

				


			}
		}
	}
	/*
	public void setCHUNKS_SIZE(int d){ // world manager thread

		Chunk[][] newArray = new Chunk[d*2][d*2];
		
		if(d > CHUNKS_SIZE){
			int start = (d - CHUNKS_SIZE) / 2;
			int end = d - start;
			for(int x = start; x < end; x++){
				for(int z = start; z < end; z++){
					newArray[x][z] = chunks[x-start][z-start];
				}
			}
			
			for(int x = 0; x < newArray.length; x++){
				for(int z = 0; z < newArray.length; z++){
					if(newArray[x][z] == null){
						
					}
				}
			}
		}
	}
	*/
	
}
