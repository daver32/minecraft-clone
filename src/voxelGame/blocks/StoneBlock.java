package voxelGame.blocks;

import org.lwjgl.util.vector.Vector3f;

import voxelEngine.World;
import voxelGame.ParticleEmitter;
import voxelGame.entities.Entity;

public class StoneBlock extends Block{

	public String getName(){
		return "stone block";
	}
	
	protected int getTextureId(int side){
		return 3;
	}
	
	public void onDestroy(Entity causer, World w, int x, int y, int z){
		ParticleEmitter.emitBlockDestroy(
				causer, 
				x, y, z, 
				new Vector3f(0.4f,0.4f,0.4f), 0.025f, 
				0.001f, 0.003f, 
				50, 80, 
				0.2f, 0.4f, 
				3000, 6000
		);
	}
}
