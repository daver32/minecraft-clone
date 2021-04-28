package renderEngine.rendering.deferred;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import renderEngine.entities.Camera;
import renderEngine.entities.Light;
import renderEngine.entities.Sun;
import renderEngine.framebuffers.GBuffer;
import renderEngine.framebuffers.PPBuffer;
import renderEngine.models.RawModel;
import renderEngine.rendering.Renderer;

public class DeferredRenderer extends Renderer{
	private DeferredShader shader;
	
	private Matrix4f projectionMatrix;
	
	public DeferredRenderer(Matrix4f projectionMatrix){
		shader = new DeferredShader();
		this.projectionMatrix = projectionMatrix;
	}
	
	public void render(GBuffer gbuffer, PPBuffer ppbuffer, Matrix4f viewMatrix, ArrayList<Light> lightList, int skybox, int envMap, Sun sun, Camera c, int shadowMapTexture, Matrix4f ssmMatrices[], Vector2f fogBorders) {
		ppbuffer.bind();

	
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadViewMatrix(viewMatrix);
		shader.loadLights(lightList, viewMatrix);
		shader.loadSun(sun);
		shader.loadCamPos(c.getPosition());
		shader.loadFogBorders(fogBorders);
		shader.loadSsmMatrices(ssmMatrices);


		
		RawModel quad = getQuad();
		GL30.glBindVertexArray(quad.getVaoID());
		enableArrays(1);
		
		prepareGBuffer(gbuffer);
		prepareCubemaps(skybox, envMap);
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_NORMALIZE);
		GL11.glDisable(GL11.GL_BLEND);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE7);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapTexture);
		

		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());

		GL30.glBindVertexArray(0);
		disableArrays(1);
		shader.stop();
	}
	
	private void prepareCubemaps(int skybox, int envmap){
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, skybox);
		GL13.glActiveTexture(GL13.GL_TEXTURE6);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, envmap);
	}
	
	private void prepareGBuffer(GBuffer gbuffer){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getAlbedoID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getNormalID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getDepthTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getPositionTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gbuffer.getReflectivityTextureID());

	}
	
	public void cleanUP(){
		shader.cleanUp();
	}
}
