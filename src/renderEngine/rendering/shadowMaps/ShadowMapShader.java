package renderEngine.rendering.shadowMaps;

import org.lwjgl.util.vector.Matrix4f;

import renderEngine.entities.Sun;
import renderEngine.rendering.ShaderProgram;

public class ShadowMapShader extends ShaderProgram {

	private final static String PATH = "src/renderEngine/rendering/shadowMaps/";
	private final static String VERTEX_FILE = "ShadowMapVertex.glsl";
	private final static String FRAGMENT_FILE = "ShadowMapFragment.glsl";
	private final static String GEOMETRY_FILE = "ShadowMapGeometry.glsl";
	
	private int[] l_pvMatrices;
	private int l_mMatrix;
	
	public ShadowMapShader() {
		super(PATH + VERTEX_FILE, PATH + FRAGMENT_FILE, PATH + GEOMETRY_FILE);
	}

	protected void getAllUniformLocations() {
		l_pvMatrices = new int[Sun.NUM_CASCADES];
		for(int i = 0; i < Sun.NUM_CASCADES; i++){
			l_pvMatrices[i] = super.getUniformLocation("pvMatrices[" + i + "]");
		}
		l_mMatrix = super.getUniformLocation("mMatrix");
	}

	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "coords");
	}
	
	public void loadPvMatrices(Matrix4f[] m){
		for(int i = 0; i < Sun.NUM_CASCADES; i++){
			super.loadMatrix(l_pvMatrices[i], m[i]);
		}

	}
	
	public void loadModelMatrix(Matrix4f matrix){
		super.loadMatrix(l_mMatrix, matrix);
	}
	
	protected void prepare() {
	}


}
