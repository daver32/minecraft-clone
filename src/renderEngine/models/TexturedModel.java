package renderEngine.models;

import renderEngine.textures.Material;



public class TexturedModel{
	private RawModel model;
	private Material material;

	
	public TexturedModel(int vaoID, int vertexCount) {
		model = new RawModel(vaoID, vertexCount);
	}
	
	public TexturedModel(RawModel m) {
		model = m;
	}
	
	public TexturedModel(RawModel m, Material t) {
		model = m;
		material = t;
	}

	public RawModel getModel() {
		return model;
	}

	public void setModel(RawModel model) {
		this.model = model;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
	
	
	
	
}