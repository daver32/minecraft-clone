package voxelEngine;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import vectors.Vec3D;

public class VoxelGeomArray {
	
	private float[] positions;
	private float[] normals;
	private float[] tangents;
	private float[] texCoords;
	private int[] indices;
	private float[] ao;
	private byte[] displ;
	
	public VoxelArray parentArray;
	
	public VoxelGeomArray(
			VoxelArray parentArray, 
			float[] positions, 
			float[] normals, 
			float[] tangents,
			float[] texCoords, 
			float[] ao,
			int[] indices,
			byte[] displ
	){
		if(parentArray == null){
			throw new NullPointerException();
		}
		
		this.positions = positions;
		this.normals = normals;
		this.texCoords = texCoords;
		this.indices = indices;
		this.tangents = tangents;
		this.ao = ao;
		this.displ = displ;
		
		this.parentArray = parentArray;
	}
	
	public VoxelVao convertToVao(){ //graphics thread
		if(positions.length == 0){
			return new VoxelVao(0, null, 0, new Vec3D(parentArray.wPosX, parentArray.wPosY, parentArray.wPosZ));
		}
		
		
		int vaoID = createVAO();
		int[] vboIDs = new int[VoxelVao.NUM_VBOS];
		vboIDs[0] = bindIndicesBuffer(indices);
		vboIDs[1] = storeDataInFloatAttributeList(0, positions, 3);
		vboIDs[4] = storeDataInFloatAttributeList(1, texCoords, 3);
		vboIDs[2] = storeDataInFloatAttributeList(2, normals, 3);
		vboIDs[3] = storeDataInFloatAttributeList(3, tangents, 3);
		vboIDs[5] = storeDataInFloatAttributeList(4, ao, 1);
		vboIDs[6] = storeDataInByteAttributeList(5, displ, 1);
		
		GL30.glBindVertexArray(0);
		
		return new VoxelVao(vaoID, vboIDs, indices.length, new Vec3D(parentArray.wPosX, parentArray.wPosY, parentArray.wPosZ));
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
	
	public static int storeDataInIntAttributeList(int attributeNumber, int[] data, int coordinateSize){
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_INT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}
	
	private static int bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		return vboID;
	}
	
	public static int storeDataInByteAttributeList(int attributeNumber, byte[] data, int coordinateSize){
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		ByteBuffer buffer = storeDataInByteBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		//GL30.glVertexAttribIPointer(attributeNumber, coordinateSize, GL11.GL_UNSIGNED_BYTE, 0, buffer);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_UNSIGNED_BYTE, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
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
	
	public static ByteBuffer storeDataInByteBuffer(byte[] data){
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}

