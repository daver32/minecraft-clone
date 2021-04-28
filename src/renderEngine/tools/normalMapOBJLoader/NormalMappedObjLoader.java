package renderEngine.tools.normalMapOBJLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.models.RawModel;
import renderEngine.tools.Loader;


public class NormalMappedObjLoader {

	private static final String RES_LOC = "res/";
	
	public static RawModel loadModel(File file) {
		String name = file.getPath();
		String path = name.substring(3, name.length()-4);
		return loadModel(path);
	}

	public static RawModel loadModel(String objFileName) {
		FileReader isr = null;
		File objFile = new File(RES_LOC + objFileName + ".obj");
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			System.err.println("File not found in " + objFileName + "; don't use any extention");
		}
		BufferedReader reader = new BufferedReader(isr);
		
		ArrayList<String> lines = new ArrayList<String>();
		String line = "";
		try {
			line = reader.readLine();
			while(line != null){
				lines.add(line);
				line = reader.readLine();
			}
			lines.add(null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		
		
		List<VertexNM> vertices = new ArrayList<VertexNM>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		int lineIndex = 0;

		
		line = "";
		try {
			while(line != null){
				
				
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]));
					VertexNM newVertex = new VertexNM(vertices.size(), vertex);
					vertices.add(newVertex);

				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
								(float) Float.valueOf(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]));
					normals.add(normal);
				}
				
				line = lines.get(lineIndex);
				
				lineIndex++;
					
			}
			
			lineIndex = 0;
			line = "";
			
			while (line != null) {
				if(line.startsWith("f ")){
					String[] currentLine = line.split(" ");
					String[] vertex1, vertex2, vertex3;
						
					vertex1 = currentLine[1].split("/");
					vertex2 = currentLine[2].split("/");
					vertex3 = currentLine[3].split("/");


					VertexNM v0 = processVertex(vertex1, vertices, indices);
					VertexNM v1 = processVertex(vertex2, vertices, indices);
					VertexNM v2 = processVertex(vertex3, vertices, indices);
					try{
						calculateTangents(v0, v1, v2, textures);//NEW
					}catch(IndexOutOfBoundsException e){}
					
				}
				
				line = lines.get(lineIndex);
				lineIndex++;
				
			}
			
			reader.close();
		} catch (IOException e) {
			//System.err.println("Error reading the file");
			e.printStackTrace();
		}
		
		
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float[] tangentsArray = new float[vertices.size() * 3];
		@SuppressWarnings("unused")
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
				texturesArray, normalsArray, tangentsArray);
		int[] indicesArray = convertIndicesListToArray(indices);

		return Loader.loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray, tangentsArray);
	}

	//NEW 
	private static void calculateTangents(VertexNM v0, VertexNM v1, VertexNM v2,
			List<Vector2f> textures) {
		Vector3f delatPos1 = Vector3f.sub(v1.getPosition(), v0.getPosition(), null);
		Vector3f delatPos2 = Vector3f.sub(v2.getPosition(), v0.getPosition(), null);
		Vector2f uv0 = textures.get(v0.getTextureIndex());
		Vector2f uv1 = textures.get(v1.getTextureIndex());
		Vector2f uv2 = textures.get(v2.getTextureIndex());
		Vector2f deltaUv1 = Vector2f.sub(uv1, uv0, null);
		Vector2f deltaUv2 = Vector2f.sub(uv2, uv0, null);

		float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
		delatPos1.scale(deltaUv2.y);
		delatPos2.scale(deltaUv1.y);
		Vector3f tangent = Vector3f.sub(delatPos1, delatPos2, null);
		tangent.scale(r);
		v0.addTangent(tangent);
		v1.addTangent(tangent);
		v2.addTangent(tangent);
	}

	private static VertexNM processVertex(String[] vertex, List<VertexNM> vertices,
			List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		VertexNM currentVertex = vertices.get(index);

		/*
		System.out.println(vertex[0]);
		System.out.println(vertex[1]);
		System.out.println(vertex[2]);
		*/
		
		int textureIndex = 0;
		int normalIndex = 0;
		try{
			textureIndex = Integer.parseInt(vertex[1]) - 1;
			normalIndex = Integer.parseInt(vertex[2]) - 1;
		}catch(NumberFormatException e){
			
		}
		
		
		

		
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
			return currentVertex;
		} else {
			return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
					vertices);
		}
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static float convertDataToArrays(List<VertexNM> vertices, List<Vector2f> textures,
			List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
			float[] normalsArray, float[] tangentsArray) {
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			VertexNM currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}
			Vector3f position = currentVertex.getPosition();
			
			Vector2f textureCoord;
			try{
				textureCoord = textures.get(currentVertex.getTextureIndex());
			}catch(IndexOutOfBoundsException e){
				textureCoord = new Vector2f(0,0);
			}
			
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			Vector3f tangent = currentVertex.getAverageTangent();
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
			tangentsArray[i * 3] = tangent.x;
			tangentsArray[i * 3 + 1] = tangent.y;
			tangentsArray[i * 3 + 2] = tangent.z;

		}
		return furthestPoint;
	}

	private static VertexNM dealWithAlreadyProcessedVertex(VertexNM previousVertex, int newTextureIndex,
			int newNormalIndex, List<Integer> indices, List<VertexNM> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		} else {
			VertexNM anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex,
						newNormalIndex, indices, vertices);
			} else {
				VertexNM duplicateVertex = previousVertex.duplicate(vertices.size());//NEW
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				return duplicateVertex;
			}
		}
	}

	private static void removeUnusedVertices(List<VertexNM> vertices) {
		for (VertexNM vertex : vertices) {
			vertex.averageTangents();
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}

}