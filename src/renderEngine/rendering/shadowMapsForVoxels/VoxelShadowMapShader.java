package renderEngine.rendering.shadowMapsForVoxels;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.entities.Sun;
import renderEngine.rendering.ShaderProgram;
import voxelEngine.VoxelVao;

public class VoxelShadowMapShader extends ShaderProgram {

	private final static String PATH = "src/renderEngine/rendering/shadowMapsForVoxels/";
	private final static String VERTEX_FILE = "VoxelShadowMapVertex.glsl";
	private final static String FRAGMENT_FILE = "VoxelShadowMapFragment.glsl";
	private final static String GEOMETRY_FILE = "VoxelShadowMapGeometry.glsl";
	
	private int[] l_pvMatrices;
	private int l_mPos;
	
	private int l_time;
	
	private int[] l_albedoMaps;
	
	private int[] l_cascadeCulls;
	
	public VoxelShadowMapShader() {
		super(PATH + VERTEX_FILE, PATH + FRAGMENT_FILE, PATH + GEOMETRY_FILE);
	}

	protected void getAllUniformLocations() {

		l_mPos = super.getUniformLocation("mPos");
		
		l_time = super.getUniformLocation("time");
		
		l_albedoMaps = new int[VoxelVao.NUM_TEXTURES];
		for(int i = 0; i < VoxelVao.NUM_TEXTURES; i++){
			l_albedoMaps[i] = super.getUniformLocation("albedoMaps[" + i + "]");
		}
		
		l_pvMatrices = new int[Sun.NUM_CASCADES];
		l_cascadeCulls = new int[Sun.NUM_CASCADES];
		for(int i = 0; i < Sun.NUM_CASCADES; i++){
			l_pvMatrices[i] = super.getUniformLocation("pvMatrices[" + i + "]");
			l_cascadeCulls[i] = super.getUniformLocation("cascadeCulls[" + i + "]");
		}

	}

	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
		super.bindAttribute(4, "ambientOcclusion");
		super.bindAttribute(5, "displacement");
	}
	
	public void loadPvMatrices(Matrix4f[] m){
		for(int i = 0; i < Sun.NUM_CASCADES; i++){
			super.loadMatrix(l_pvMatrices[i], m[i]);
		}

	}
	
	public void loadCascadeCulls(boolean[] culls){
		for(int i = 0; i < Sun.NUM_CASCADES; i++){
			super.loadInt(l_cascadeCulls[i], culls[i] ? 1 : 0);
		}

	}
	
	public void loadModelPosition(Vector3f p){
		super.loadVector(l_mPos, p);
	}
	
	protected void prepare() {
		super.loadFloat(l_time, (float) (System.nanoTime()/1e6 % 1e5));
		
		for(int i = 0; i < VoxelVao.NUM_TEXTURES; i++){
			super.loadInt(l_albedoMaps[i], i);
		}
	}


}
