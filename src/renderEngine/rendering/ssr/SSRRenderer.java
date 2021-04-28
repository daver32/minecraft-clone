package renderEngine.rendering.ssr;


import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import renderEngine.framebuffers.GBuffer;
import renderEngine.framebuffers.PPBuffer;
import renderEngine.models.RawModel;
import renderEngine.rendering.Renderer;

public class SSRRenderer extends Renderer{
	private SSRShader shader;
	
	private Matrix4f projectionMatrix;
	
	public SSRRenderer(Matrix4f projectionMatrix){
		shader = new SSRShader();
		this.projectionMatrix = projectionMatrix;
	}
	
	public void render(GBuffer gbuffer, PPBuffer ppbuffer, Matrix4f viewMatrix, int skybox, int envMap) {
		ppbuffer.bind();
		this.clear(0, 0, 0, 1);
	
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadViewMatrix(viewMatrix);
		
		RawModel quad = getQuad();
		GL30.glBindVertexArray(quad.getVaoID());
		enableArrays(1);
		
		prepareTextures(gbuffer, ppbuffer);
		prepareCubemaps(skybox, envMap);
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_NORMALIZE);
		GL11.glDisable(GL11.GL_BLEND);

		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());

		GL30.glBindVertexArray(0);
		disableArrays(1);
		shader.stop();
	}
	
	private void prepareCubemaps(int skybox, int envmap){
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, skybox);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, envmap);
	}
	
	private void prepareTextures(GBuffer gbuffer, PPBuffer ppbuffer){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ppbuffer.getRawColorMap());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getNormalID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getDepthTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getReflectivityTextureID());
	}
	
	public void cleanUP(){
		shader.cleanUp();
	}
}
