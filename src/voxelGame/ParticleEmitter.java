package voxelGame;

import org.lwjgl.util.vector.Vector3f;

import vectors.Vec3D;
import voxelGame.entities.Entity;
import voxelGame.entities.Explosion;
import voxelGame.entities.Particle;
import voxelGame.entities.Player;
import voxelGame.threads.LogicThread;

public class ParticleEmitter {

	public static void emitBlockDestroy(
			Entity causer, 
			int x, int y, int z, 
			Vector3f color, float colorVariance, 
			float velocity, float velocityVariance, 
			int minCount, int maxCount, 
			float minSize, float maxSize, 
			float minDuration, float maxDuration){
		
		emitParticles(causer, x, y, z, x+1, y+1, z+1, color, colorVariance, velocity, velocityVariance, minCount, maxCount, minSize, maxSize, minDuration, maxDuration);
		
	}
	
	public static void emitParticles(
			Entity causer, 
			double minx, double miny, double minz, 
			double maxx, double maxy, double maxz,
			Vector3f color, float colorVariance, 
			float velocity, float velocityVariance, 
			int minCount, int maxCount, 
			float minSize, float maxSize, 
			float minDuration, float maxDuration){
		
		float mSize = 1;
		float mCount = 1;
		
		if(causer == null){
			mCount = 0.3f;
			//return;
		}else if(causer instanceof Explosion){
			mCount = 0.05f;
			mSize = 1.5f;
			velocity += 0.05;
			color.x = 1;
		}else if(!(causer instanceof Player)){
			mCount = 0.35f;
			mSize = 5f;
		}else{
			mCount = 0.5f;
			mSize = 0.8f;
		}
			
		
		int pCount = randInt(mCount*minCount, mCount*maxCount);
		
		for(int i = 0; i < pCount; i++){
			float x = randFloat(minx, maxx);
			float y = randFloat(miny, maxy);
			float z = randFloat(minz, maxz);
			emitParticle(causer, x, y, z, color, colorVariance, velocity, velocityVariance, minSize, maxSize, minDuration, maxDuration, mSize);

		}
	}
	
	private static void emitParticle(
			Entity causer,
			float x, float y, float z, 
			Vector3f color, float colorVariance, 
			float velocity, float velocityVariance, 
			float minSize, float maxSize, 
			float minDuration, float maxDuration,
			float mSize
			){
		
		Vec3D position = new Vec3D(x, y, z);
		
		Vec3D pVelocity;
		if(causer != null && velocity != 0){

			
			pVelocity = subVec(position, causer.getPosition());

			try{
				pVelocity.normalise();
			}catch(IllegalStateException e){
				return;
			}

			pVelocity.x *= velocity;
			pVelocity.y *= velocity;
			pVelocity.z *= velocity;
		}else{
			pVelocity = new Vec3D();
		}
		
		if(velocityVariance != 0){
			pVelocity.x += randFloat(-velocityVariance, velocityVariance);
			pVelocity.y += randFloat(-velocityVariance, velocityVariance);
			pVelocity.z += randFloat(-velocityVariance, velocityVariance);
		}

		Vec3D pColor = new Vec3D(color);
		if(colorVariance != 0){
			float cVariance = randFloat(-colorVariance, colorVariance);
			pColor.x += cVariance;
			pColor.y += cVariance;
			pColor.z += cVariance;
		}

		float pSize = randFloat(mSize*minSize, mSize*maxSize);
		float pDur = randFloat(minDuration, maxDuration);
		Particle p = new Particle(position, pColor, pSize, pVelocity, pDur);
		LogicThread.particles.add(p);
		
	}
	
	private static Vec3D subVec(Vec3D a, Vec3D b){
		return new Vec3D(a.x - b.x, a.y - b.y, a.z - b.z);
	}
	
	private static float randFloat(double min, double max){
		return (float) (min + Math.random() * (max - min));
	}
	
	private static int randInt(double min, double max){
		return (int) (min + Math.random() * (max - min));
	}
}
