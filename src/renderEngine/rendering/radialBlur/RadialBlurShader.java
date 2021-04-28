package renderEngine.rendering.radialBlur;

import org.lwjgl.util.vector.Vector2f;

import renderEngine.rendering.ShaderProgram;
import renderEngine.rendering.radialBlur.RadialBlurRenderer.RadialBlur;

public class RadialBlurShader extends ShaderProgram{

	private static String vertexFile = "src/renderEngine/rendering/radialBlur/RadialVertex.glsl";
	private static String fragmentFile = "src/renderEngine/rendering/radialBlur/RadialFragment.glsl";
	
	private int location_isOutwards;
	private int location_strength;
	private int location_center;
	
	public RadialBlurShader() {
		super(vertexFile, fragmentFile);
	}

	protected void getAllUniformLocations() {
		location_isOutwards = super.getUniformLocation("isOutwards");
		location_strength = super.getUniformLocation("strength");
		location_center = super.getUniformLocation("center");
	}

	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	public void loadCenter(Vector2f c){
		super.loadVector(location_center, c);
	}
	
	public void loadDirection(RadialBlur d){
		super.loadBoolean(location_isOutwards, d == RadialBlur.OUTWARDS);
	}
	
	public void loadStrength(float s){
		super.loadFloat(location_strength, s);
	}

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}
}
