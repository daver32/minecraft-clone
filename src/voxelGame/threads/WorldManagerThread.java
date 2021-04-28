package voxelGame.threads;

import tools.MTArray;
import voxelEngine.Voxel;
import voxelEngine.World;
import voxelGame.entities.Entity;

public class WorldManagerThread extends Thread{

	private static WorldManagerThread instance;
	
	private static MTArray<Request> blockSet1 = new MTArray<Request>();
	private static MTArray<Request> blockSet2 = new MTArray<Request>((int) 1e6);
	
	private static MTArray<Request> blockUp1 = new MTArray<Request>();
	private static MTArray<Request> blockUp2 = new MTArray<Request>();
	
	private static MTArray<Request> chunkShift = new MTArray<Request>();
	
	public static boolean stop = false;
	
	public static World world;
	public static boolean initialized = false;
	
	private WorldManagerThread(){}
	
	public static void runThread(){
		instance = new WorldManagerThread();
		instance.start();
		
		
	}
	
	public void run(){
		System.out.println("World manager thread: running");
		
		world = new World();
		world.init();
		initialized = true;
		
		while(!stop){
			processAllRequests(blockSet1);
			processAllRequests(blockUp1);
			
			processOneRequest(blockSet2);
			processOneRequest(blockUp2);
			
			processOneRequest(chunkShift);
			
			if(blockSet2.size() + blockUp2.size() == 0){
				delay(1);
			}
			//delay(1);
		}
		System.out.println("World manager thread: stopped");
	}
	
	private static void delay(long ms){try{Thread.sleep(ms);}catch(InterruptedException e){}}
	
	private static void processAllRequests(MTArray<Request> requests){
		while(true){
			Request req = requests.get(0);
			requests.remove(req);
			if(req != null){
				if(!processRequest(req, true)){
					requests.add(req);
				}
			}else{
				break;
			}
		}
	}
	
	private static void processOneRequest(MTArray<Request> requests){
		int l = requests.size();
		for(int i = 0; i < l; i++){
			Request req = requests.get(0);
			requests.remove(req);
			if(processRequest(req, false)){
				return;
			}else{
				requests.add(req);
			}
		}
	}
	
	private static boolean processRequest(Request req, boolean priority){
		if(req instanceof BlockSetRequest){
			BlockSetRequest creq = (BlockSetRequest)req; 
			if(creq.targetTime < System.currentTimeMillis()){
				world.setAt(creq.causer, creq.x, creq.y, creq.z, creq.v, creq.vaoUpdateNeeded, priority);
				return true;
			}
		}else if(req instanceof ChunkShiftRequest){
			ChunkShiftRequest creq = (ChunkShiftRequest)req; 
			world.shiftChunks(creq.x, creq.z);
			return true;
		}else if(req instanceof BlockUpdateRequest){
			BlockUpdateRequest creq = (BlockUpdateRequest)req; 
			if(creq.targetTime < System.currentTimeMillis()){
				world.updateVoxel(creq.x, creq.y, creq.z, creq.vaoUpdateNeeded, priority);
				return true;
			}
		}
		
		return false;
	}
	
	public static void setBlock(Entity causer, int x, int y, int z, Voxel v, long targetTime, boolean priority){
		BlockSetRequest req = new BlockSetRequest(causer, x, y, z, v, targetTime, true);
		MTArray<Request> targetArray = priority ? blockSet1 : blockSet2;
		targetArray.add(req);
	}
	


	
	public static void setBlock(Entity causer, int x, int y, int z, Voxel v, long targetTime, boolean vaoUpdateNeeded, boolean priority){
		BlockSetRequest req = new BlockSetRequest(causer, x, y, z, v, targetTime, vaoUpdateNeeded);
		if(priority){
			blockSet1.add(req);
		}else{
			blockSet2.add(req);
		}
	}
	
	public static void shiftChunks(int x, int z){
		ChunkShiftRequest req = new ChunkShiftRequest(x, z);
		chunkShift.add(req);
	}
	
	public static void updateBlock(int x, int y, int z, boolean vaoUpdateNeeded, long targetTime, boolean priority){
		BlockUpdateRequest req = new BlockUpdateRequest(x, y, z, vaoUpdateNeeded, targetTime);
		if(priority){
			blockUp1.add(req);
		}else{
			blockUp2.add(req);
		}
	}
	
	private static abstract class Request{
	}
	
	private static class BlockSetRequest extends Request{
		int x, y, z;
		Voxel v;
		long targetTime;
		Entity causer;
		boolean vaoUpdateNeeded;
		
		public BlockSetRequest(Entity causer, int x, int y, int z, Voxel v, long targetTime, boolean vaoUpdateNeeded) {
			this.x = x; this.y = y; this.z = z; this.v = v;
			this.targetTime = targetTime;
			this.causer = causer;
			this.vaoUpdateNeeded = vaoUpdateNeeded;
		}
	}
	
	private static class ChunkShiftRequest extends Request{
		int x, z;
		public ChunkShiftRequest(int x, int z) {
			this.x = x; this.z = z;
		}
	}
	
	private static class BlockUpdateRequest extends Request{
		int x, y, z;
		boolean vaoUpdateNeeded;
		long targetTime;
		public BlockUpdateRequest(int x, int y, int z, boolean vaoUpdateNeeded, long targetTime) {
			this.x = x; this.y = y; this.z = z;
			this.vaoUpdateNeeded = vaoUpdateNeeded;
			this.targetTime = targetTime;
		}
	}
}
