package renderEngine.rendering.entities;


import org.lwjgl.util.vector.Matrix4f;

import renderEngine.entities.Camera;
import renderEngine.rendering.ShaderProgram;
import renderEngine.textures.Material;

public class EntityShader extends ShaderProgram {

	private final static String VERTEX_FILE = "src/renderEngine/rendering/entities/EntityVertex.glsl";
	private final static String FRAGMENT_FILE = "src/renderEngine/rendering/entities/EntityFragment.glsl";
	
	private int l_transformationMatrix;
	private int l_viewMatrix;
	private int l_projectionMatrix;
	
	private int l_albedoMap;
	private int l_heightMap;
	private int l_normalMap;
	
	private int l_transparency;
	
	private int l_cameraPosition;
	
	private int l_reflectivity;
	private int l_shineDamper;
	private int l_fresnel;
	private int l_refractiveIndex;
	
	private int l_useNormalMaps;
	private int l_useParallaxMaping;
	
	
	
	public EntityShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		l_transformationMatrix = super.getUniformLocation("transformationMatrix");
		l_viewMatrix = super.getUniformLocation("viewMatrix");
		l_projectionMatrix = super.getUniformLocation("projectionMatrix");
		
		l_albedoMap = super.getUniformLocation("albedoMap");
		l_normalMap = super.getUniformLocation("normalMap");
		l_heightMap = super.getUniformLocation("heightMap");
		
		l_transparency = super.getUniformLocation("transparency");
		
		l_cameraPosition = super.getUniformLocation("cameraPosition");
		
		l_reflectivity = super.getUniformLocation("reflectivity");
		l_shineDamper = super.getUniformLocation("shineDamper");
		l_fresnel = super.getUniformLocation("fresnel");
		l_refractiveIndex = super.getUniformLocation("refractiveIndex");
		
		l_useNormalMaps = super.getUniformLocation("useNormalMaps");
		l_useParallaxMaping = super.getUniformLocation("useParallaxMaping");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
		
		super.bindFragOutput(0, "out_Diffuse");
		super.bindFragOutput(1, "out_Normal");
		super.bindFragOutput(2, "out_Position");
	}
	
	public void loadTextureUsage(boolean depth, boolean normal){
		super.loadBoolean(l_useParallaxMaping, depth);
		super.loadBoolean(l_useNormalMaps, normal);
	}
	
	public void loadShineVars(Material m){
		super.loadFloat(l_reflectivity, m.getReflectivity());
		super.loadFloat(l_fresnel, m.getFresnel());
		super.loadFloat(l_shineDamper, m.getShineDamper());
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(l_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(l_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Matrix4f viewMatrix){
		super.loadMatrix(l_viewMatrix, viewMatrix);
	}	
	
	public void loadCameraPosition(Camera camera){
		super.loadVector(l_cameraPosition, camera.getPosition());
	}
	
	public void loadTransparency(boolean b){
		super.loadBoolean(l_transparency, b);
	}
	
	public void loadRefractiveIndex(float i){
		super.loadFloat(l_refractiveIndex, i);
	}
	
	protected void prepare() {
		super.loadInt(l_albedoMap, 0); 
		super.loadInt(l_normalMap, 1); 
		super.loadInt(l_heightMap, 2); 
	}
}
