package voxelGame.threads;

import tools.BytePacker;
import tools.MTArray;
import voxelEngine.Chunk;
import voxelEngine.VoxelArray;
import voxelGame.ChunkGenerator;
import voxelGame.ChunkSaver;

// A thread that loads, saves and generates chunks.

public class WorldLoaderThread extends Thread{

	private static WorldLoaderThread instance;
	
	public static MTArray<Chunk> chunksToLoad = new MTArray<Chunk>();
	public static MTArray<Chunk> chunksToSave = new MTArray<Chunk>();
	
	public static boolean stop = false;
	

	
	private WorldLoaderThread(){}
	
	public static void runThread(){
		instance = new WorldLoaderThread();
		instance.start();
	}
	
	public void run(){
		System.out.println("World loader thread: running");
		this.setPriority(Thread.MIN_PRIORITY);
		
		while(!stop){

			
			Chunk load = chunksToLoad.get(0);
			Chunk save = chunksToSave.get(0);
			boolean actionPerformed = false;
			if(load != null){
				//System.out.println(chunksToLoad.size());
				
				/*
				if(!ChunkSaver.loadChunk(load)){
					ChunkGenerator.genChunk(load);
					

				}*/
				//long t1 = System.currentTimeMillis();
				if(!ChunkSaver.currentSave.loadChunk(load)){
					//System.out.println("c " + (System.currentTimeMillis() - t1));
					//ChunkGenerator.genChunk(load);
					WorldGenThread.addToQueue(load);
				}else{
					load.generated = true;
				}
				

				actionPerformed = true;
			}
			if(save != null && save.generated){
				ChunkSaver.currentSave.saveChunk(save);
				//ChunkSaver.saveChunk(save);
				
				for(VoxelArray a : save.voxelArrays){
					a.voxels = null;
				}
				
				delay(1);
				actionPerformed = true;
			}
			
			chunksToSave.remove(save);
			chunksToLoad.remove(load);
			
			if(!actionPerformed){
				delay(50);
				ChunkSaver.currentSave.update();
			}
			

		}
		
		System.out.println("World loader thread: stopped");
	}
	
	private static void delay(long ms){try{Thread.sleep(ms);}catch(InterruptedException e){}}
	

	
	public void end(){
		stop = true;
	}
	
	public static int getQueueLength(){
		return chunksToLoad.size();
	}

	static WorldLoaderThread getInstance() {
		return instance;
	}
	
	
}
