package voxelGame.entities;

import renderEngine.entities.RenderEntity;
import renderEngine.models.TexturedModel;
import renderEngine.resources.materialResources.MaterialResources;
import renderEngine.resources.modelResources.ModelResources;
import vectors.Vec3D;
import vectors.Vec3I;
import voxelEngine.Voxel;
import voxelGame.blocks.SandBlock;
import voxelGame.threads.WorldManagerThread;

public class FallingSand extends Entity{

	private static TexturedModel model;
	
	private Vec3D position;
	
	private float velocity = 0;
	
	private static final float gravity = 0.001f;
	private static final float terminalVelocity = 0.1f;
	
	private boolean placed = false;
	private long placedTime;
	
	private TexturedModel getTexturedModel(){
		if(model == null){
			model = new TexturedModel(ModelResources.get("cube"), MaterialResources.get("sand"));
		}
		return model;
	}
	
	public FallingSand(Vec3D position){
		this.position = position;
	}

	@Override
	public Vec3D getPosition() {
		return position;
	}

	@Override
	public boolean update(double timeElapsed) {
		Vec3I iPos = new Vec3I(position);
		if(!placed){
			Voxel under = WorldManagerThread.world.getAt(iPos.x, iPos.y, iPos.z);
			if(under != null && under.isSolid()){
				WorldManagerThread.setBlock(this, iPos.x, iPos.y+1, iPos.z, new SandBlock(), 0, false);
				placed = true;
				placedTime = System.currentTimeMillis();
			}else{
				position.y -= velocity;
				velocity = Math.min(velocity + gravity, terminalVelocity);
			}
			return false;
		}else{
			Voxel block = WorldManagerThread.world.getAt(iPos.x, iPos.y + 1, iPos.z);
			if(block != null || System.currentTimeMillis() - placedTime > 500){
				return true;
			}
			return false;
		}
		
	}
	
	@Override
	public RenderEntity getRenderEntity(Vec3D centerPosition){
		Vec3D ePos = new Vec3D(position.x + 0.5f, position.y + 0.5f, position.z + 0.5f);
		ePos.sub(centerPosition);
		RenderEntity re = new RenderEntity(getTexturedModel(), ePos.getVector3f());
		re.setScale(0.5f);
		return re;
	}
}
