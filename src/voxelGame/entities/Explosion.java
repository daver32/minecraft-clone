package voxelGame.entities;

import java.util.ArrayList;

import vectors.Vec3D;
import vectors.Vec3I;
import voxelEngine.Voxel;
import voxelEngine.VoxelArray;
import voxelEngine.World;
import voxelGame.threads.WorldManagerThread;

public class Explosion extends Entity{

	private static final int RAY_COUNT = 100;
	
	private Vec3D position;
	public Explosion(Vec3D position) {
		super();
		this.position = position;
	}
	
	private static int[][] sideDirs = {
		{-1, 0, 0},
		{1, 0, 0},
		{0, -1, 0},
		{0, 1, 0},
		{0, 0, -1},
		{0, 0, 1}
	};

	public static void addExplosion(Vec3D position, double strength){
		
		explosionRay[] rays = new explosionRay[RAY_COUNT];
		
		for(int i = 0; i < RAY_COUNT; i++){
			Vec3D finalDir = null;
			while(finalDir == null){
				Vec3D dir = randVec(1);
				if(dir.calcSize() <= 1){
					dir.normalise();
					finalDir = dir;
				}
			}
			
			rays[i] = new explosionRay(new Vec3D(position), finalDir, strength);
		}
		
		int rayCount = RAY_COUNT;
		
		World world = WorldManagerThread.world;
		
		ArrayList<VoxelArray> arraysToUpdate = new ArrayList<VoxelArray>();
		
		Explosion e = new Explosion(new Vec3D(position));
		
		ArrayList<Vec3I> requests = new ArrayList<Vec3I>();
		
		while(rayCount > 0){

			for(int i = 0; i < RAY_COUNT; i++){
				explosionRay ray = rays[i];
				if(ray != null){
					
					int ix = (int)ray.position.x;
					int iy = (int)ray.position.y;
					int iz = (int)ray.position.z;
					
					Voxel v = world.getAt(ix, iy, iz);
					
					if(v != null){
						float res = v.getExplosionResistance();
						if(ray.strength >= res){

							//WorldManagerThread.setBlock(e, ix, iy, iz, null, 0, true, false);
							requests.add(new Vec3I(ix, iy, iz));
							addChunkToUpdates(ix, iy, iz, arraysToUpdate);
							
							for(int[] d : sideDirs){
								int iix = ix + d[0];
								int iiy = iy + d[1];
								int iiz = iz + d[2];
								
								Voxel v1 = world.getAt(iix, iiy, iiz);
								if(v1 != null && v1.getExplosionResistance() <= res){
									//WorldManagerThread.setBlock(e, iix, iiy, iiz, null, 0, true, false);
									requests.add(new Vec3I(iix, iiy, iiz));
									addChunkToUpdates(iix, iiy, iiz, arraysToUpdate);
								}
							}
						}
						ray.strength -= res;
					}else{
						ray.strength -= 0.5;
					}

					if(ray.strength <= 0){
						rays[i] = null;
						rayCount--;
					}else{
						ray.advance();
					}
				}
			}
		}
		System.out.println("s"+arraysToUpdate.size());
		
		for(Vec3I r : requests){
			WorldManagerThread.setBlock(e, r.x, r.y, r.z, null, 0, true, false);
		}
		
		//for(VoxelArray a : arraysToUpdate){
			//a.update(false);
		//}
	}
	
	private static void addChunkToUpdates(int x, int y, int z, ArrayList<VoxelArray> arraysToUpdate){
		VoxelArray array = WorldManagerThread.world.getArrayAt(x, y, z);
		if(array == null)return;
		
		for(VoxelArray a : arraysToUpdate){
			if(array.equals(a)){
				return;
			}
		}
		arraysToUpdate.add(array);
	}
	
	private static class explosionRay{
		
		public Vec3D position, direction;
		public double strength;
		
		public static final float advance = 0.1f;
		
		public explosionRay(Vec3D position, Vec3D direction, double strength) {
			super();
			this.position = position;
			this.direction = direction;
			this.strength = strength;
			
			direction.normalise();
			direction.scale(advance);
		}
		
		public void advance(){
			position.x += direction.x;
			position.y += direction.y;
			position.z += direction.z;
		}
	}
	
	private static Vec3D randVec(float radius){
		return new Vec3D(randDouble(-radius, radius),randDouble(-radius, radius),randDouble(-radius, +radius));
	}
	
	private static double randDouble(double min, double max){
		return min + Math.random() * (max - min);
	}

	public Vec3D getPosition() {
		return position;
	}

	@Override
	public boolean update(double timeElapsed) {return false;}
}
