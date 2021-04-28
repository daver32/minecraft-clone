package renderEngine.resources.textureResources;

import java.io.File;
import java.util.ArrayList;

import renderEngine.resources.FileTools;
import renderEngine.textures.Texture;


public class TextureResources {
	private static ArrayList<TextureResource> resources;
	
	public static void init(){
		resources = new ArrayList<TextureResource>();
		
		File srcFolder = new File("res/textures");
		ArrayList<File> files = FileTools.listAllFiles(srcFolder);
		 
		for(File f : files){
			String name = f.getName();
			if(name.toLowerCase().endsWith(".png")){
				try{
					TextureResource r = new TextureResource(f, name.substring(0, name.length()-4));
					resources.add(r);
				}catch(Exception e){}
			}
		}
	}
	
	public static void erase(){
		for(TextureResource r : resources){
			r.erase();
		}
	}
	
	public static Texture get(String name){
		if(name == null){
			return null;
		}
	
		for(TextureResource r : resources){
			if(r.name.equals(name)){
				return r.getTexture();
			}
		}
		return null;
	}


	
}
