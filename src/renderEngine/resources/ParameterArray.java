package renderEngine.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ParameterArray {
	private ArrayList<String[]> parameters;
	
	public ParameterArray(){
		parameters = new ArrayList<String[]>();
	}

	public boolean init(File f){
		try{
			BufferedReader br = new BufferedReader(new FileReader(f));

			String line = br.readLine();
			
			int nullLines = 0;
			while(true){
				if(line == null){
					nullLines++;
					if(nullLines > 10){
						break;
					}
				}else{
					nullLines = 0;
					parseLine(line);
					line = br.readLine();	
				}
				
	
			}

			br.close();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean getBooleanParameter(String name){
		try{
			String str = getStringParameter(name).toLowerCase();
			if(str.equals("true")){
				return true;
			}else if(str.equals("false")){
				return false;
			}else{
				
				int i = Integer.parseInt(str);
				if(i == 0){
					return false;
				}else if(i == 1){
					return true;
				}

			}
		}catch(Exception e){
			
		}
		return false;
	}
	
	public float getFloatParameter(String name){
		String str = getStringParameter(name);
		if(str != null){
			try{
				return Float.parseFloat(str);
			}catch(Exception e){}
		}
		return 0;
	}
	
	public String getStringParameter(String name){
		for(String[] param : parameters){
			if(param[0].equals(name)){
				return param[1];
			}
		}
		return null;
	}
	
	private void parseLine(String line){
		String[] params = line.split("=", 2);
		if(params != null && params.length > 1 && params[1] != ""){
			parameters.add(params);
		}
	}	
}
