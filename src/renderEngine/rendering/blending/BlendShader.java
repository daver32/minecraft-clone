package renderEngine.rendering.blending;

import renderEngine.rendering.ShaderProgram;

public class BlendShader extends ShaderProgram {

	private final static String VERTEX_FILE = "src/renderEngine/rendering/blending/BlendVertex.glsl";
	private final static String FRAGMENT_FILE = "src/renderEngine/rendering/blending/BlendFragment.glsl";
	
	private int l_multiplier;
	
	public BlendShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		l_multiplier = super.getUniformLocation("multiplier");
	}
	
	public void loadMultiplier(float m){
		super.loadFloat(l_multiplier, m);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void prepare() {

	}
}
