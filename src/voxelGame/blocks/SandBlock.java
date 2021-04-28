package voxelGame.blocks;

import org.lwjgl.util.vector.Vector3f;

import vectors.Vec3D;
import voxelEngine.Voxel;
import voxelEngine.VoxelArray;
import voxelEngine.World;
import voxelGame.ParticleEmitter;
import voxelGame.entities.Entity;
import voxelGame.entities.FallingSand;
import voxelGame.threads.LogicThread;

public class SandBlock extends Block{

	protected boolean isFalling = false;
	
	public String getName(){
		return "sand block";
	}
	
	public SandBlock() {
		super();
	}
	
	protected int getTextureId(int side){
		return 12;
	}
	
	public float getExplosionResistance(){
		return 0.8f;
	}
	
	public boolean hasPhysics(){
		return false;
	}
	
	protected void update(World w, int x, int y, int z, VoxelArray arr, boolean priority){
		Voxel under = w.getAt(x, y-1, z);

		if((under == null || !under.isSolid())){
			w.setAt(null, x, y, z, null, true, priority);
			FallingSand s = new FallingSand(new Vec3D(x, y, z));
			LogicThread.entities.add(s);
		}
		

	}
	
	public void onDestroy(Entity causer, World w, int x, int y, int z){
		
		ParticleEmitter.emitBlockDestroy(
				causer, 
				x, y, z, 
				new Vector3f(0.71f, 0.58f, 0.3f), 0.025f, 
				0, 0.003f, 
				30, 60, 
				0.2f, 0.4f, 
				3000, 6000
		);
	}
	
}
