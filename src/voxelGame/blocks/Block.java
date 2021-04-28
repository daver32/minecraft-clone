package voxelGame.blocks;

import vectors.Vec3D;
import voxelEngine.Voxel;
import voxelGame.physics.HitboxAABB;

public abstract class Block extends Voxel{

	public boolean isStackable(){ //save related
		return true;
	}
	
	public int[] getData(){
		return null;
	}
	
	public String getName(){
		return "UNDEFINED_BLOCK_" + findIdOf(this);
	}
	
	public HitboxAABB getHitbox(int x, int y, int z){
		return new HitboxAABB(new Vec3D(x, y, z), new Vec3D(1, 1, 1));
	}
	
	private static Block[] blocks = {
			new StoneBlock(),
			new DirtBlock(),
			new GrassBlock(),
			new WoodBlock(),
			new WoodPlankBlock(),
			new SandBlock(),
			new LeavesBlock(),
			new TallGrassBlock(),
			new WaterBlock(),
	};
	
	public static Block getByID(int id){
		if(id == 0)return null;
		try{
			return (Block)blocks[id-1].copy();
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}

	}
	
	public static int findIdOf(Block block){
		for(int i = 0; i < blocks.length; i++){
			if(blocks[i].getClass().equals(block.getClass())){
				return i+1;
			}
		}
		return 0;
	}

}