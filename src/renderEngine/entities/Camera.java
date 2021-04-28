package renderEngine.entities;

import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private Vector3f position, rotation;
	//position : x, y, z
	//rotation : pitch, yaw, roll
	
	public Camera(){
		position = new Vector3f(0,0,0);
		rotation = new Vector3f(0,0,0);
	}
	
	public Camera(Vector3f pos){
		position = pos;
		rotation = new Vector3f(0,0,0);
	}

	public Camera(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	
	public float getPitch(){
		return rotation.x;
	}
	
	public float getYaw(){
		return rotation.y;
	}
	
	public float getRoll(){
		return rotation.z;
	}
	
	public float setPitch(float v){
		return rotation.x = v;
	}
	
	public float setYaw(float v){
		return rotation.y = v;
	}
	
	public float setRoll(float v){
		return rotation.z = v;
	}
	
	public Vector3f calcForwardVec(){
		double yawRads = -Math.toRadians(rotation.y-90);
		double pitchRads = -Math.toRadians(rotation.x);
		
		double xzLen = Math.cos(pitchRads);
		return new Vector3f((float)(xzLen * Math.cos(yawRads)), (float)Math.sin(pitchRads), (float)(xzLen * Math.sin(-yawRads)));
	}
}
