package renderEngine.rendering.envMap;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.rendering.ShaderProgram;

public class EnvMapShader extends ShaderProgram{

	private final static String PATH = "src/renderEngine/rendering/envMap/";
	private final static String VERTEX_FILE = "EnvMapVertex.glsl";
	private final static String FRAGMENT_FILE = "EnvMapFragment.glsl";
	
	private int l_transformationMatrix;
	private int l_viewMatrix;
	private int l_projectionMatrix;
	private int l_translation;
	
	private int l_albedoMap;
	
	public EnvMapShader() {
		super(PATH + VERTEX_FILE, PATH + FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		l_transformationMatrix = super.getUniformLocation("transformationMatrix");
		l_viewMatrix = super.getUniformLocation("viewMatrix");
		l_projectionMatrix = super.getUniformLocation("projectionMatrix");
		l_translation = super.getUniformLocation("translation");
		
		l_albedoMap = super.getUniformLocation("albedoMap");
	}

	public void loadTransformationMatrix(Matrix4f m){
		super.loadMatrix(l_transformationMatrix, m);
	}
	
	public void loadViewMatrix(Matrix4f m){
		super.loadMatrix(l_viewMatrix, m);
	}
	
	public void loadProjectionMatrix(Matrix4f m){
		super.loadMatrix(l_projectionMatrix, m);
	}
	
	public void loadTranslation(Vector3f v){
		super.loadVector(l_translation, v);
	}
	
	@Override
	protected void prepare() {
		super.loadInt(l_albedoMap, 0);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
	}

}
