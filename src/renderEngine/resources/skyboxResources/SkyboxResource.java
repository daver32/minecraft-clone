package renderEngine.resources.skyboxResources;

import java.io.File;

import renderEngine.textures.Cubemap;
import renderEngine.tools.TextureLoader;

class SkyboxResource {
	public File folder;
	public final String name;
	private Cubemap sb;
	
	public SkyboxResource(String name) {
		super();
		this.name = name;
	}
	
	public Cubemap getSkybox(){
		if(sb == null){
			loadSkybox();
		}
		return sb;
	}
	
	private void loadSkybox(){
		try{
			sb = TextureLoader.loadSkybox(folder);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
