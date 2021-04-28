package voxelGame.blocks;

import org.lwjgl.util.vector.Vector3f;

import voxelEngine.Voxel;
import voxelEngine.World;
import voxelGame.ParticleEmitter;
import voxelGame.entities.Entity;
import voxelGame.threads.WorldManagerThread;

public class WoodBlock extends Block{

	public boolean isNatural = false;
	
	public String getName(){
		return "wood block";
	}
	
	private static int[][] updateDirs = new int[][]{
		{1,0,0},
		{-1,0,0},
		{0,1,0},
		{0,-1,0},
		{0,0,1},
		{0,0,-1},
	};
	
	public WoodBlock(boolean natural){
		isNatural = natural;
	}
	
	public WoodBlock(){
		isNatural = false;
	}
	
	protected int getTextureId(int side){
		return 6;
	}
	
	public void onDestroy(Entity causer, World w, int x, int y, int z){

		ParticleEmitter.emitBlockDestroy(
				causer, 
				x, y, z, 
				new Vector3f(0.4f,0.24f,0.12f), 0.025f, 
				0, 0.003f, 
				30, 60, 
				0.2f, 0.4f, 
				3000, 6000
		);
		
		if(!isNatural){
			return;
		}
		for(int[] side : updateDirs){
			int ix = x + side[0];
			int iy = y + side[1];
			int iz = z + side[2];
			Voxel v = w.getAt(ix, iy, iz);
			
			if(v != null){
				boolean n = false;
				if(v instanceof LeavesBlock){
					LeavesBlock cv = (LeavesBlock) v;
					if(cv.isNatural){
						n = true;
						cv.destroyByLumber(w, x, y, z);
					}

				}else if(v instanceof WoodBlock){
					n = ((WoodBlock) v).isNatural;
				}
				
				if(n){
					WorldManagerThread.setBlock(null, ix, iy, iz, null, 0, false);
				}
			}
		}
		

	}
	
	public WoodBlock copy(){
		return new WoodBlock(isNatural);
	}
}
