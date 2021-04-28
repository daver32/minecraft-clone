package renderEngine.resources.modelResources;

import java.io.File;

import renderEngine.models.RawModel;
import renderEngine.tools.normalMapOBJLoader.NormalMappedObjLoader;

class ModelResource {
	public File file;
	private RawModel model;
	public final String name;
	
	public ModelResource(String n){
		name = n;
	}
	
	public RawModel getModel(){
		if(model == null){
			model = loadModel(file);
		}
		return model;
	}
	
	private static RawModel loadModel(File f){
		try{
			return NormalMappedObjLoader.loadModel(f);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
