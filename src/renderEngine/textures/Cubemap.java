package renderEngine.textures;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import renderEngine.tools.Loader;

public class Cubemap {
	public final int CUBEMAP_ID, SIZE;
	
	public Cubemap(int id, int size){
		CUBEMAP_ID = id;
		SIZE = size;
	}
	
	public Cubemap(int size){
		CUBEMAP_ID = genCubemap(size);
		SIZE = size;
	}
	

	public int getID() {
		return CUBEMAP_ID;
	}
	
	private static int genCubemap(int size){
		int id = GL11.glGenTextures();
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
		
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		
		for(int i = 0; i < 6; i++){
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, size, size, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
		}
		


		
		Loader.addToRemoveTexture(id);
		return id;
	}
}
