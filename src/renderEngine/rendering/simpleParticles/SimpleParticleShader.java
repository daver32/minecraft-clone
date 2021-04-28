package renderEngine.rendering.simpleParticles;

import org.lwjgl.util.vector.Matrix4f;

import renderEngine.rendering.ShaderProgram;

public class SimpleParticleShader extends ShaderProgram{
	
	private final static String PATH = "src/renderEngine/rendering/simpleParticles/";
	private final static String VERTEX_FILE = "SimpleParticleVertex.glsl";
	private final static String GEOMETRY_FILE = "SimpleParticleGeometry.glsl";
	private final static String FRAGMENT_FILE = "SimpleParticleFragment.glsl";
	
	private int l_viewMatrix;
	private int l_projectionMatrix;
	
	public SimpleParticleShader() {
		super(PATH + VERTEX_FILE, PATH + FRAGMENT_FILE, PATH + GEOMETRY_FILE);
	}

	protected void getAllUniformLocations() {
		l_viewMatrix = super.getUniformLocation("viewMatrix");
		l_projectionMatrix = super.getUniformLocation("projectionMatrix");

	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(l_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Matrix4f viewMatrix){
		super.loadMatrix(l_viewMatrix, viewMatrix);
	}

	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "scale");
		super.bindAttribute(2, "color");
	}
	
	protected void prepare() {
	}
}
