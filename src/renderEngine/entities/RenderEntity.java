package renderEngine.entities;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.models.TexturedModel;
import renderEngine.resources.materialResources.MaterialResources;
import renderEngine.resources.modelResources.ModelResources;

public class RenderEntity extends RenderObject{
	private TexturedModel model;
	private Vector3f position, rotation;
	private float scale;
	
	private boolean castsShadow = true;
	
	public RenderEntity(TexturedModel model){
		position = new Vector3f(0,0,0);
		rotation = new Vector3f(0,0,0);
		scale = 1;
		this.model = model;
	}
	
	public RenderEntity(TexturedModel model, Vector3f pos, float rx, float ry, float rz, float scale){
		this.position = pos;
		this.rotation = new Vector3f(rx, ry, rz);
		this.scale = scale;
		this.model = model;
	}
	
	public RenderEntity(TexturedModel model, Vector3f pos){
		this.position = pos;
		this.scale = 1;
		this.model = model;
		this.rotation = new Vector3f(0, 0, 0);
	}
	
	public RenderEntity(String modelName, String materialName){
		model = new TexturedModel(ModelResources.get(modelName), MaterialResources.get(materialName));
		position = new Vector3f(0,0,0);
		rotation = new Vector3f(0,0,0);
		scale = 1;
	}

	public TexturedModel getTexturedModel() {
		return model;
	}

	public void setTexturedModel(TexturedModel model) {
		this.model = model;
	}

	
	public Vector3f getPosition() {
		return position;
	}

	public float getScale() {
		return scale;
	}

	public float getRotX() {
		return rotation.x;
	}
	
	public float getRotY() {
		return rotation.y;
	}

	public float getRotZ() {
		return rotation.z;
	}

	public void setPos(Vector3f pos) {
		this.position = pos;
	}

	public void setRotX(float v) {
		rotation.x = v;
	}

	public void setRotY(float v) {
		rotation.y = v;
	}

	public void setRotZ(float v) {
		rotation.z = v;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public boolean castsShadow() {
		return castsShadow;
	}

	public void setCastsShadow(boolean castsShadow) {
		this.castsShadow = castsShadow;
	}

	

	
}
