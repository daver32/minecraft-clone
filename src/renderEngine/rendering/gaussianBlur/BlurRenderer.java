package renderEngine.rendering.gaussianBlur;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import renderEngine.models.RawModel;
import renderEngine.rendering.Renderer;

public class BlurRenderer extends Renderer{
	private BlurShader shader;

	
	public BlurRenderer(){
		shader = new BlurShader();
	}
	
	public void blur(int srcTexture, Blur dir, int diameter) {
		shader.start();
		shader.loadDirection(dir);
		shader.loadDiameter(diameter);
		RawModel quad = getQuad();
		
		enableArrays(1);
		GL30.glBindVertexArray(quad.getVaoID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, srcTexture);
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_NORMALIZE);
		GL11.glEnable(GL11.GL_BLEND);
		this.setBlending(Blend.NONE);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		
		GL30.glBindVertexArray(0);
		disableArrays(1);
		shader.stop();
	}
	
	public void cleanUP(){
		shader.cleanUp();
	}
}
