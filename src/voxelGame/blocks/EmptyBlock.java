package voxelGame.blocks;

public class EmptyBlock extends Block{

	public EmptyBlock() {
		super();
	}
	
	public String getName(){
		return "air block";
	}
	
	protected int getTextureId(int side){
		return 0;
	}
}
