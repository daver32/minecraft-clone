package voxelGame.threads;

import tools.MTArray;
import voxelEngine.VoxelArray;

// A thread that converts voxels into arrays which are later pushed to the gpu from the GraphicsThread.

public class GeometryGeneratorThread extends Thread{

	public static boolean stop = false;
	
	private static GeometryGeneratorThread instance;
	
	private static MTArray<VoxelArray> arrays = new MTArray<VoxelArray>(true);
	private static MTArray<VoxelArray> lowPriorityArrays = new MTArray<VoxelArray>(true);
	private static boolean paused = false;
	
	private static final int LOW_PRIORITY_LIMIT = 10;
	
	private GeometryGeneratorThread(){}
	
	public static void runThread(){
		instance = new GeometryGeneratorThread();
		instance.start();
	}
	
	public void run(){
		System.out.println("Vao generator thread: running");
		
		while(!stop){
			if(!paused){

				while(true){
					VoxelArray a = arrays.get(0);
					
					while(arrays.contains(a)){
						arrays.remove(a);
					}
					
					if(a == null){break;}
					processArray(a);
					
					
				}
				

				
				VoxelArray a = lowPriorityArrays.get(0);
				lowPriorityArrays.remove(a);
				
				
				if(a != null){
					if(a.vao == null || System.currentTimeMillis() - a.vao.timeCreated > LOW_PRIORITY_LIMIT){
						processArray(a);
					}else{
						lowPriorityArrays.add(a);
					}

				}else{
					delay(10);
				}
				


			}else{
				delay(10);
			}
			//System.out.println("Vao generator thread: running");
		}
		
		System.out.println("Vao generator thread: stopped");
	}
	
	private static void delay(long ms){try{Thread.sleep(ms);}catch(InterruptedException e){}}
	
	private static void processArray(VoxelArray a){
		if(a.parentChunk.allNeighborsSet() && a.parentChunk.allNeighborsGenerated()){
			if(a.parentChunk.allNeighborsSet()){
				try{
					a.voxelGeomArray = a.convertToVaoArray();
					GraphicsThread.geometryToLoad.add(a.voxelGeomArray);
				}catch(NullPointerException e){}

			}
		}
	}
	
	public static void addArray(VoxelArray a, boolean priority){
		if(a == null){return;}
		
		
		MTArray<VoxelArray> targetList;
		if(priority){
			targetList = arrays;
		}else{
			targetList = lowPriorityArrays;
		}
		
		if(!targetList.contains(a)){
			targetList.add(a);
		}
	}
	

	
	public void end(){
		stop = true;
	}
	
	public static void pause(){
		paused = true;
	}
	
	public static void unpause(){
		paused = false;
	}

	static GeometryGeneratorThread getInstance() {
		return instance;
	}
}
