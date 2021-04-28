package voxelGame.blocks;

import org.lwjgl.util.vector.Vector3f;

import voxelEngine.World;
import voxelGame.ParticleEmitter;
import voxelGame.entities.Entity;

public class WoodPlankBlock extends Block{

	private static Vector3f particleColor = new Vector3f(0.4f,0.24f,0.12f);
	
	public String getName(){
		return "wooden plank block";
	}
	
	public WoodPlankBlock(){
	}
	
	protected int getTextureId(int side){
		return 8;
	}
	
	public void onDestroy(Entity causer, World w, int x, int y, int z){

		ParticleEmitter.emitBlockDestroy(
				causer, 
				x, y, z, 
				particleColor, 0.025f, 
				0, 0.003f, 
				30, 60, 
				0.2f, 0.4f, 
				3000, 6000
		);

	}
	
	public WoodPlankBlock copy(){
		return new WoodPlankBlock();
	}
}
