package voxelGame;

import tools.LineCounter;
import voxelGame.threads.GeometryGeneratorThread;
import voxelGame.threads.GraphicsThread;
import voxelGame.threads.LogicThread;
import voxelGame.threads.PhysicsThread;
import voxelGame.threads.WorldGenThread;
import voxelGame.threads.WorldLoaderThread;
import voxelGame.threads.WorldManagerThread;

public class VoxelGame {

	public static void main(String[] args) {
		/*
		for(short i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++){
			byte[] arr = new byte[2];
			BytePacker.packShortToArray(arr, i, 0);
			short u = BytePacker.unpackShortFromArray(arr, 0);
			if(u != i){
				System.out.println(i + " " + u);
			}
		}
		
		
		System.exit(-1);*/
		
		System.out.println(LineCounter.countLines() + " lines");
		
		WorldGenThread.runThreads();
		WorldManagerThread.runThread();
		LogicThread.runThread();
		WorldLoaderThread.runThread();
		GeometryGeneratorThread.runThread();
		PhysicsThread.runThread();
		
		GraphicsThread.run();
		
	}

}
