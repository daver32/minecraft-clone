package voxelGame.blocks;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import voxelEngine.GeometryGenerator.WrappedInt;
import voxelEngine.Voxel;
import voxelEngine.VoxelArray;
import voxelEngine.World;
import voxelGame.ParticleEmitter;
import voxelGame.entities.Entity;

public class TallGrassBlock extends Block{
	
	public String getName(){
		return "tall grass block";
	}
	
	private static final float RAND_DISPL_RANGE = 0.3f;
	private float displX, displZ, height;
	
	
	private static final float e = -0.1f; //elevation
	private static final float[][][] quadDirs = {
			{{1, 1, 1}, {1, e, 1}, {0, 1, 0}, {0, e, 0}},
			{{1, 1, 0}, {1, e, 0}, {0, 1, 1}, {0, e, 1}},
			{{1, 1, 1}, {0, 1, 0}, {1, e, 1}, {0, e, 0}},
			{{1, 1, 0}, {0, 1, 1}, {1, e, 0}, {0, e, 1}},
		};
	
	/*private static Vector3f[] quadNormals = {
		new Vector3f(-1, 0, 1),
		new Vector3f(1, 0, 1),
		new Vector3f(1, 0, -1),
		new Vector3f(-1, 0, -1),
	};*/
	
	private static Vector3f[] quadNormals = {
		new Vector3f(-1, 0, 1),
		new Vector3f(1, 0, 1),
		new Vector3f(1, 0, -1),
		new Vector3f(-1, 0, -1),
	};
	
	private static Vector3f[] quadTangents = {
		new Vector3f(1, 0, 1),
		new Vector3f(-1, 0, 1),
		new Vector3f(-1, 0, -1),
		new Vector3f(1, 0, -1),
	};

	
	private static int[][][] quadTexCoords = {
		{{1, 0}, {1, 1}, {0, 0}, {0, 1}},
		{{1, 0}, {1, 1}, {0, 0}, {0, 1}},
		{{0, 0}, {1, 0}, {0, 1}, {1, 1}},
		{{0, 0}, {1, 0}, {0, 1}, {1, 1}},
	};

	
	public TallGrassBlock() {
		super();
		displX = randFloat(-RAND_DISPL_RANGE, RAND_DISPL_RANGE);
		displZ = randFloat(-RAND_DISPL_RANGE, RAND_DISPL_RANGE);
		height = (float) (0.8+Math.random());
	}
	
	protected int getTextureId(int side){
		return 4;
	}
	
	public boolean isSolid(){
		return false;
	}
	
	protected boolean usesDisplacement(){
		return true;
	}
	
	public boolean hasTransparency(){
		return true;
	}
	
	public boolean usesAO(){
		return false;
	}
	
	protected void update(World w, int x, int y, int z, VoxelArray arr, boolean priority){
		/*Grass destroys itself if there's nothing to support it or if there's a block right above it.*/
		Voxel under = w.getAt(x, y-1, z);
		if(!(under instanceof GrassBlock || under instanceof DirtBlock)){
			w.setAt(null, x, y, z, null, true, priority);
			return;
		}
		
		Voxel above = w.getAt(x, y+1, z);
		if(above != null){
			w.setAt(null, x, y, z, null, true, priority);
		}
	}
	
	protected void genQuad(VoxelArray a, int side, int x, int y, int z, ArrayList<Vector3f> positions, ArrayList<Vector3f> normals, ArrayList<Vector3f> tangents, ArrayList<Vector3f> texCoords, ArrayList<Integer> indices, ArrayList<Float> ao, ArrayList<Boolean> displacement, WrappedInt vecId){
		
		int[] quadIndices = new int[4];
		
		if(side < 4){
			float[][] dir = quadDirs[side];
			for(int i = 0; i < 4; i++){
				positions.add(new Vector3f(x + dir[i][0] + displX, y + dir[i][1] * height, z + dir[i][2] + displZ));
				//normals.add(quadNormals[side]); disabled due to self shadowing artifacts
				normals.add(new Vector3f());
				texCoords.add(new Vector3f(quadTexCoords[side][i][0], quadTexCoords[side][i][1], getTextureId(side)));
				ao.add(1f);
				displacement.add(dir[i][1] == 1);
				quadIndices[i] = vecId.value*4+i;
				tangents.add(quadTangents[side]);
			}
			

			
			indices.add(quadIndices[0]);
			indices.add(quadIndices[2]);
			indices.add(quadIndices[1]);
			
			indices.add(quadIndices[3]);
			indices.add(quadIndices[1]);
			indices.add(quadIndices[2]);
			
			vecId.value += 1;
		}
		
		

	}
	
	protected boolean analyze(int side, int x, int y, int z, VoxelArray a){
		return true;
	}
	
	private float randFloat(float min, float max){
		return (float) (min + Math.random() * (max - min));
	}
	
	public void onDestroy(Entity causer, World w, int x, int y, int z){
		ParticleEmitter.emitBlockDestroy(
				causer, 
				x, y, z, 
				new Vector3f(0.33f, 0.18f, 0.09f), 0.025f, 
				0, 0.005f, 
				15, 30, 
				0.1f, 0.2f, 
				1000, 3000
		);
	}
}
