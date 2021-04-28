package voxelGame.entities;

import renderEngine.entities.RenderEntity;
import vectors.Vec3D;

public abstract class Entity {

	public abstract Vec3D getPosition();
	public abstract boolean update(double timeElapsed);
	
	public RenderEntity getRenderEntity(Vec3D centerPosition){
		return null;
	}
}
