package renderEngine.resources;

import java.io.File;
import java.util.ArrayList;

public class FileTools {
	public static ArrayList<File> listAllFiles(File folder){
		ArrayList<File> result = new ArrayList<File>();
		addAllFiles(result, folder.listFiles());
		return result;
	}
	
	private static void addAllFiles(ArrayList<File> array, File[] list){
		if(list != null){
			for(File f : list){
				if(f.isDirectory()){
					addAllFiles(array, f.listFiles());
				}else if(f.isFile()){
					array.add(f);
				}
			}
		}
	}
}
