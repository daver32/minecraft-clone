package renderEngine.resources.modelResources;

import java.io.File;
import java.util.ArrayList;

import renderEngine.models.RawModel;
import renderEngine.resources.FileTools;

public class ModelResources {
	private static ArrayList<ModelResource> resources;
	
	public static void init(){
		resources = new ArrayList<ModelResource>();
		
		File srcFolder = new File("res/models");
		ArrayList<File> files = FileTools.listAllFiles(srcFolder);
		 
		for(File f : files){
			String name = f.getName();
			if(name.toLowerCase().endsWith(".obj")){
				try{
					ModelResource r = new ModelResource(name.substring(0, name.length()-4));
					r.file = f;
					resources.add(r);
				}catch(Exception e){}
			}
		}
	}
	

	
	public static RawModel get(String name){
		for(ModelResource r : resources){
			if(r.name.equals(name)){
				return r.getModel();
			}
		}
		return null;
	}
}
