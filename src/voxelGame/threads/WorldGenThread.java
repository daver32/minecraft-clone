package voxelGame.threads;

import java.util.ArrayList;

import tools.MTArray;
import voxelEngine.Chunk;
import voxelGame.ChunkGenerator;

public class WorldGenThread extends Thread{

	private static final int NUM_THREADS = 2;
	private static WorldGenThread[] instances = new WorldGenThread[NUM_THREADS];
	
	public MTArray<Chunk> genQueue = new MTArray<Chunk>();
	
	public static boolean stop = false;
	
	public static void runThreads(){
		System.out.println("World generator thread: running");
		for(int i = 0; i < NUM_THREADS; i++){
			instances[i] = new WorldGenThread();
			instances[i].start();
		}
	}
	
	public static void addToQueue(Chunk c){
		WorldGenThread leastLoaded = null;
		for(WorldGenThread t : instances){
			if(leastLoaded != null){
				int s = t.genQueue.size();
				if(s < leastLoaded.genQueue.size()){
					leastLoaded = t;
				}
			}else{
				leastLoaded = t;
			}
		}
		
		leastLoaded.genQueue.add(c);
		
	}
	
	public void run(){
		setPriority(1);
		while(!stop){
			if(genQueue.size() > 0){
				Chunk c = genQueue.get(0);
				if(c != null){
					ChunkGenerator.genChunk(c);
					c.generated = true;
				}
				genQueue.remove(c);
			}else{
				try {
					sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("World generator thread: stopped");
	}

}
