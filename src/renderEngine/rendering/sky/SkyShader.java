package renderEngine.rendering.sky;


import org.lwjgl.util.vector.Matrix4f;

import renderEngine.entities.Sun;
import renderEngine.rendering.ShaderProgram;

public class SkyShader extends ShaderProgram {

	private final static String PATH = "src/renderEngine/rendering/sky/";
	private final static String VERTEX_FILE = "SkyVertex.glsl";
	private final static String FRAGMENT_FILE = "SkyFragment.glsl";
	
	private int l_viewMatrix;
	private int l_projectionMatrix;
	private int l_skybox;
	
	private int l_sunDirection;
	private int l_sunColor;

	public SkyShader() {
		super(PATH + VERTEX_FILE, PATH + FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		l_viewMatrix = super.getUniformLocation("viewMatrix");
		l_projectionMatrix = super.getUniformLocation("projectionMatrix");
		l_skybox = super.getUniformLocation("skybox");
		l_sunDirection = super.getUniformLocation("sunDirection");
		l_sunColor = super.getUniformLocation("sunColor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(l_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Matrix4f viewMatrix){
		super.loadMatrix(l_viewMatrix, viewMatrix);
	}
	
	public void loadSun(Sun s){
		super.loadVector(l_sunColor, s.color);
		super.loadVector(l_sunDirection, s.direction);
	}

	@Override
	protected void prepare() {
		super.loadInt(l_skybox, 0);
	}	
}
