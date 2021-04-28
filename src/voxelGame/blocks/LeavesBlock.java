package voxelGame.blocks;

import org.lwjgl.util.vector.Vector3f;

import voxelEngine.Voxel;
import voxelEngine.VoxelArray;
import voxelEngine.World;
import voxelGame.ParticleEmitter;
import voxelGame.entities.Entity;
import voxelGame.threads.WorldManagerThread;

public class LeavesBlock extends Block{

	public boolean isNatural = false;
	public boolean treeWasDestroyed = false;
	
	public String getName(){
		return "leaves block";
	}
	
	private static int[][] updateDirs = new int[][]{
		{1,0,0},
		{-1,0,0},
		{0,1,0},
		{0,-1,0},
		{0,0,1},
		{0,0,-1},
	};
	
	public LeavesBlock(boolean natural){
		isNatural = natural;
	}
	
	public LeavesBlock(){
		isNatural = false;
	}
	
	protected int getTextureId(int side){
		return 7;

	}
	
	public void onRandTick(World w, int x, int y, int z){
		WorldManagerThread.setBlock(null, x, y, z, null, 0, false);
	}
	
	public void destroyByLumber(World w, int x, int y, int z){
		treeWasDestroyed = true;
		long time = System.currentTimeMillis() + 100;
		WorldManagerThread.setBlock(null, x, y, z, null, time, false);
		
		for(int[] side : updateDirs){
			int ix = x + side[0];
			int iy = y + side[1];
			int iz = z + side[2];
			Voxel v = w.getAt(ix, iy, iz);
			
			if(v != null && v instanceof LeavesBlock && !((LeavesBlock)v).treeWasDestroyed && ((LeavesBlock)v).isNatural){
				((LeavesBlock) v).destroyByLumber(w, ix, iy, iz);
			}
		}
	}
	
	public boolean usesDisplacement(){
		return true;
	}
	
	public boolean hasTransparency(){
		return true;
	}
	
	public boolean renderBackfaces(){
		return true;
	}
	
	public boolean isSolid(){return true;}
	
	public LeavesBlock copy(){
		return new LeavesBlock(isNatural);
	}
	
	public void onDestroy(Entity causer, World w, int x, int y, int z){
		ParticleEmitter.emitBlockDestroy(
				causer, 
				x, y, z, 
				new Vector3f(0.2f, 0.3f, 0.08f), 0.025f, 
				0.01f, 0.005f, 
				30, 60, 
				0.2f, 0.4f, 
				1000, 3000
		);
		

	}

	
}
