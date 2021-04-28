package renderEngine.rendering.colorCorrection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import renderEngine.models.RawModel;
import renderEngine.rendering.Renderer;

public class CCRenderer extends Renderer{
	private CCShader shader;
	
	public CCRenderer(){
		shader = new CCShader();
	}
	
	public void render(int srcTexture) {
		shader.start();

		RawModel quad = getQuad();
		GL30.glBindVertexArray(quad.getVaoID());
		enableArrays(1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, srcTexture);
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_NORMALIZE);
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		
		disableArrays(1);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void cleanUP(){
		shader.cleanUp();
	}
}
