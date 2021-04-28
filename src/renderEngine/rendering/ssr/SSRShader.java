package renderEngine.rendering.ssr;


import org.lwjgl.util.vector.Matrix4f;

import renderEngine.rendering.ShaderProgram;

public class SSRShader extends ShaderProgram {

	private final static String VERTEX_FILE = "src/renderEngine/rendering/ssr/SSRVertex.glsl";
	private final static String FRAGMENT_FILE = "src/renderEngine/rendering/ssr/SSRFragment.glsl";
	
	private int l_viewMatrix;
	private int l_projectionMatrix;
	
	private int l_diffuseTexture;
	private int l_normalTexture;
	private int l_depthTexture;
	private int l_shineTexture;
	private int l_skybox, l_envMap;
	
	public SSRShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		l_viewMatrix = super.getUniformLocation("viewMatrix");
		l_projectionMatrix = super.getUniformLocation("projectionMatrix");
		
		l_diffuseTexture = super.getUniformLocation("diffuseTexture");
		l_normalTexture = super.getUniformLocation("normalTexture");
		l_depthTexture = super.getUniformLocation("depthTexture");
		l_shineTexture = super.getUniformLocation("shineTexture");
		l_skybox = super.getUniformLocation("skybox");
		l_envMap = super.getUniformLocation("envMap");
	}
	

	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(l_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Matrix4f viewMatrix){
		super.loadMatrix(l_viewMatrix, viewMatrix);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void prepare() {
		super.loadInt(l_diffuseTexture, 0);
		super.loadInt(l_normalTexture, 1);
		super.loadInt(l_depthTexture, 2);
		super.loadInt(l_shineTexture, 3);
		super.loadInt(l_skybox, 4);
		super.loadInt(l_envMap, 5);
	}
}
