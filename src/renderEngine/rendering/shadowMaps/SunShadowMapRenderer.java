package renderEngine.rendering.shadowMaps;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.entities.Camera;
import renderEngine.entities.RenderEntity;
import renderEngine.entities.Sun;
import renderEngine.framebuffers.ShadowMapFBO;
import renderEngine.models.TexturedModel;
import renderEngine.rendering.Renderer;
import renderEngine.rendering.shadowMapsForVoxels.VoxelShadowMapShader;
import renderEngine.tools.Maths;
import voxelEngine.VoxelVao;
import voxelGame.threads.LogicThread;

public class SunShadowMapRenderer extends Renderer{
	
	private ShadowMapShader shader;
	private VoxelShadowMapShader vshader;
	private Matrix4f projectionMatrix;

	public SunShadowMapRenderer(Matrix4f projectionMatrix){
		this.projectionMatrix = projectionMatrix;
		shader = new ShadowMapShader();
		vshader = new VoxelShadowMapShader();
	}
	
	public Matrix4f[] renderAll(ShadowMapFBO fbo, Sun s, Camera c, Matrix4f viewMatrix, ArrayList<ArrayList<RenderEntity>> eLists, ArrayList<VoxelVao> voxels, float nPlane, float fPlane){
		
		Matrix4f[] sunPvMatrices = null;
		try{
			sunPvMatrices = s.getSunPVMatrices(projectionMatrix, viewMatrix, c, nPlane, fPlane);
		}catch(NullPointerException e){
			return null;
		}
		
		
		fbo.bind();
		prepare();

		for(ArrayList<RenderEntity> elist : eLists){
			GL30.glBindVertexArray(elist.get(0).getTexturedModel().getModel().getVaoID());
			for(RenderEntity e : elist){
				if(e.castsShadow()){
					Matrix4f transformationMatrix = Maths.createTransformationMatrix(e.getPosition(), e.getRotX(), e.getRotY(), e.getRotZ(), e.getScale());
					enableArrays(4);
					
					TexturedModel m = e.getTexturedModel();
					
					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, m.getMaterial().getAlbedoMap().ID);
					
					
					render(m.getModel().getVertexCount(), transformationMatrix, sunPvMatrices);
					disableArrays(4);
				}

			}
		}
	
		
		for(VoxelVao v : voxels){

			for(int i = 0; i < VoxelVao.NUM_TEXTURES; i++){
				GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, VoxelVao.albedoIds[i]);
			}
			
			if(v.vertexCount != 0){

				boolean render = false;
				boolean[] cascadesToRender = new boolean[Sun.NUM_CASCADES];
				for(int i = 0; i < Sun.NUM_CASCADES; i++){
					cascadesToRender[i] = v.frustumCull(sunPvMatrices[i], LogicThread.player.getPosition());
					render = true;
				}
				
				
				if(render){
					GL30.glBindVertexArray(v.vaoID);
					enableArrays(VoxelVao.NUM_VBOS);

					renderVoxelChunk(v.vertexCount, v.getPosition(), sunPvMatrices, cascadesToRender);
					disableArrays(VoxelVao.NUM_VBOS);
				}

				
			}
		}
			
		GL30.glBindVertexArray(0);

		
		return sunPvMatrices;
	}
	
	private void renderVoxelChunk(int vertexCount, Vector3f position, Matrix4f[] pvMatrices, boolean[] cascadeCulls){
		try{
			vshader.start();

			vshader.loadPvMatrices(pvMatrices);
			vshader.loadModelPosition(position);
			vshader.loadCascadeCulls(cascadeCulls);

			GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

			vshader.stop();
		}catch(Exception e){}

	}
	
	private void render(int vertexCount, Matrix4f mMatrix, Matrix4f[] pvMatrices){
		try{
			shader.start();

			shader.loadPvMatrices(pvMatrices);
			shader.loadModelMatrix(mMatrix);


			GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
			

			shader.stop();
		}catch(Exception e){}

	}

	public void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_FRONT);
		GL11.glEnable(GL11.GL_NORMALIZE);

		GL11.glDepthMask(true);

		GL11.glClearColor(0,0,0,0);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
	}
	
	public void cleanUP(){
		shader.cleanUp();
	}
	
	
}
