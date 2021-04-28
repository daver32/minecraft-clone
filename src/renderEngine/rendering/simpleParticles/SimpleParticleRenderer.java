package renderEngine.rendering.simpleParticles;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.framebuffers.GBuffer;
import renderEngine.rendering.Renderer;

public class SimpleParticleRenderer extends Renderer{
	private SimpleParticleShader shader;
	
	private Matrix4f projectionMatrix;
	
	public SimpleParticleRenderer(Matrix4f projectionMatrix){
		shader = new SimpleParticleShader();
		this.projectionMatrix = projectionMatrix;
	}
	
	public void render(GBuffer buffer, Matrix4f viewMatrix, ArrayList<SimpleParticle> particles) {
		if(particles.size() == 0)return;

		
		buffer.bind();

		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_NORMALIZE);
		GL11.glDisable(GL11.GL_BLEND);
		this.setBlending(Blend.ALPHA);
		GL11.glDepthMask(true);
		

		
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadViewMatrix(viewMatrix);



		int[] arrays = genParticleVao(particles);
		GL30.glBindVertexArray(arrays[0]);
		enableArrays(3);
		
		GL11.glDrawArrays(GL11.GL_POINTS, 0, particles.size());
		//GL31.glDrawArraysInstanced(GL11.GL_POINTS, 0, particles.size(), particles.size());
		
		clearVao(arrays);
		GL30.glBindVertexArray(0);
		disableArrays(3);
		shader.stop();
	}
	
	private static int[] genParticleVao(ArrayList<SimpleParticle> particles){
		ArrayList<Vector3f> positions = new ArrayList<Vector3f>();
		ArrayList<Vector3f> colors = new ArrayList<Vector3f>();
		ArrayList<Float> scales = new ArrayList<Float>();
		
		for(SimpleParticle p : particles){
			positions.add(p.position);
			colors.add(p.color);
			scales.add(p.scale);
		}
		
		float[] fpos = decomposeVec3Array(positions);
		float[] fsca = decomposeFloatArray(scales);
		float[] fcol = decomposeVec3Array(colors);
		
		int[] res = new int[4];
		res[0] = createVAO();
		res[1] = storeDataInFloatAttributeList(0, fpos, 3);
		res[2] = storeDataInFloatAttributeList(1, fsca, 1);
		res[3] = storeDataInFloatAttributeList(2, fcol, 3);
		
		return res;
	}
	
	private static void clearVao(int[] vao){
		GL30.glDeleteVertexArrays(vao[0]);
		for(int i = 1; i < vao.length; i++){
			GL15.glDeleteBuffers(vao[i]);
		}
	}
	
	public static int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	public static int storeDataInFloatAttributeList(int attributeNumber, float[] data, int coordinateSize){
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}
	
	public static FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private static float[] decomposeVec3Array(ArrayList<Vector3f> a){
		float[] res = new float[a.size()*3];
		int i = 0;
		for(Vector3f vec : a){
			res[i++] = vec.x;
			res[i++] = vec.y;
			res[i++] = vec.z;
		}
		return res;
	}
	
	private static float[] decomposeFloatArray(ArrayList<Float> a){
		float[] res = new float[a.size()];
		int i = 0;
		for(float n : a){
			res[i++] = n;
		}
		return res;
	}

	
	@Override
	public void cleanUP() {
		shader.cleanUp();
	}
}
