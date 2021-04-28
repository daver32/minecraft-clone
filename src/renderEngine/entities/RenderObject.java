package renderEngine.entities;

import org.lwjgl.util.vector.Vector3f;

public abstract class RenderObject {
	protected Vector3f position;
	public double distance;
	public boolean rendered;

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
}
