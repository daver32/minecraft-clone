package voxelEngine;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.textures.Material;
import vectors.Vec3D;
import vectors.Vec4D;

public class VoxelVao {
	public static final int NUM_VBOS = 7;
	public static final int NUM_TEXTURES = 15;
	
	private static int index = 0;
	public static int[] albedoIds = new int[NUM_TEXTURES];
	public static int[] normalIds = new int[NUM_TEXTURES];
	public static Vector2f[] uvShifts = new Vector2f[NUM_TEXTURES];
	public static Vector3f[] shineInfos = new Vector3f[NUM_TEXTURES];
	public final int vaoID, vertexCount;
	private int[] vboIds;
	private Vector3f offsetPosition;
	private Vec3D position;
	
	public final long timeCreated;
	
	private Vec4D[] frustumCullPositions;
	
	public VoxelVao(int vaoID, int[] vboIds, int vertCount, Vec3D position) {
		this.vaoID = vaoID;
		vertexCount = vertCount;
		this.position = position;
		this.vboIds = vboIds;
		if(vertexCount != 0){
			updateFrustumCullPositions();
		}
		timeCreated = System.currentTimeMillis();
	}

	public void updateOffsetPosition(Vec3D centerPos){
		Vec3D np = new Vec3D(position);
		np.sub(centerPos);
		offsetPosition = np.getVector3f();
	}
	
	public Vector3f getPosition() {
		return offsetPosition;
	}

	public void setPosition(Vector3f position) {
		this.offsetPosition = position;
		updateFrustumCullPositions();
	}
	
	public static void addTexture(Material m){
		albedoIds[index] = m.getAlbedoMap().getID();
		try{
			normalIds[index] = m.getNormalMap().getID();
		}catch(NullPointerException e){}
		uvShifts[index] = m.getUvShift();
		shineInfos[index] = new Vector3f(m.getReflectivity(), m.getShineDamper(), m.getFresnel());
		index++;
	}
	
	public void delete(){ //graphics thread
		if(vaoID != 0){
			GL30.glDeleteVertexArrays(vaoID);
			for(int id : vboIds){
				GL15.glDeleteBuffers(id);
			}
		}
	}
	
	private void updateFrustumCullPositions(){
		if(frustumCullPositions == null){
			frustumCullPositions = new Vec4D[8];
		}
		frustumCullPositions[0] = new Vec4D(position.x, position.y, position.z, 1);
		frustumCullPositions[7] = new Vec4D(position.x + Chunk.WIDTH, position.y + Chunk.WIDTH, position.z + Chunk.WIDTH, 1);
		frustumCullPositions[1] = new Vec4D(position.x, frustumCullPositions[7].y, position.z, 1);
		frustumCullPositions[2] = new Vec4D(position.x, position.y, frustumCullPositions[7].z, 1);
		frustumCullPositions[3] = new Vec4D(position.x, frustumCullPositions[7].y, frustumCullPositions[7].z, 1);
		frustumCullPositions[4] = new Vec4D(frustumCullPositions[7].x, position.y, position.z, 1);
		frustumCullPositions[5] = new Vec4D(frustumCullPositions[7].x, frustumCullPositions[7].y, position.z, 1);
		frustumCullPositions[6] = new Vec4D(frustumCullPositions[7].x, position.y, frustumCullPositions[7].z, 1);
	}
	
	public boolean frustumCull(Matrix4f pvm, Vec3D camPos){
		
		if(frustumCullPositions == null){
			return false;
		}
		
		float o = 1.2f;
		
		for(Vec4D pos : frustumCullPositions){
			if(camPos != null && Math.sqrt(Math.pow(pos.x - camPos.x, 2) + Math.pow(pos.y - camPos.y, 2) + Math.pow(pos.z - camPos.z, 2)) < Chunk.WIDTH){
				return true;
			}
			
			Vec4D relPos = pos.clone();
			relPos.x -= camPos.x; relPos.y -= camPos.y; relPos.z -= camPos.z;
			
			
			Vector4f ssPos = Matrix4f.transform(pvm, relPos.getVector4f(), null);
			ssPos.x /= ssPos.w;
			ssPos.y /= ssPos.w;
			ssPos.z /= ssPos.w;
			if(ssPos.x > -o && ssPos.x < o && ssPos.y > -o && ssPos.y < o && ssPos.w > -0.2){
				return true;
			}
		}
		
		return false;
	}
}
