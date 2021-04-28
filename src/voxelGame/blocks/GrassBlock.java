package voxelGame.blocks;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import voxelEngine.GeometryGenerator.WrappedInt;
import voxelEngine.Voxel;
import voxelEngine.VoxelArray;
import voxelEngine.World;
import voxelGame.ParticleEmitter;
import voxelGame.entities.Entity;

public class GrassBlock extends Block{

	public String getName(){
		return "grass block";
	}
	
	private static int[] sideTextures = {1,1,0,2,1,1};
	
	private static int[][] smoothGrassDirs = {
			{1, 0},
			{-1, 0},
			null,
			null,
			{0, 1},
			{0, -1},
	};
	
	private static int[][][] advSmoothGrassDirs = {
			{{1, -1}, {1, 1}},
			{{-1, 1}, {-1, -1}},
			null,
			null,
			{{1, 1}, {-1, 1}},
			{{-1, -1}, {1, -1}},
	};
	
	public float getExplosionResistance(){
		return 0.1f;
	}
	
	protected int getTextureId(int side){
		return sideTextures[side];
	}
	
	private int getTextureId(int x, int y, int z, int side, VoxelArray a){
		int[] dir = smoothGrassDirs[side];
		if(dir != null){
			Voxel v = a.getAt(x + dir[0], y - 1, z + dir[1]);
			if(v instanceof GrassBlock){
				return sideTextures[2];
			}else{
				int[][] advDir = advSmoothGrassDirs[side];
				boolean left = a.getAt(x + advDir[0][0], y - 1, z + advDir[0][1]) instanceof GrassBlock;
				boolean right = a.getAt(x + advDir[1][0], y - 1, z + advDir[1][1]) instanceof GrassBlock;
				
				if(left && right){
					return 11;
				}else if(left){
					return 10;
				}else if(right){
					return 9;
				}
			}
		}
		
		
		return sideTextures[side];
	}
	
	public void onDestroy(Entity causer, World w, int x, int y, int z){

		ParticleEmitter.emitBlockDestroy(
				causer, 
				x, y, z, 
				new Vector3f(0.33f, 0.18f, 0.09f), 0.025f, 
				0, 0.003f, 
				30, 60, 
				0.2f, 0.4f, 
				3000, 6000
		);
		
		ParticleEmitter.emitParticles(
				causer, 
				x, y + 0.9f, z, 
				x + 1, y + 1, z + 1, 
				new Vector3f(0.3f, 0.3f, 0.08f), 0.025f, 
				0, 0.003f, 
				10, 20, 
				0.2f, 0.4f, 
				3000, 6000
		);
	}
	
	private float[][] randRotateTexCoords(int x, int y, int z, float[][] coords){
		double rand = rand(x, y, z);
		int numRotates = 0;
		if(rand < 0.25){
			numRotates = 1;
		}else if(rand < 0.5){
			numRotates = 2;
		}else if(rand < 0.75){
			numRotates = 3;
		}
		
		float[][] res = coords;
		for(int i = 0; i < numRotates; i++){
			res = rotateCoords(res);
		}
		return res;
	}
	
	private float[][] rotateCoords(float[][] c){ //90 degrees
		float[][] res = new float[4][2];
		res[0] = c[2];
		res[1] = c[0];
		res[2] = c[3];
		res[3] = c[1];
		return res;
	}
	
	public double rand(double x, double y, double z){
		//43758.5453
	    return fract(Math.sin(dot(x, y, z , 7.575755885682, 6.5858588577, 7.4445648634))*43758.5446846453);
	}
	
	private static double dot(double x1, double y1, double z1, double x2, double y2, double z2){
		return x1*x2 + y1*y2 + z1*z2;
	}
	
	private static double fract(double x){
		return x - Math.floor(x);
	}
	
	protected void genQuad(VoxelArray a, int side, int x, int y, int z, ArrayList<Vector3f> positions, ArrayList<Vector3f> normals, ArrayList<Vector3f> tangents, ArrayList<Vector3f> texCoords, ArrayList<Integer> indices, ArrayList<Float> ao, ArrayList<Boolean> displacement, WrappedInt vecId){
		int[] quadIndices = new int[4];
		
		int[][] dir = quadDirs[side];
		
		float[] aoVals = new float[4];
		
		float[][] sideTexCoords = quadTexCoords[side];
		int texID = getTextureId(x, y, z, side, a);
		
		if(texID == 0){
			sideTexCoords = randRotateTexCoords(x, y, z, sideTexCoords);
		}
		
		for(int i = 0; i < 4; i++){
			
			positions.add(new Vector3f(x+dir[i][0], y+dir[i][1], z+dir[i][2]));
			normals.add(Voxel.normals[side]);
			float[] currTexCoords = sideTexCoords[i];

			texCoords.add(new Vector3f(currTexCoords[0], currTexCoords[1], texID));
			tangents.add(Voxel.tangents[side]);
			quadIndices[i] = vecId.value*4+i;
			displacement.add(usesDisplacement());

			int[][] currAoDirs = aoDirs[side][i];
			boolean occluded = false;
			
			if(usesAO()){
				for(int aoDir = 0; aoDir < 3; aoDir++){
					Voxel aoVox = a.getAt(x+currAoDirs[aoDir][0], y+currAoDirs[aoDir][1], z+currAoDirs[aoDir][2]);
					if(aoVox != null && aoVox.usesAO()){
						occluded = true;
						break;
					}
				}
			}

			
			if(occluded){
				ao.add(invAoIntensity);
				aoVals[i] = invAoIntensity;
			}else{
				ao.add(1f);
				aoVals[i] = 1f;
			}
		}
		
		
		if(aoVals[0] + aoVals[3] > aoVals[1] + aoVals[2]){
			//generate flipped quad to get rid of occlusion artifacts
			
			indices.add(quadIndices[0]);
			indices.add(quadIndices[3]);
			indices.add(quadIndices[1]);
			
			indices.add(quadIndices[0]);
			indices.add(quadIndices[2]);
			indices.add(quadIndices[3]);
			
			if(hasTransparency()){
				indices.add(quadIndices[1]);
				indices.add(quadIndices[3]);
				indices.add(quadIndices[0]);
				
				indices.add(quadIndices[3]);
				indices.add(quadIndices[2]);
				indices.add(quadIndices[0]);
			}
			
		}else{
			
			indices.add(quadIndices[0]);
			indices.add(quadIndices[2]);
			indices.add(quadIndices[1]);
			
			indices.add(quadIndices[3]);
			indices.add(quadIndices[1]);
			indices.add(quadIndices[2]);
			
			if(hasTransparency()){
				indices.add(quadIndices[3]);
				indices.add(quadIndices[2]);
				indices.add(quadIndices[1]);
				
				indices.add(quadIndices[0]);
				indices.add(quadIndices[1]);
				indices.add(quadIndices[2]);
			}
		}

		vecId.value += 1;
	}
	
	protected void update(World w, int x, int y, int z, VoxelArray arr, boolean priority){
		Voxel above = w.getAt(x, y+1, z);
		if(above != null && !above.hasTransparency()){
			w.setAt(null, x, y, z, new DirtBlock(), true, priority);
		}
	}

}
