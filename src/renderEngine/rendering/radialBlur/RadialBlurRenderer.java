package renderEngine.rendering.radialBlur;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;

import renderEngine.models.RawModel;
import renderEngine.rendering.Renderer;

public class RadialBlurRenderer extends Renderer{
	private RadialBlurShader shader;

	public enum RadialBlur{INWARDS, OUTWARDS};
	
	public RadialBlurRenderer(){
		shader = new RadialBlurShader();
	}
	
	public void blur(int srcTexture, Vector2f center, RadialBlur direction, float strength) {
		shader.start();

		RawModel quad = getQuad();
		
		shader.loadCenter(center);
		shader.loadDirection(direction);
		shader.loadStrength(strength);
		
		enableArrays(1);
		GL30.glBindVertexArray(quad.getVaoID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, srcTexture);
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_NORMALIZE);
		GL11.glDisable(GL11.GL_BLEND);
		this.setBlending(Blend.ALPHA);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		
		GL30.glBindVertexArray(0);
		disableArrays(1);
		shader.stop();
	}
	
	public void cleanUP(){
		shader.cleanUp();
	}
}
