package renderEngine.resources.materialResources;

import java.io.File;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import renderEngine.resources.FileTools;
import renderEngine.resources.ParameterArray;
import renderEngine.textures.Material;

public class MaterialResources {
	private static ArrayList<MaterialResource> resources;
	
	public static void init(){
		 resources = new ArrayList<MaterialResource>();
		 
		 File srcFolder = new File("res/materials");
		 ArrayList<File> files = FileTools.listAllFiles(srcFolder);
		 
		 for(File f : files){
			 String name = f.getName();
			 
			 if(name.toLowerCase().endsWith(".mat")){
				 ParameterArray a = new ParameterArray();
				 if(a.init(f)){

					 parseParams(a);
				 }
			 }
		 }
	}
	
	private static void parseParams(ParameterArray a){
		String name = a.getStringParameter("name");
		//System.out.println(name);
		if(name == null){
			return;
		}

		MaterialResource res = new MaterialResource(name);
		res.diffuseName = a.getStringParameter("diffuse");
		res.specularName = a.getStringParameter("specular");
		res.normalName = a.getStringParameter("normal");
		res.heightName = a.getStringParameter("height");
		
		res.fresnel = a.getFloatParameter("fresnel");
		res.reflectivity = a.getFloatParameter("reflectivity");
		res.shineDamper = a.getFloatParameter("shineDamper");
		
		res.refractiveIndex = a.getFloatParameter("refractiveIndex");
		
		res.transparency = (int)a.getFloatParameter("transparency");
		
		res.uvShift = new Vector2f();
		res.uvShift.x = a.getFloatParameter("uvShift_x");
		res.uvShift.y = a.getFloatParameter("uvShift_y");

		resources.add(res);
	}
	

	
	public static Material get(String name){
		for(MaterialResource m : resources){
			if(m.name.equals(name)){
				return m.getMaterial();
			}
		}
		return null;
	}
	
	public static void erase(){
		for(MaterialResource m : resources){
			m.erase();
		}
	}
}
