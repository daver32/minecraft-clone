package renderEngine.entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Light {
	private Vector3f color, position, viewPosition;
	private float strength;
	
	public Light(Vector3f color, Vector3f position, float strength) {
		this.color = color;
		this.position = position;
		this.strength = strength;
	}	
	
	public void updateViewPosition(Matrix4f viewMatrix){
		Vector4f newViewPos = Matrix4f.transform(viewMatrix, new Vector4f(position.x, position.y, position.z, 1), null);
		viewPosition = new Vector3f(newViewPos.x, newViewPos.y, newViewPos.z);
	}
	
	public Vector3f getViewPosition() {
		return viewPosition;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}
}
