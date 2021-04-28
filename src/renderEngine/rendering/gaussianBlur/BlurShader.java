package renderEngine.rendering.gaussianBlur;

import renderEngine.rendering.ShaderProgram;

public class BlurShader extends ShaderProgram{

	private static String vertexFile = "src/renderEngine/rendering/gaussianBlur/GaussianVertex.glsl";
	private static String fragmentFile = "src/renderEngine/rendering/gaussianBlur/GaussianFragment.glsl";
	
	private int location_isHorizontal;
	private int location_diameter;
	
	public BlurShader() {
		super(vertexFile, fragmentFile);
	}

	protected void getAllUniformLocations() {
		location_isHorizontal = super.getUniformLocation("isHorizontal");
		location_diameter = super.getUniformLocation("diameter");
	}

	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	public void loadDirection(Blur d){
		super.loadBoolean(location_isHorizontal, d == Blur.HORIZONTAL);
	}
	
	public void loadDiameter(int r){
		super.loadInt(location_diameter, r);
	}

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}
}
