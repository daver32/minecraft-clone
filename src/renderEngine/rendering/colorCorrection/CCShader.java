package renderEngine.rendering.colorCorrection;

import renderEngine.rendering.ShaderProgram;

public class CCShader extends ShaderProgram {

	private final static String PATH = "src/renderEngine/rendering/colorCorrection/";
	
	private final static String VERTEX_FILE = "CCVertex.glsl";
	private final static String FRAGMENT_FILE = "CCFragment.glsl";
	
	public CCShader() {
		super(PATH + VERTEX_FILE, PATH + FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void prepare() {

	}
}
