package renderEngine.resources.materialResources;

import org.lwjgl.util.vector.Vector2f;

import renderEngine.resources.textureResources.TextureResources;
import renderEngine.textures.Material;

class MaterialResource {
	private Material material;
	public final String name;
	
	public String diffuseName, normalName, specularName, heightName;
	public float reflectivity, shineDamper, fresnel, refractiveIndex;
	public int transparency;
	public Vector2f uvShift;
	
	public MaterialResource(String n){
		name = n;
	}
	
	public Material getMaterial(){
		if(material == null){
			material = loadMaterial();
		}

		return material;
	}
	
	public void erase(){
		material = null;
	}
	
	private Material loadMaterial(){
		Material r = new Material();
		r.setAlbedoMap(TextureResources.get(diffuseName));
		r.setNormalMap(TextureResources.get(normalName));
		r.setHeightMap(TextureResources.get(heightName));
		
		r.setRefractiveIndex(refractiveIndex);
		
		r.setUvShift(uvShift);

		r.setFresnel(fresnel);
		r.setReflectivity(reflectivity);
		r.setShineDamper(shineDamper);
		
		r.setTransparencyPreset(transparency);
		
		return r;
	}
}
