package renderEngine.rendering.sky;


import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import renderEngine.entities.Sun;
import renderEngine.framebuffers.GBuffer;
import renderEngine.models.RawModel;
import renderEngine.rendering.Renderer;

public class SkyRenderer extends Renderer{
	private SkyShader shader = new SkyShader();
	private Matrix4f projectionMatrix;
	
	private static final int NUM_ARRAYS = 2;
	
	public SkyRenderer(Matrix4f projectionMatrix){
		this.projectionMatrix = projectionMatrix;
	}
	
	public void render(GBuffer buffer, Matrix4f viewMatrix, int skyboxID, Sun sun) {
		buffer.bind();
		prepare();
		RawModel cube = getCube();
		GL30.glBindVertexArray(cube.getVaoID());
		
		shader.start();
		enableArrays(NUM_ARRAYS);
		shader.loadViewMatrix(viewMatrix);
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadSun(sun);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, skyboxID);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, cube.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

		disableArrays(NUM_ARRAYS);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void prepare(){
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_NORMALIZE);
		GL11.glDisable(GL11.GL_BLEND);

		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		this.clear(0, 0, 0, 0);
		//this.clear(0, 0, 0, 0);
	}
	
	public void cleanUP(){
		shader.cleanUp();
	}
}
