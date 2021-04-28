package renderEngine.framebuffers;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import renderEngine.tools.Loader;

public class CubeBuffer {
	
	private final int fboID, cubemapID, depthID, SIZE;
	
	public CubeBuffer(int size){
		fboID = createFrameBuffer();
		SIZE = size;
		
		cubemapID = genCubeTexture();
		depthID = genDepthBuffer();
	}
	
	private int genCubeTexture(){
		int cubemapID = GL11.glGenTextures();
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubemapID);
		Loader.addToRemoveTexture(cubemapID);
		
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_MAX_TEXTURE_LOD_BIAS, 1f);
		
		
		
		bind();
		for(int i = 0; i < 6; i++){
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, GL11.GL_RGBA, SIZE, SIZE, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
		}
		
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X, cubemapID, 0);
		
		return cubemapID;
	}
	
	private int genDepthBuffer(){
		/*
        int depthBuffer = EXTFramebufferObject.glGenRenderbuffersEXT();
        bind();
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthBuffer);
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, SIZE, SIZE);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthBuffer);
        return depthBuffer;
        */
		/*
        bind();
        int depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, SIZE, SIZE);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                GL30.GL_RENDERBUFFER, depthBuffer);
        return depthBuffer;
        */
        return 0;
		
	}
	
    public void bind(){
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, cubemapID, 0);
        GL11.glViewport(0, 0, SIZE, SIZE);
	}
	
    private static int createFrameBuffer() {
        int frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        determineDrawBuffers();
        return frameBuffer;
    }
    
    private static void determineDrawBuffers(){
    	IntBuffer buffers = BufferUtils.createIntBuffer(1);
    	buffers.put(GL30.GL_COLOR_ATTACHMENT0);
    	buffers.flip();
    	GL20.glDrawBuffers(buffers);
    }
    
    public int getCubemapID(){
    	return cubemapID;
    }
    
    public void cleanUP(){
    	GL30.glDeleteFramebuffers(fboID);
    	GL11.glDeleteTextures(cubemapID);
    	GL30.glDeleteRenderbuffers(depthID);
    }

	public int getFboID() {
		return fboID;
	}
    
    
}
