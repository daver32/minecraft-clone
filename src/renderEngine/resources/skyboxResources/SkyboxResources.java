package renderEngine.resources.skyboxResources;

import java.io.File;
import java.util.ArrayList;

import renderEngine.textures.Cubemap;

public class SkyboxResources {
	private static ArrayList<SkyboxResource> resources;
	
	public static void init(){
		resources = new ArrayList<SkyboxResource>();
		
		File srcFolder = new File("res/skyboxes");
		
		File[] list = srcFolder.listFiles();
		for(File f : list){
			if(f.isDirectory()){
				add(f);
			}
		}
	}
	
	public static Cubemap get(String name){
		for(SkyboxResource r : resources){
			if(r.name.equals(name)){
				return r.getSkybox();
			}
		}
		
		return null;
	}
	
	private static void add(File src){
		File[] list = src.listFiles();
		boolean[] sides = new boolean[6];
		String[] sideNames = new String[]{"right", "left", "top", "bottom", "front", "back"};
		
		for(File f : list){
			String name = f.getName().toLowerCase();
			for(int i = 0; i < 6; i++){
				if(name.endsWith("_" + sideNames[i] + ".png")){
					sides[i] = true;
					break;
				}
			}
		}
		
		boolean allSidesLoaded = true;
		for(boolean side : sides){
			allSidesLoaded = allSidesLoaded && side;
		}
		
		
		if(allSidesLoaded){
			SkyboxResource r = new SkyboxResource(src.getName());
			r.folder = src;
			resources.add(r);
		}
	}
	
	
}
