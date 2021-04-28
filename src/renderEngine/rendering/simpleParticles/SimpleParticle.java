package renderEngine.rendering.simpleParticles;

import org.lwjgl.util.vector.Vector3f;

public class SimpleParticle {

	public Vector3f position, color;
	public float scale = 1;
	
	public SimpleParticle(){
		position = color = new Vector3f();
	}

	public SimpleParticle(Vector3f position, Vector3f color, float scale) {
		this.position = position;
		this.color = color;
		this.scale = scale;
	}

	public SimpleParticle(Vector3f position, Vector3f color) {
		this.position = position;
		this.color = color;
	}
}
