package renderEngine.rendering.voxels;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import renderEngine.framebuffers.GBuffer;
import renderEngine.rendering.Renderer;
import renderEngine.tools.Maths;
import voxelEngine.VoxelVao;
import voxelGame.threads.LogicThread;

public class VoxelRenderer extends Renderer{
	private VoxelShader shader = new VoxelShader();
	private Matrix4f projectionMatrix;
	
	private static final int NUM_ARRAYS = 6;
	
	public VoxelRenderer(Matrix4f projectionMatrix){
		this.projectionMatrix = projectionMatrix;
	}
	
	public void renderAll(GBuffer gbuffer, ArrayList<VoxelVao> vaos, Matrix4f viewMatrix){
		gbuffer.bind();
		prepare();
		this.setBlending(Blend.NONE);
		
		Matrix4f pvm = Matrix4f.mul(projectionMatrix, viewMatrix, null);
	
		for(VoxelVao vao : vaos){
			if(vao.vertexCount != 0 && vao.frustumCull(pvm, LogicThread.player.getPosition())){
				GL30.glBindVertexArray(vao.vaoID);
				render(vao, viewMatrix);
			}

		}
		
		unbindTextures(10);

	}
	
	
	private void render(VoxelVao vao, Matrix4f viewMatrix) {
		shader.start();
		enableArrays(NUM_ARRAYS);
		shader.loadViewMatrix(viewMatrix);
		
		prepareInstance(vao);
		
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, vao.vertexCount, GL11.GL_UNSIGNED_INT, 0);
		//GL11.glDrawElements(GL11.GL_POINTS, vao.vertexCount, GL11.GL_UNSIGNED_INT, 0);
		
		disableArrays(NUM_ARRAYS);
		shader.stop();
	}
	
	public void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_NORMALIZE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glDepthMask(true);
		
		for(int i = 0; i < VoxelVao.NUM_TEXTURES; i++){
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, VoxelVao.albedoIds[i]);
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i + VoxelVao.NUM_TEXTURES);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, VoxelVao.normalIds[i]);
		}
	}
	
	private void prepareInstance(VoxelVao vao){
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(vao.getPosition(), 0, 0, 0, 1);
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadTransformationMatrix(transformationMatrix);
	}

	
	public void cleanUP(){
		shader.cleanUp();
	}
}
