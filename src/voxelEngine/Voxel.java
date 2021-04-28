package voxelEngine;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import voxelEngine.GeometryGenerator.WrappedInt;
import voxelEngine.lighting.VoxelLightSourceReferrence;
import voxelGame.entities.Entity;

public abstract class Voxel implements Cloneable{
	public boolean checked;
	
	protected static final float invAoIntensity = 0.6f;
	
	public ArrayList<VoxelLightSourceReferrence> lightRefs = new ArrayList<VoxelLightSourceReferrence>();
	
	protected int getTextureId(int side){
		return 0;
	}
	
	protected boolean usesDisplacement(){
		return false;
	}
	
	public boolean isSolid(){
		return true;
	}
	
	public boolean hasTransparency(){
		return false;
	}
	
	public boolean renderBackfaces(){
		return false;
	}
	
	public boolean usesAO(){
		return true;
	}
	
	protected void update(World w, int x, int y, int z, VoxelArray arr, boolean priority){
		
	}
	
	public float getExplosionResistance(){
		return 1;
	}
	
	public boolean hasPhysics(){
		return false;
	}
	
	public boolean doPhysics(World w, int x, int y, int z){
		return false;
	}
	
	public void onDestroy(Entity causer, World w, int x, int y, int z){
		
	}
	
	public void onRandTick(World w, int x, int y, int z){
		//WorldManagerThread.setBlock(x, y, z, null, 0, false);
	}
	
	protected static final int[][][] quadDirs = {
			{{1, 0, 0}, {1, 0, 1}, {1, 1, 0}, {1, 1, 1}},		//+x
			{{0, 0, 0}, {0, 1, 0}, {0, 0, 1}, {0, 1, 1}},		//-x
			{{0, 1, 0}, {1, 1, 0}, {0, 1, 1}, {1, 1, 1}},		//+y
			{{0, 0, 0}, {0, 0, 1}, {1, 0, 0}, {1, 0, 1}},		//-y
			{{0, 0, 1}, {0, 1, 1}, {1, 0, 1}, {1, 1, 1}},		//+z
			{{0, 0, 0}, {1, 0, 0}, {0, 1, 0}, {1, 1, 0}}		//-z
		};
	//{{0, 0}, {1, 0}, {0, 1}, {1, 1}},
	protected static final float[][][] quadTexCoords = {
		{{1, 1}, {0, 1}, {1, 0}, {0, 0}}, 					
		{{0, 1}, {0, 0}, {1, 1}, {1, 0}},					
		{{0, 1}, {0, 0}, {1, 1}, {1, 0}},					
		{{1, 1}, {0, 1}, {1, 0}, {0, 0}},
		{{0, 1}, {0, 0}, {1, 1}, {1, 0}},
		{{1, 1}, {0, 1}, {1, 0}, {0, 0}},
	};
	
	protected static final int[][] sides = {
		{1, 0, 0},
		{-1, 0, 0},
		{0, 1, 0},
		{0, -1, 0},
		{0, 0, 1},
		{0, 0, -1},
	};
	
	protected static final Vector3f[] normals = new Vector3f[]{
		new Vector3f(sides[0][0], sides[0][1], sides[0][2]),
		new Vector3f(sides[1][0], sides[1][1], sides[1][2]),
		new Vector3f(sides[2][0], sides[2][1], sides[2][2]),
		new Vector3f(sides[3][0], sides[3][1], sides[3][2]),
		new Vector3f(sides[4][0], sides[4][1], sides[4][2]),
		new Vector3f(sides[5][0], sides[5][1], sides[5][2]),
	};
	
	protected static final Vector3f[] tangents = new Vector3f[]{
		new Vector3f(0, 1, 0),
		new Vector3f(0, 1, 0),
		new Vector3f(0, 0, 1),
		new Vector3f(0, 0, 1),
		new Vector3f(1, 0, 0),
		new Vector3f(1, 0, 0),
	};
	
	protected static final int[][][][] aoDirs = {
		{
			{{1, -1, 0}, {1, 0, -1}, {1, -1, -1}},
			{{1, -1, 0}, {1, 0, 1}, {1, -1, 1}},
			{{1, 1, 0}, {1, 0, -1}, {1, 1, -1}},
			{{1, 1, 0}, {1, 0, 1}, {1, 1, 1}},
		},
		{
			{{-1, -1, 0}, {-1, 0, -1}, {-1, -1, -1}},
			{{-1, 1, 0}, {-1, 0, -1}, {-1, 1, -1}},
			{{-1, -1, 0}, {-1, 0, 1}, {-1, -1, 1}},
			{{-1, 1, 0}, {-1, 0, 1}, {-1, 1, 1}},
		},
		{
			{{0, 1, -1}, {-1, 1, 0}, {-1, 1, -1}},
			{{0, 1, -1}, {1, 1, 0}, {1, 1, -1}},
			{{0, 1, 1}, {-1, 1, 0}, {-1, 1, 1}},
			{{0, 1, 1}, {1, 1, 0}, {1, 1, 1}},
		},
		{
			{{0, -1, -1}, {-1, -1, 0}, {-1, -1, -1}},
			{{0, -1, 1}, {-1, -1, 0}, {-1, -1, 1}},
			{{0, -1, -1}, {1, -1, 0}, {1, -1, -1}},
			{{0, -1, 1}, {1, -1, 0}, {1, -1, 1}},
		},
		{
			{{0, -1, 1}, {-1, 0, 1}, {-1, -1, 1}},
			{{0, 1, 1}, {-1, 0, 1}, {-1, 1, 1}},
			{{0, -1, 1}, {1, 0, 1}, {1, -1, 1}},
			{{0, 1, 1}, {1, 0, 1}, {1, 1, 1}},
		},
		{
			{{0, -1, -1}, {-1, 0, -1}, {-1, -1, -1}},
			{{0, -1, -1}, {1, 0, -1}, {1, -1, -1}},
			{{0, 1, -1}, {-1, 0, -1}, {-1, 1, -1}},
			{{0, 1, -1}, {1, 0, -1}, {1, 1, -1}},
		},
	};
	
	protected boolean analyze(int side, int x, int y, int z, VoxelArray a){
		int[] dir = sides[side];
		Voxel v = a.getAt(x + dir[0], y + dir[1], z + dir[2]);
		if(v != null){
			if(v.getClass().equals(this.getClass())){
				return false;
			}
			return v.hasTransparency();
		}else{
			return true;
		}

	}
	
	
	protected void genQuad(VoxelArray a, int side, int x, int y, int z, ArrayList<Vector3f> positions, ArrayList<Vector3f> normals, ArrayList<Vector3f> tangents, ArrayList<Vector3f> texCoords, ArrayList<Integer> indices, ArrayList<Float> ao, ArrayList<Boolean> displacement, WrappedInt vecId){
		int[] quadIndices = new int[4];
		
		int[][] dir = quadDirs[side];
		
		float[] aoVals = new float[4];
		
		for(int i = 0; i < 4; i++){
			
			positions.add(new Vector3f(x+dir[i][0], y+dir[i][1], z+dir[i][2]));
			normals.add(Voxel.normals[side]);
			float[] currTexCoords = quadTexCoords[side][i];
			texCoords.add(new Vector3f(currTexCoords[0], currTexCoords[1], getTextureId(side)));
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
			
			if(renderBackfaces()){
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
			
			if(renderBackfaces()){
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

	public Voxel copy(){
		try {
			return (Voxel) this.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
