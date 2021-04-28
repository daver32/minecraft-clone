package renderEngine.rendering.deferred;

import java.util.ArrayList;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.entities.Light;
import renderEngine.entities.Sun;
import renderEngine.rendering.ShaderProgram;

public class DeferredShader extends ShaderProgram {

	private final static String VERTEX_FILE = "src/renderEngine/rendering/deferred/DeferredVertex.glsl";
	private final static String FRAGMENT_FILE = "src/renderEngine/rendering/deferred/DeferredFragment.glsl";
	
	private int l_viewMatrix;
	private int l_projectionMatrix;
	
	private int l_albedoTexture;
	private int l_normalTexture;
	private int l_depthTexture;
	private int l_positionTexture;
	private int l_shineTexture;
	private int l_shadowmapTexture;
	private int l_skybox, l_envMap;
	
	private int l_sunDirection;
	private int l_sunColor;
	
	private int l_camPosition;
	
	private int l_ssmMatrices[];
	private int l_numCascades;
	private int l_drawSunShadows;
	
	private int l_fogBorders;
	
	private static final int MAX_LIGHTS = 300;
	private int[] l_lightPositions, l_lightColors, l_lightStrengths;
	private int l_lightCount;
	
	public DeferredShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		l_viewMatrix = super.getUniformLocation("viewMatrix");
		l_projectionMatrix = super.getUniformLocation("projectionMatrix");
		
		l_albedoTexture = super.getUniformLocation("albedoTexture");
		l_normalTexture = super.getUniformLocation("normalTexture");
		l_depthTexture = super.getUniformLocation("depthTexture");
		l_positionTexture = super.getUniformLocation("positionTexture");
		l_shineTexture = super.getUniformLocation("shineTexture");
		l_skybox = super.getUniformLocation("skybox");
		l_envMap = super.getUniformLocation("envMap");
		l_shadowmapTexture = super.getUniformLocation("shadowmapTexture");
		
		l_drawSunShadows = super.getUniformLocation("drawSunShadows");
		
		l_fogBorders = super.getUniformLocation("fogBorders");
		
		l_camPosition = super.getUniformLocation("camPosition");
		
		l_sunDirection = super.getUniformLocation("sunDirection");
		l_sunColor = super.getUniformLocation("sunColor");
		
		l_ssmMatrices = new int[Sun.NUM_CASCADES];
		for(int i = 0; i < Sun.NUM_CASCADES; i++){
			l_ssmMatrices[i] = super.getUniformLocation("ssmMatrices["+i+"]");
		}
		l_numCascades = super.getUniformLocation("numCascades");
		
		l_lightPositions = new int[MAX_LIGHTS];
		l_lightColors = new int[MAX_LIGHTS];
		l_lightStrengths = new int[MAX_LIGHTS];
		for(int i = 0; i < MAX_LIGHTS; i++){
			l_lightPositions[i] = super.getUniformLocation("lightPositions["+i+"]");
			l_lightColors[i] = super.getUniformLocation("lightColors["+i+"]");
			l_lightStrengths[i] = super.getUniformLocation("lightStrengths["+i+"]");
		}
		
		l_lightCount = super.getUniformLocation("lightCount");
		
		
	}
	
	public void loadLights(ArrayList<Light> lights, Matrix4f vm){
		int i = 0;
		for(Light light : lights){
			if(i == MAX_LIGHTS){
				break;
			}
		
			light.updateViewPosition(vm);
			
			super.loadVector(l_lightPositions[i], light.getViewPosition());
			super.loadVector(l_lightColors[i], light.getColor());
			super.loadFloat(l_lightStrengths[i], light.getStrength());
			i++;
		}
		super.loadInt(l_lightCount, i);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(l_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Matrix4f viewMatrix){
		super.loadMatrix(l_viewMatrix, viewMatrix);
	}
	
	public void loadCamPos(Vector3f p){
		super.loadVector(l_camPosition, p);
	}
	
	public void loadFogBorders(Vector2f b){
		if(b != null)super.loadVector(l_fogBorders, b);
	}
	
	public void loadSun(Sun sun){
		if(sun != null){
			super.loadVector(l_sunColor, sun.getRenderColor());
			super.loadVector(l_sunDirection, sun.direction.normalise(null));
		}
	}
	
	public void loadSsmMatrices(Matrix4f[] m){
		if(m != null){
			super.loadBoolean(l_drawSunShadows, true);
			for(int i = 0; i < Sun.NUM_CASCADES; i++){
				super.loadMatrix(l_ssmMatrices[i], m[i]);
			}
		}else{
			super.loadBoolean(l_drawSunShadows, false);
		}

	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void prepare() {
		super.loadInt(l_albedoTexture, 0);
		super.loadInt(l_normalTexture, 1);
		super.loadInt(l_depthTexture, 2);
		super.loadInt(l_positionTexture, 3);
		super.loadInt(l_shineTexture, 4);
		super.loadInt(l_skybox, 5);
		super.loadInt(l_envMap, 6);
		super.loadInt(l_shadowmapTexture, 7);
		
		super.loadInt(l_numCascades, Sun.NUM_CASCADES);
	}
}
