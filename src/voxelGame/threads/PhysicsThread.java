package voxelGame.threads;

import tools.MTArray;
import voxelEngine.Voxel;
import voxelEngine.VoxelArray;

public class PhysicsThread extends Thread{

	private static PhysicsThread instance;
	
	public static boolean stop = false;
	
	private static MTArray<VoxelArray> arraysToSimulate = new MTArray<VoxelArray>();
	
	public static void runThread(){
		instance = new PhysicsThread();
		instance.start();
	}
	
	public void run(){
		System.out.println("Physics thread: running");
		
		while(!stop){
			
			MTArray<VoxelArray> arrayCopy = arraysToSimulate.clone();
			
			for(VoxelArray a : arrayCopy){
				if(simArray(a) == 0){
					arraysToSimulate.remove(a);
				}
				delay(1);

			}
			
			delay(1);
			//WorldManagerThread.world.updateChunks();

		}
		
		System.out.println("Physics thread: stopped");
	}
	
	private static void delay(long ms){try{Thread.sleep(ms);}catch(InterruptedException e){}}
	
	private static int simArray(VoxelArray a){
		if(a == null || a.voxels == null){return 0;}
		int totalUpdates = 0;
		
		Voxel[][][] voxels = a.voxels;
		
		for(int x = a.width-1; x >= 0; x--){
			for(int y = a.width-1; y >= 0; y--){
				for(int z = a.width-1; z >= 0; z--){
					Voxel v = voxels[x][y][z];
					if(v != null && v.hasPhysics()){
						if(v.doPhysics(WorldManagerThread.world, a.wPosX+x, a.wPosY+y, a.wPosZ+z)){
							totalUpdates++;

						}
					}
				}
			}
		}
		System.out.println(totalUpdates);
		return totalUpdates;
	}
	
	public static void addArray(VoxelArray a){
		if(!arraysToSimulate.contains(a)){
			arraysToSimulate.add(a);
		}
	}
}
