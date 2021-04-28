package renderEngine.textures;

import org.lwjgl.util.vector.Vector2f;

public class Material {
	
	private Texture albedoMap, normalMap, heightMap;
	private float shineDamper, reflectivity, fresnel, refractiveIndex;
	private int transparencyPreset;
	private Vector2f uvShift;
	/* 0 = no transparency
	 * 1 = refracts envmap
	 * 2 = refracts background
	*/
	
	public Material(){
		
	}

	public Texture getAlbedoMap() {
		return albedoMap;
	}
	public void setAlbedoMap(Texture diffuseMap) {
		this.albedoMap = diffuseMap;
	}
	public Texture getNormalMap() {
		return normalMap;
	}
	public void setNormalMap(Texture normalMap) {
		this.normalMap = normalMap;
	}
	public Texture getHeightMap() {
		return heightMap;
	}
	public void setHeightMap(Texture heightMap) {
		this.heightMap = heightMap;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public float getFresnel() {
		return fresnel;
	}

	public void setFresnel(float fresnel) {
		this.fresnel = fresnel;
	}

	public int getTransparency() {
		return transparencyPreset;
	}

	public void setTransparencyPreset(int transparencyPreset) {
		this.transparencyPreset = transparencyPreset;
	}

	public float getRefractiveIndex() {
		return refractiveIndex;
	}

	public void setRefractiveIndex(float refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}

	public Vector2f getUvShift() {
		return uvShift;
	}

	public void setUvShift(Vector2f uvShift) {
		this.uvShift = uvShift;
	}
	
	
}
