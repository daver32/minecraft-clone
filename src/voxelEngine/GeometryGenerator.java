package voxelEngine;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;


public class GeometryGenerator {
	
	static VoxelGeomArray convertToVaoArray(VoxelArray a){
		ArrayList<Vector3f> positions = new ArrayList<Vector3f>();
		ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
		ArrayList<Vector3f> texCoords = new ArrayList<Vector3f>();
		ArrayList<Vector3f> tangents = new ArrayList<Vector3f>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Float> ao = new ArrayList<Float>();
		ArrayList<Boolean> displacement = new ArrayList<Boolean>();
		
		WrappedInt vecId = new WrappedInt();
		vecId.value = 0;
		
		for(int x = 0; x < a.width; x++){
			for(int y = 0; y < a.height; y++){
				for(int z = 0; z < a.depth; z++){
					Voxel vox = a.voxels[x][y][z];
					if(vox != null){
						for(int side = 0; side < 6; side++){
							if(vox.analyze(side, x, y, z, a)){
								vox.genQuad(a, side, x, y, z, positions, normals, tangents, texCoords, indices, ao, displacement, vecId);
							}
						}
					}
				}
			}
		}
		return new VoxelGeomArray(
				a, 
				decomposeVec3Array(positions), 
				decomposeVec3Array(normals), 
				decomposeVec3Array(tangents), 
				decomposeVec3Array(texCoords), 
				decomposeFloatArray(ao), 
				decomposeIntArray(indices), 
				decomposeBoolArray(displacement)
		);
	}
	
	
	private static float[] decomposeVec3Array(ArrayList<Vector3f> a){
		float[] res = new float[a.size()*3];
		int i = 0;
		for(Vector3f vec : a){
			res[i++] = vec.x;
			res[i++] = vec.y;
			res[i++] = vec.z;
		}
		return res;
	}
	
	private static int[] decomposeIntArray(ArrayList<Integer> a){
		int[] res = new int[a.size()];
		int i = 0;
		for(int n : a){
			res[i++] = n;
		}
		return res;
	}
	
	private static float[] decomposeFloatArray(ArrayList<Float> a){
		float[] res = new float[a.size()];
		int i = 0;
		for(float n : a){
			res[i++] = n;
		}
		return res;
	}
	
	private static byte[] decomposeBoolArray(ArrayList<Boolean> a){
		byte[] res = new byte[a.size()];
		int i = 0;
		for(boolean b : a){
			res[i++] = (byte) (b ? 1 : 0);
		}
		return res;
	}
	
	public static class WrappedInt{
		public int value = 0;
	}
}
