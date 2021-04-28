package renderEngine.resources;

import renderEngine.resources.materialResources.MaterialResources;
import renderEngine.resources.modelResources.ModelResources;
import renderEngine.resources.skyboxResources.SkyboxResources;
import renderEngine.resources.textureResources.TextureResources;

public class ResourceManager {
	public static void init(){
		MaterialResources.init();
		TextureResources.init();
		ModelResources.init();
		SkyboxResources.init();
	}
}
