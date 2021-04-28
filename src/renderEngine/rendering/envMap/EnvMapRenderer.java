package renderEngine.rendering.envMap;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.entities.RenderEntity;
import renderEngine.models.RawModel;
import renderEngine.models.TexturedModel;
import renderEngine.rendering.Renderer;
import renderEngine.textures.Cubemap;
import renderEngine.textures.Material;
import renderEngine.textures.Texture;
import renderEngine.tools.Maths;

public class EnvMapRenderer extends Renderer{
	
	private static Matrix4f[] rotMatrices = genRotMatrices();
	private static final int NUM_ARRAYS = 4;
	
	private EnvMapShader shader = new EnvMapShader();
	
	private Matrix4f projectionMatrix = genProjectionMatrix(90, 1000, 0.01f);
	public EnvMapRenderer() {

	}

	public void render(Cubemap target, Vector3f camPos, ArrayList<ArrayList<RenderEntity>> entities){
		//create fbo
		int fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

		//attach depth buffer
		int depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT32, target.SIZE, target.SIZE);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBuffer);

		//indicate that we want to render to the entire face
		GL11.glViewport(0, 0, target.SIZE, target.SIZE);
		
		//render for each face
		prepare();
		Vector3f negativeCamPos = camPos.negate(null);
		for(int i = 0; i < 6; i++){
			Matrix4f viewMatrix = rotMatrices[i];
			
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, target.CUBEMAP_ID, 0);
			this.clear(0, 0, 0, 0);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			
			for(ArrayList<RenderEntity> l : entities){
				
				RawModel m = l.get(0).getTexturedModel().getModel();
				GL30.glBindVertexArray(m.getVaoID());
				
				for(RenderEntity e : l){
					renderEntity(e, viewMatrix, negativeCamPos);
				}
			}
		
		
		
		}
		//unbind stuff
		GL30.glBindVertexArray(0);
		unbindTextures(1);
		
		//stop rendering to fbo
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
		//delete fbo
		GL30.glDeleteRenderbuffers(depthBuffer);
		GL30.glDeleteFramebuffers(fbo);

		
	}
	
	private void renderEntity(RenderEntity e, Matrix4f vm, Vector3f negativeCamPos){
		shader.start();
		enableArrays(NUM_ARRAYS);
		
		TexturedModel tm = e.getTexturedModel();
		Material mat = tm.getMaterial();
		if(mat.getTransparency() != 0){
			return;
		}
		
		shader.start();
		enableArrays(NUM_ARRAYS);
		
		shader.loadViewMatrix(vm);
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadTranslation(negativeCamPos);
		
		prepareMaterial(mat);
		prepareTransMatrix(e);
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, tm.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		disableArrays(NUM_ARRAYS);
		shader.stop();
	}
	
	private void prepare(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_NORMALIZE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glDepthMask(true);

		
		
	}

	
	private void prepareTransMatrix(RenderEntity entity){
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	private void prepareMaterial(Material material){
		Texture albedo = material.getAlbedoMap();
		
		if(albedo != null){
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, albedo.ID);
		}
	}
	
	@Override
	public void cleanUP() {
		shader.cleanUp();
	}
	
	private static Matrix4f[] genRotMatrices(){
		Matrix4f[] res = new Matrix4f[6];
		
		res[0] = genRotMatrix(0, 90, 0,			1, 1, 1);
		res[1] = genRotMatrix(0, -90, 0,		1, 1, 1);
		res[2] = genRotMatrix(-90, 180, 0,		1, 1, 1);
		res[3] = genRotMatrix(90, 180, 0,		1, 1, 1);
		res[4] = genRotMatrix(0, 180, 0,		1, 1, 1);
		res[5] = genRotMatrix(0, 0, 0,			1, 1, 1);
		
		return res;
	}
	
	private static Matrix4f genRotMatrix(float rx, float ry, float rz, float sx, float sy, float sz){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.scale(new Vector3f(sx, sy, sz), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		
		return matrix;
	}
	
	private static Matrix4f genProjectionMatrix(float fov, float farPlane, float nearPlane){
		float aspectRatio = 1;
		float y_scale  = (float)(1f / Math.tan(Math.toRadians(fov/2f)));
		float x_scale = y_scale/aspectRatio;
		float frustumLength = farPlane - nearPlane;
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((farPlane + nearPlane) / frustumLength);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * farPlane * nearPlane) / frustumLength);
		projectionMatrix.m33 = 0;
		
		return projectionMatrix;
	}
	
}
