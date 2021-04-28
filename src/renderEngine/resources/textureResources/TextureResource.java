package renderEngine.resources.textureResources;

import java.io.File;

import renderEngine.textures.Texture;
import renderEngine.tools.TextureLoader;

class TextureResource {
	private Texture texture;
	public final String name;
	private File file;
	
	public TextureResource(File f, String n){
		name = n;
		file = f;
	}
	
	public Texture getTexture(){
		if(texture == null){
			texture = loadTexture(file);
		}
		
		return texture;
	}
	
	public void erase(){
		texture = null;
	}
	
	private static Texture loadTexture(File file){
		try{
			int id = TextureLoader.loadTexture(file);
			return new Texture(id);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
}
