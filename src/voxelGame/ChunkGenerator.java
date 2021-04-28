package voxelGame;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import noise.RandNoise;
import noise.RandNoise3D;
import noise.perlinNoise1D.PerlinNoise1D;
import noise.perlinNoise2D.CombinedPerlinNoise2D;
import vectors.Vec3I;
import voxelEngine.Chunk;
import voxelEngine.Voxel;
import voxelGame.blocks.DirtBlock;
import voxelGame.blocks.GrassBlock;
import voxelGame.blocks.LeavesBlock;
import voxelGame.blocks.SandBlock;
import voxelGame.blocks.StoneBlock;
import voxelGame.blocks.TallGrassBlock;
import voxelGame.blocks.WaterBlock;
import voxelGame.blocks.WoodBlock;

public class ChunkGenerator {
	
	private static final double GLOBAL_SCALE = 0.5;
	
	private static final double PERLIN_WORM_DENSITY = 0.001;
	private static final int MAX_WORM_LENGTH = 256;
	private static final int MIN_WORM_LENGTH = 128;
	private static final int WORM_SEARCH_RADIUS = 50;
	
	private static final int WATER_LEVEL = -10;
	
	private static final float DESERT_BORDER = 0.5f;
	
	private static final int HEIGHTMAP_OFFSET = 5;
	
	private static int SEED = 45456456;

	private static CombinedPerlinNoise2D temperatureNoise = createTempMap();
	private static RandNoise randomNoise2D = new RandNoise(SEED);
	private static RandNoise3D randomNoise3D = new RandNoise3D(SEED);
	private static CombinedPerlinNoise2D heightMapNoise = createHeightMap();
	private static CombinedPerlinNoise2D mountainNoise = createMountainMap();
	private static CombinedPerlinNoise2D mountainHeightNoise = createMountainHeightMap();
	private static CombinedPerlinNoise2D desertHeightNoise = createDesertHeightMap();
	
	private static CombinedPerlinNoise2D createDesertHeightMap(){
		CombinedPerlinNoise2D res = new CombinedPerlinNoise2D(SEED);
		res.addLayer(50 * GLOBAL_SCALE, 1);
		return res;
	}
	
	private static CombinedPerlinNoise2D createHeightMap(){
		CombinedPerlinNoise2D res = new CombinedPerlinNoise2D(SEED);
		res.addLayer(300 * GLOBAL_SCALE, 1);
		res.addLayer(150 * GLOBAL_SCALE, 2);
		res.addLayer(75 * GLOBAL_SCALE, 2);
		res.addLayer(30 * GLOBAL_SCALE, 2);
		return res;
	}
	
	private static CombinedPerlinNoise2D createMountainHeightMap(){
		CombinedPerlinNoise2D res = new CombinedPerlinNoise2D(SEED);
		res.addLayer(10 * GLOBAL_SCALE, 5);
		res.addLayer(30 * GLOBAL_SCALE, 2);
		res.addLayer(50 * GLOBAL_SCALE, 2);
		return res;
	}
	
	private static CombinedPerlinNoise2D createMountainMap(){
		CombinedPerlinNoise2D res = new CombinedPerlinNoise2D(SEED);
		res.addLayer(300 * GLOBAL_SCALE, 1);
		res.addLayer(150 * GLOBAL_SCALE, 0.5);
		return res;
	}
	
	private static CombinedPerlinNoise2D createTempMap(){
		CombinedPerlinNoise2D res = new CombinedPerlinNoise2D(SEED);
		res.addLayer(1000 * GLOBAL_SCALE, 1);
		res.addLayer(100 * GLOBAL_SCALE, 0.1);
		res.addLayer(10 * GLOBAL_SCALE, 0.05);
		return res;
	}
	
	private static int[][] steepnessCalcDirs = {
		{0, 1},
		{0, -1},
		{1, 1},
		{1, -1},
		{1, 0},
		{-1, 1},
		{-1, -1},
		{-1, 0},
	};
	
	
	@SuppressWarnings("unused")
	public static void genChunk(Chunk c){
		
		int cx = c.wPosX;
		int cz = c.wPosZ;
		
		int heighMapSize = Chunk.WIDTH+2*HEIGHTMAP_OFFSET;
		double[][] heightMap = new double[heighMapSize][heighMapSize];
		double[][] temperatureMap = new double[heighMapSize][heighMapSize];
		
		for(int x = 0; x < heighMapSize; x++){
			int wx = cx + x + HEIGHTMAP_OFFSET;
			for(int z = 0; z < heighMapSize; z++){
				int wz = cz + z + HEIGHTMAP_OFFSET;
				
				double mValue = Math.pow(mountainNoise.sample(wx, wz), 5);
				double mhValue = mountainHeightNoise.sample(wx, wz);
				double nValue = heightMapNoise.sample(wx, wz);

				heightMap[x][z] = mValue * (mhValue * 100+350) + (1-mValue) * (nValue * 50 + 10);
				
				double temp = temperatureNoise.sample(wx, wz);
				if(temp > DESERT_BORDER){
					double dValue = desertHeightNoise.sample(wx, wz);
					double duneFactor = (temp - DESERT_BORDER) / (1 - DESERT_BORDER);
					//duneFactor += (1 - duneFactor) / 2;
					
					heightMap[x][z] = duneFactor * (dValue * 10 + 10) + (1-duneFactor) * heightMap[x][z];
				}
					
				temperatureMap[x][z] = temp;
			}
		}

		for(int x = 0; x < Chunk.WIDTH; x++){
			int wx = cx + x;
			int hMapX = x + HEIGHTMAP_OFFSET;

			for(int y = 0; y < Chunk.HEIGHT; y++){
				
				for(int z = 0; z < Chunk.WIDTH; z++){
					int wz = cz + z;
					int hMapZ = z + HEIGHTMAP_OFFSET;
					int h = (int)heightMap[hMapX][hMapZ];
					double t = temperatureMap[hMapX][hMapZ];
					
					Voxel toAdd = null;
					
					boolean desert = t > DESERT_BORDER;
					
					if(y < h){
						if(y < h - 4){
							toAdd = new StoneBlock();
						}else{
							toAdd = desert ? new SandBlock() : new DirtBlock();
						}

					}else if(y == h){
						
						toAdd = desert ? new SandBlock() : new GrassBlock();

					}else if(y < WATER_LEVEL && t < DESERT_BORDER){
							toAdd = new WaterBlock();
					}else if(!desert && y == h + 1 && randomNoise2D.sample(wx, wz) < 0.15){
						toAdd = new TallGrassBlock();
					}
					

					c.setAtWithBounds(x, y, z, toAdd);
					

				}
			}
		}
		
		for(int x = 1; x < heighMapSize-1; x++){
			int wx = cx + x + HEIGHTMAP_OFFSET;
			for(int z = 1; z < heighMapSize-1; z++){
				double t = temperatureMap[x][z];
				if(t < DESERT_BORDER){
					int wz = cz + z + HEIGHTMAP_OFFSET;
					
					double h = heightMap[x][z];
					if(h > WATER_LEVEL){
						
						double steepness = 0;
						for(int[] dir : steepnessCalcDirs){
							steepness = Math.max(steepness, Math.abs(h - heightMap[x+dir[0]][z+dir[1]]));
						}
						

						double treeDensity = ((1-t) - (1-DESERT_BORDER)) * 0.01;
						
						if(steepness < 2 && randomNoise2D.sample(wx, wz) > 1 - treeDensity){
							int size = (int) (8 + randomNoise2D.sample(wx, h) * 3);
							genTree(c, x - HEIGHTMAP_OFFSET, (int) h, z - HEIGHTMAP_OFFSET, size);
						}
					}

				}

			}
		}
		
		if(false){
			ArrayList<PerlinWorm> worms = genWorms(c.wPosX, c.wPosZ);
			
			for(PerlinWorm worm : worms){
				for(Vec3I pos : worm.genPatch()){
					genSphere(pos.x-cx, pos.y, pos.z-cz, null, 10, c);
				}
			}
		}
		
	}
	
	private static void genTree(Chunk c, int x, int y, int z, int size){
		genSphere(x, y+size, z, new LeavesBlock(), size/2, c);
		
		for(int i = 0; i < size; i++){
			c.setAtWithBounds(x, y+i, z, new WoodBlock(true));
		}
	}
	
	private static void genSphere(int x, int y, int z, Voxel material, double r, Chunk c){
		for(int ix = (int)(x-r); ix < x+r; ix++){
			for(int iy = (int)(y-r); iy < y+r; iy++){
				for(int iz = (int)(z-r); iz < z+r; iz++){
					if(Math.sqrt(Math.pow(x-ix, 2)+Math.pow(y-iy, 2)+Math.pow(z-iz, 2)) <= r){
						try{
							Voxel toAdd = material != null ? material.copy() : null;
							c.setAtWithBounds(ix, iy, iz, toAdd);
						}catch(Exception e){
							e.printStackTrace();
						}

						
					}
				}
			}
		}
	}
	
	private static ArrayList<PerlinWorm> genWorms(int x, int z){
		ArrayList<PerlinWorm> res = new ArrayList<PerlinWorm>();
		
		for(int ix = x - WORM_SEARCH_RADIUS; ix < x + WORM_SEARCH_RADIUS; ix++){
			for(int iz = z - WORM_SEARCH_RADIUS; iz < z + WORM_SEARCH_RADIUS; iz++){
				float iy = (float) (randomNoise2D.sample(ix, iz) * 128);
				double sampled = randomNoise3D.sample(ix, iy, iz);
				if(sampled < PERLIN_WORM_DENSITY){
					res.add(new PerlinWorm(ix, iy, iz, sampled));
				}
				
			}
		}
		
		
		
		return res;
	}
	
	private static class PerlinWorm{
		public Vector3f position;
		@SuppressWarnings("unused")
		private PerlinNoise1D noiseX, noiseY, noiseZ;
		private int length;
		private static double scale = 0.01;
		
		public PerlinWorm(float x, float y, float z, double seed){
			position = new Vector3f(x, y, z);
			noiseX = new PerlinNoise1D(seed * 4.240517, scale);
			noiseY = new PerlinNoise1D(seed * 4.846541, scale);
			noiseZ = new PerlinNoise1D(seed * 4.944551, scale);
			
			length = (int)(MIN_WORM_LENGTH + (MAX_WORM_LENGTH - MIN_WORM_LENGTH) * noiseX.sample(x * z));

		}
		
		public ArrayList<Vec3I> genPatch(){
			ArrayList<Vec3I> res = new ArrayList<Vec3I>();
			
			Vec3I prev = null;
			for(int i = 0; i < length; i++){
				move();
				Vec3I curr = new Vec3I(position);
				if(!curr.isSameAs(prev)){
					if(prev != null){
						//System.out.println((curr.x - prev.x) + " " + (curr.y - prev.y) + " " + (curr.z - prev.z));
					}
					
					
					res.add(curr);
					prev = curr;
					

				}
			}

			//System.out.println(res.size());
			return res;
		}
		
		
		private void move(){
			double yawRads = Math.toRadians(noiseX.sample(position.x) * 360);
			double pitchRads = Math.toRadians(noiseZ.sample(position.z) * 360);
			
			//System.out.println(yawRads + " " +pitchRads);
			
			double xzLen = Math.cos(pitchRads);
			Vector3f forwardVec = new Vector3f((float)(xzLen * Math.cos(yawRads)), (float)Math.sin(pitchRads), (float)(xzLen * Math.sin(-yawRads)));
			forwardVec.normalise();
			
			position.x += forwardVec.x * 5;
			position.y += forwardVec.y * 5;
			position.z += forwardVec.z * 5;
		}
	}
	
}
