package voxelGame.blocks;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import voxelEngine.GeometryGenerator.WrappedInt;
import voxelEngine.Voxel;
import voxelEngine.VoxelArray;
import voxelEngine.World;

public class WaterBlock extends Block{

	public float waterLevel;
	public long lastUpdate;

	public String getName(){
		return "water block";
	}
	
	public WaterBlock() {
		super();
		waterLevel = 1;
	}
	
	@SuppressWarnings("unused")
	private static final int[][] flowDirs = {
		{1, 0}, {-1, 0}, {0, 1}, {0, -1}
	};
	
	public WaterBlock(float waterLevel) {
		super();
		this.waterLevel = waterLevel;
	}
	
	protected int getTextureId(int side){
		return 5;
	}
	
	public boolean isSolid(){
		return false;
	}
	
	public boolean hasTransparency(){
		return true;
	}
	
	protected boolean usesDisplacement(){
		return false;
	}
	
	protected void update(World w, int x, int y, int z, boolean priority){

	}
	
	public boolean hasPhysics(){
		return true;
	}
	
	
	protected void genQuad(VoxelArray a, int side, int x, int y, int z, ArrayList<Vector3f> positions, ArrayList<Vector3f> normals, ArrayList<Vector3f> tangents, ArrayList<Vector3f> texCoords, ArrayList<Integer> indices, ArrayList<Float> ao, ArrayList<Boolean> displacement, WrappedInt vecId){
		int[] quadIndices = new int[4];
		
		int[][] dir = quadDirs[side];
		
		float[] aoVals = new float[4];
		
		for(int i = 0; i < 4; i++){
			
			positions.add(new Vector3f(x+dir[i][0], y+dir[i][1] * waterLevel, z+dir[i][2]));
			normals.add(Voxel.normals[side]);
			float[] currTexCoords = quadTexCoords[side][i];
			texCoords.add(new Vector3f(currTexCoords[0], currTexCoords[1], getTextureId(side)));
			tangents.add(Voxel.tangents[side]);
			quadIndices[i] = vecId.value*4+i;
			displacement.add(usesDisplacement());

			int[][] currAoDirs = aoDirs[side][i];
			boolean occluded = false;
			for(int aoDir = 0; aoDir < 3; aoDir++){
				Voxel aoVox = a.getAt(x+currAoDirs[aoDir][0], y+currAoDirs[aoDir][1], z+currAoDirs[aoDir][2]);
				if(aoVox != null && aoVox.isSolid()){
					occluded = true;
					break;
				}
			}
			
			if(occluded){
				ao.add(0.7f);
				aoVals[i] = 0.7f;
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
			
		}else{
			
			indices.add(quadIndices[0]);
			indices.add(quadIndices[2]);
			indices.add(quadIndices[1]);
			
			indices.add(quadIndices[3]);
			indices.add(quadIndices[1]);
			indices.add(quadIndices[2]);
		}
		

		
		vecId.value += 1;
	}
}
