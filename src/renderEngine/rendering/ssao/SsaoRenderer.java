package renderEngine.rendering.ssao;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.models.RawModel;
import renderEngine.rendering.Renderer;

public class SsaoRenderer extends Renderer{
	private SsaoShader shader;
	private static Vector3f[] sampleKernel = null;
	private static final int KERNEL_SIZE = 40;
	
	public SsaoRenderer(){
		shader = new SsaoShader();
		if(sampleKernel == null){
			genKernel();
		}
	}
	
	private static void genKernel(){
		sampleKernel = new Vector3f[KERNEL_SIZE];
		for(int i = 0; i < KERNEL_SIZE; i++){
			Vector3f pt = new Vector3f(randFloat(-1, 1), randFloat(-1, 1), randFloat(0, 1));
			sampleKernel[i] = pt;
			pt.normalise();
			
			float scale = (float)i / KERNEL_SIZE;
			scale = lerp(0.1f, 1f, scale * scale);
			pt.x *= scale;
			pt.y *= scale;
			pt.z *= scale;
			
		}
	}
	
	private static float lerp(float min, float max, float value){
		return min + (max - min) * value;
	}
	
	private static float randFloat(float min, float max){
		return (float)(Math.random() * (max - min) + min);
	}
	
	public void render(int depthTextureID, int normalTextureID, int positionTextureID, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
		shader.start();
		shader.loadKernel(sampleKernel);
		shader.loadViewMatrix(viewMatrix);
		shader.loadProjectionMatrix(projectionMatrix);
		RawModel quad = getQuad();
		GL30.glBindVertexArray(quad.getVaoID());
		enableArrays(1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureID);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalTextureID);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, positionTextureID);
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_NORMALIZE);
		GL11.glEnable(GL11.GL_BLEND);
		this.setBlending(Blend.ALPHA);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		
		disableArrays(1);
		shader.stop();
	}
	
	public void cleanUP(){
		shader.cleanUp();
	}
}
