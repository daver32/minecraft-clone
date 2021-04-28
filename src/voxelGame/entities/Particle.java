package voxelGame.entities;

import renderEngine.rendering.simpleParticles.SimpleParticle;
import vectors.Vec3D;
import voxelEngine.Voxel;
import voxelEngine.World;
import voxelGame.threads.WorldManagerThread;

public class Particle{
	
	public Vec3D velocity;
	public Vec3D position;
	public Vec3D color;
	
	public static final float gravity = 0.0001f;
	public static final float terminalVelocity = 1f;
	
	private float duration;
	private float size, scale;
	
	public Particle(Vec3D position, Vec3D color, float size, Vec3D velocity, float duration){
		this.velocity = velocity;
		this.duration = duration;
		scale = this.size = size;
		this.color = color;
		this.position = position;
	}
	
	public boolean update(double timeElapsed){
		timeElapsed /= 1;
		
		Vec3D newPos = new Vec3D(position.x + velocity.x * (float)timeElapsed, position.y + velocity.y * (float)timeElapsed, position.z + velocity.z * (float)timeElapsed);
		if(collides(newPos)){
			velocity = new Vec3D();
		}else{
			position = newPos;
		}
		
		if(duration < 3000){
			scale = size * duration / 3000;
		}

		duration -= timeElapsed;
		if(duration <= 0.01){
			return true;
		}
		
		velocity.y -= gravity;
		checkTerminalVelocity();

		return false;
	}
	
	private boolean collides(Vec3D pos){
		World w = WorldManagerThread.world;
		Voxel v = w.getAt((int)Math.floor(pos.x), (int)Math.floor(pos.y - scale/2), (int)Math.floor(pos.z));
		if(v != null && v.isSolid()){
			return true;
		}
		return false;
	}
	
	private void checkTerminalVelocity(){
		if(velocity.x > terminalVelocity){
			velocity.x = terminalVelocity;
		}else if(velocity.x < -terminalVelocity){
			velocity.x = -terminalVelocity;
		}
		
		if(velocity.y > terminalVelocity){
			velocity.y = terminalVelocity;
		}else if(velocity.y < -terminalVelocity){
			velocity.y = -terminalVelocity;
		}
		
		if(velocity.z > terminalVelocity){
			velocity.z = terminalVelocity;
		}else if(velocity.z < -terminalVelocity){
			velocity.z = -terminalVelocity;
		}
	}
	
	public SimpleParticle getGParticle(Vec3D centerPos){
		Vec3D position = new Vec3D(this.position);
		position.sub(centerPos);
		return new SimpleParticle(position.getVector3f(), color.getVector3f(), scale);
	}
}
