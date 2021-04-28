package voxelGame.blocks;

import org.lwjgl.util.vector.Vector3f;

import voxelEngine.World;
import voxelGame.ParticleEmitter;
import voxelGame.entities.Entity;

public class DirtBlock extends Block{

	public DirtBlock() {
		super();
	}
	
	public String getName(){
		return "dirt block";
	}
	
	protected int getTextureId(int side){
		return 2;
	}
	
	public float getExplosionResistance(){
		return 0.1f;
	}
	
	public void onDestroy(Entity causer, World w, int x, int y, int z){
		
		ParticleEmitter.emitBlockDestroy(
				causer, 
				x, y, z, 
				new Vector3f(0.33f, 0.18f, 0.09f), 0.025f, 
				0, 0.003f, 
				30, 60, 
				0.2f, 0.4f, 
				3000, 6000
		);
	}
}
