package renderEngine.framebuffers;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import renderEngine.tools.Loader;

public class PPBuffer {
	private final int rawColorMap, colorMap, bloomMap, blurredMap;
	private final int ID;
	private final int WIDTH, HEIGHT;
	
	public PPBuffer(int w, int h){
		WIDTH = w;
		HEIGHT = h;
		ID = createFrameBuffer();
		rawColorMap = createTextureAttachment(w, h, GL11.GL_RGBA, 0);
		colorMap = createTextureAttachment(w, h, GL11.GL_RGB, 1);
		bloomMap = createTextureAttachment(w, h, GL11.GL_RGB, 2);
		blurredMap = createTextureAttachment(w, h, GL11.GL_RGB, 3);
	}
	
	public void bind(){
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ID);
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
	}
	
    private static int createFrameBuffer() {
        int frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        determineDrawBuffers();
        return frameBuffer;
    }
    
    private static void determineDrawBuffers(){
    	IntBuffer buffers = BufferUtils.createIntBuffer(4);
    	buffers.put(GL30.GL_COLOR_ATTACHMENT0);
    	buffers.put(GL30.GL_COLOR_ATTACHMENT1);
    	buffers.put(GL30.GL_COLOR_ATTACHMENT2);
    	buffers.put(GL30.GL_COLOR_ATTACHMENT3);
    	buffers.flip();
    	GL20.glDrawBuffers(buffers);
    }
    
    private static int createTextureAttachment(int width, int height, int colors, int attachment){
        int texture = GL11.glGenTextures();
        Loader.addToRemoveTexture(texture);
       
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, colors, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);        
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE); 
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0+attachment, texture, 0);
        return texture;
    }
    
    public void cleanUP(){
    	GL30.glDeleteFramebuffers(ID);
    	GL11.glDeleteTextures(colorMap);
    	GL11.glDeleteTextures(rawColorMap);
    	GL11.glDeleteTextures(bloomMap);
    	GL30.glDeleteRenderbuffers(blurredMap);
    }

	public int getColorMap() {
		return colorMap;
	}

	public int getBloomMap() {
		return bloomMap;
	}

	public int getBlurredMap() {
		return blurredMap;
	}
    
	public int getFboID(){
		return ID;
	}

	public int getRawColorMap() {
		return rawColorMap;
	}
}
