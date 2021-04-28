package renderEngine.tools;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import renderEngine.models.RawModel;

public class Loader {
	
	private static List<Integer> vaos = new ArrayList<Integer>();
	private static List<Integer> vbos = new ArrayList<Integer>();
	private static List<Integer> textures = new ArrayList<Integer>();
	
	public static void addToRemoveTexture(int t){
		textures.add(t);
	}
	
	public static RawModel loadToVAO(float[] positions,float[] textureCoords, float[] normals,int[] indices){
		int vaoID = createVAO(); 

		bindIndicesBuffer(indices);
		storeDataInFloatAttributeList(0,positions,3);
		storeDataInFloatAttributeList(1,textureCoords,2);
		storeDataInFloatAttributeList(2,normals,3);
		unbindVAO();
		return new RawModel(vaoID,indices.length);
	}
	
	public static RawModel loadToVAO(float[] positions,float[] textureCoords, float[] normals,int[] indices, float[] tangents){
		int vaoID = createVAO(); 

		bindIndicesBuffer(indices);
		storeDataInFloatAttributeList(0,positions,3);
		storeDataInFloatAttributeList(1,textureCoords,2);
		storeDataInFloatAttributeList(2,normals,3);
		storeDataInFloatAttributeList(3,tangents,3);
		unbindVAO();
		return new RawModel(vaoID,indices.length);
	}
	
	public static RawModel loadToVAO(float[] positions, int dimensions){
		int vaoID = createVAO();
		storeDataInFloatAttributeList(0,positions,dimensions);
		unbindVAO();
		return new RawModel(vaoID,positions.length/dimensions);
	}
	
	public static RawModel loadToVAO(float[] positions, float[] textureCoords, int dimensions){
		int vaoID = createVAO();
		storeDataInFloatAttributeList(0,positions,dimensions);
		storeDataInFloatAttributeList(1,textureCoords,2);
		unbindVAO();
		return new RawModel(vaoID,positions.length/dimensions);
	}
	
	public static RawModel genQuad(){
		int vaoID = createVAO();
		storeDataInFloatAttributeList(0, new float[]{-1,1,-1,-1,1,1,1,-1}, 2);
		storeDataInFloatAttributeList(1, new float[]{0,1,2,3}, 1);
		unbindVAO();
		return new RawModel(vaoID,4);
	}
	
	public static RawModel genCube(){
		int vaoID = createVAO();
		
		float[] vertices = {			
				-0.5f,0.5f,-0.5f,	
				-0.5f,-0.5f,-0.5f,	
				0.5f,-0.5f,-0.5f,	
				0.5f,0.5f,-0.5f,		
				
				-0.5f,0.5f,0.5f,	
				-0.5f,-0.5f,0.5f,	
				0.5f,-0.5f,0.5f,	
				0.5f,0.5f,0.5f,
				
				0.5f,0.5f,-0.5f,	
				0.5f,-0.5f,-0.5f,	
				0.5f,-0.5f,0.5f,	
				0.5f,0.5f,0.5f,
				
				-0.5f,0.5f,-0.5f,	
				-0.5f,-0.5f,-0.5f,	
				-0.5f,-0.5f,0.5f,	
				-0.5f,0.5f,0.5f,
				
				-0.5f,0.5f,0.5f,
				-0.5f,0.5f,-0.5f,
				0.5f,0.5f,-0.5f,
				0.5f,0.5f,0.5f,
				
				-0.5f,-0.5f,0.5f,
				-0.5f,-0.5f,-0.5f,
				0.5f,-0.5f,-0.5f,
				0.5f,-0.5f,0.5f
		};
		
		float[] textureCoords = {0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,0};
		
		int[] indices = {
				0,1,3,	
				3,1,2,	
				4,5,7,
				7,5,6,
				8,9,11,
				11,9,10,
				12,13,15,
				15,13,14,	
				16,17,19,
				19,17,18,
				20,21,23,
				23,21,22
		};
		
		bindIndicesBuffer(indices);
		storeDataInFloatAttributeList(0, vertices, 3);
		storeDataInFloatAttributeList(1, textureCoords, 2);
		
		return new RawModel(vaoID, indices.length);
	}
	
	
	public static void cleanUp(){
		int totalVaos = 0;
		int totalVbos = 0;
		int totalTextures = 0;
		for(int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
			totalVaos++;
		}
		System.out.println("Loader: Cleared "+totalVaos+" total vaos. ");
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
			totalVbos++;
		}
		System.out.println("Loader: Cleared "+totalVbos+" total vbos. ");
		for(int txt:textures){
			GL11.glDeleteTextures(txt);
			totalTextures++;
		}
		System.out.println("Loader: Cleared "+totalTextures+" total textures. ");
		
		
	}
	
	public static int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	public static void storeDataInFloatAttributeList(int attributeNumber, float[] data, int coordinateSize){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public static void storeDataInIntAttributeList(int attributeNumber, int[] data, int coordinateSize){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_INT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private static void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	private static void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	public static IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public static FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	
}
