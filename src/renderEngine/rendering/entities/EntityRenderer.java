package renderEngine.rendering.entities;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import renderEngine.entities.Camera;
import renderEngine.entities.RenderEntity;
import renderEngine.framebuffers.GBuffer;
import renderEngine.models.TexturedModel;
import renderEngine.rendering.Renderer;
import renderEngine.rendering.settings.TextureSettings;
import renderEngine.textures.Material;
import renderEngine.textures.Texture;
import renderEngine.tools.Maths;

public class EntityRenderer extends Renderer{
	private EntityShader shader = new EntityShader();
	private Matrix4f projectionMatrix;
	
	private static final int NUM_ARRAYS = 4;
	
	public EntityRenderer(Matrix4f projectionMatrix){
		this.projectionMatrix = projectionMatrix;
	}
	
	public void renderAll(GBuffer gbuffer, ArrayList<ArrayList<RenderEntity>> entityLists, Matrix4f viewMatrix, Camera camera, TextureSettings texSettings){
		gbuffer.bind();
		prepare();
		this.setBlending(Blend.NONE);
		
		for(ArrayList<RenderEntity> entityList : entityLists){
			GL30.glBindVertexArray(entityList.get(0).getTexturedModel().getModel().getVaoID());
			for(RenderEntity entity : entityList){
				render(entity, viewMatrix, camera, texSettings);
			}
		}
		unbindTextures(3);

	}
	
	
	private void render(RenderEntity e, Matrix4f viewMatrix, Camera camera, TextureSettings texSettings) {
		shader.start();
		enableArrays(NUM_ARRAYS);
		shader.loadCameraPosition(camera);
		shader.loadViewMatrix(viewMatrix);
		TexturedModel model = e.getTexturedModel();
		prepareMaterial(model.getMaterial(), texSettings);
		
		boolean transp = model.getMaterial().getTransparency() == 1;
		shader.loadTransparency(transp);
		
		prepareInstance(e);
		
		
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		disableArrays(NUM_ARRAYS);
		shader.stop();
	}
	
	public void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_NORMALIZE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glDepthMask(true);

	}
	
	private void prepareInstance(RenderEntity entity){
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	
	private void prepareMaterial(Material material, TextureSettings texSettings){
		Texture albedo = material.getAlbedoMap();
		Texture normal = material.getNormalMap();
		Texture height = material.getHeightMap();
		shader.loadShineVars(material);
		shader.loadRefractiveIndex(material.getRefractiveIndex());
		
		boolean useNormal = false, useDepth = false;
		
		if(albedo != null){
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, albedo.ID);
		}
		if(texSettings.useNormal && normal != null){
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, normal.ID);
			useNormal = true;
		}
		if(texSettings.useHeight && height != null){
			GL13.glActiveTexture(GL13.GL_TEXTURE2);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, height.ID);
			useDepth = true;
		}
		
		shader.loadTextureUsage(useDepth, useNormal);
	}
	
	public void cleanUP(){
		shader.cleanUp();
	}
}
