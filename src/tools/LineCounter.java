package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LineCounter {
	
	@SuppressWarnings("unused")
	private static final char SCOLON = ";".toCharArray()[0];
	
	public static int countLines(){
		File parent = new File("src/");
		ArrayList<File> files = new ArrayList<File>();
		ArrayList<File> newFiles = new ArrayList<File>();
		File[] fa = parent.listFiles();
		for(File f : fa){
			files.add(f);
		}
		
		
		int r = 0;
		
		while(files.size() > 0){
			
			for(File f : files){
				
				if(f.isFile() && f.getName().endsWith(".java")){
					try{
						r += countLines(f);
					}catch(IOException e){}
				}else if(f.isDirectory()){
					File[] a = f.listFiles();
					for(File x : a){
						newFiles.add(x);
					}
				}
			}
			files = newFiles;
			newFiles = new ArrayList<File>();
			
		}
		
		return r;
	}
	
	private static int countLines(File f) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine();
		int r = 0;
		
		while(line != null){
			
			for(int i = 0; i < line.length(); i++){
				char curr = line.charAt(i);
				if(curr != 9 && curr != 32){
					if(curr != 47){
						r++;
					}
					break;
				}
				
				
				
			}
			
			
			line = br.readLine();
		}
		
		br.close();
		return r;
	}
	
	@SuppressWarnings("unused")
	private static int countChar(String str, char el){
		
		int r = 0;
		
		for(int i = 0; i < str.length(); i++){
			if(str.charAt(i) == el){
				r++;
			}
		}

		return r;
	}
	
}
