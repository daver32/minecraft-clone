package renderEngine.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.models.RawModel;


public class ModelOBJLoader {
	public static RawModel loadModel(String path){
		BufferedReader reader;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Vector2f> uvs = new ArrayList<Vector2f>();
		List<int[][]> indices = new ArrayList<int[][]>();
		
		
		try {
			reader = new BufferedReader(new FileReader("res/"+path+".obj"));
			String line;
			float[] values = new float[3];
			int index;
			String character;
			String string;
			
			while((line = reader.readLine()) != null){
				
				if(line.startsWith("v ")){
					index = 2;
					for(int i = 0;i < 3;i++){
						string = "";
						character = Character.toString(line.charAt(index));
						while(!" ".equals(character)){
							try{
								character = Character.toString(line.charAt(index));
							}catch(StringIndexOutOfBoundsException e){
								character = " ";
							}
							string += character;
							index++;	

						}			
						values[i] = Float.parseFloat(string);
					}
					vertices.add(new Vector3f(values[0],values[1],values[2]));
					
				}else if(line.startsWith("vt ")){
					index = 3;
					for(int i = 0;i < 2;i++){
						string = "";
						character = Character.toString(line.charAt(index));
						while(!" ".equals(character)){
							try{
								character = Character.toString(line.charAt(index));
							}catch(StringIndexOutOfBoundsException e){
								character = " ";
							}
							string += character;
							index++;
						}			
						values[i] = Float.parseFloat(string);
					}
					uvs.add(new Vector2f(values[0],values[1]));
					
				}else if(line.startsWith("vn ")){
					index = 3;
					for(int i = 0;i < 3;i++){
						string = "";
						character = Character.toString(line.charAt(index));
						while(!" ".equals(character)){
							try{
								character = Character.toString(line.charAt(index));
							}catch(StringIndexOutOfBoundsException e){
								character = " ";
							}
							string += character;
							index++;
						}			
						values[i] = Float.parseFloat(string);
					}
					normals.add(new Vector3f(values[0],values[1],values[2]));		
					
				}else if(line.startsWith("f ")){
					index = 2;
					int[][] thisLineIndices = new int[3][3];
					int arrayIndex1 = 0;
					int arrayIndex2 = 0;
					string = "";
					while(arrayIndex1 < 3){
						try{
							character = Character.toString(line.charAt(index));	
						}catch(StringIndexOutOfBoundsException e){
							character = " ";
						}
						if(character.equals("/")){
							thisLineIndices[arrayIndex1][arrayIndex2] = Integer.parseInt(string);
							arrayIndex2++;
							string = "";
						}else if(character.equals(" ")){
							thisLineIndices[arrayIndex1][arrayIndex2] = Integer.parseInt(string);
							string = "";
							arrayIndex2 = 0;
							arrayIndex1 ++;
						}else{
							string+=character;
						}
						index++;
						
					}
					indices.add(thisLineIndices);
				}
			}
			reader.close();
			
			
			float[] finalVertices = new float[indices.size()*27];
			float[] finalUvs = new float[indices.size()*18];
			float[] finalNormals = new float[indices.size()*27];
			int[] finalIndices = new int[indices.size()*9];
			
			int currentVerticeIndex;
			int currentUVIndex;
			int currentNormalIndex;
			int currentIndiceNumber = 0;
			
			int totalVerticeIndex = 0, totalNormalIndex = 0, totalUVIndex = 0, totalIndiceIndex = 0;
			
			

			Vector3f currentVertice;
			Vector3f currentNormal;
			Vector2f currentUV;
			
			
			
			
			
			
			for(index = 0; index < indices.size(); index++){

				for(int currentIndice = 0; currentIndice < 3; currentIndice++){
					currentVerticeIndex = indices.get(index)[currentIndice][0]-1;
					currentUVIndex = indices.get(index)[currentIndice][1]-1;
					currentNormalIndex = indices.get(index)[currentIndice][2]-1;
						
					currentVertice = vertices.get(currentVerticeIndex);
					currentNormal = normals.get(currentNormalIndex);
					currentUV = uvs.get(currentUVIndex);
					
					finalIndices[totalIndiceIndex++] = currentIndiceNumber;
					currentIndiceNumber++;
						
					finalVertices[totalVerticeIndex++] = currentVertice.getX();
					finalVertices[totalVerticeIndex++] = currentVertice.getY();
					finalVertices[totalVerticeIndex++] = currentVertice.getZ();
				
					finalNormals[totalNormalIndex++] = -currentNormal.getX();
					finalNormals[totalNormalIndex++] = -currentNormal.getY();
					finalNormals[totalNormalIndex++] = -currentNormal.getZ();
						
					finalUvs[totalUVIndex++] = currentUV.getX(); 
					finalUvs[totalUVIndex++] = 1.0f-currentUV.getY(); 
					
				}
			}
			
			/*
			for(int i = 0; i < uvs.size(); i++){
				for(int i2 = 0; i2 < 3; i2++){
					//System.out.println(indices.get(i)[i2][0]+" "+indices.get(i)[i2][1]+" "+indices.get(i)[i2][2]);
					
				}
				//System.out.println(vectors.get(i).x+" "+vectors.get(i).y+" "+vectors.get(i).z);
				System.out.println(uvs.get(i).x+" "+uvs.get(i).y);
			}
			*/
			
			System.out.println(finalVertices.length);
			
			
			return Loader.loadToVAO(finalVertices, finalUvs, finalNormals, finalIndices);
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		

	}
}
