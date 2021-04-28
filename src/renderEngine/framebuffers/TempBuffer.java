package renderEngine.framebuffers;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import renderEngine.tools.Loader;

public class TempBuffer {
	private final int texture1;
	private final int WIDTH, HEIGHT, ID;
	
	public TempBuffer(int w, int h){
		WIDTH = w;
		HEIGHT = h;
		ID = createFrameBuffer();
		texture1 = createTextureAttachment(w, h, GL11.GL_RGBA, 0);
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
    	IntBuffer buffers = BufferUtils.createIntBuffer(1);
    	buffers.put(GL30.GL_COLOR_ATTACHMENT0);
    	buffers.flip();
    	GL20.glDrawBuffers(buffers);
    }
    
    private static int createTextureAttachment(int width, int height, int colors, int attachment){
        int texture = GL11.glGenTextures();
        Loader.addToRemoveTexture(texture);
       
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, colors, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0+attachment, texture, 0);
        return texture;
    }
    
    public void cleanUP(){
    	GL30.glDeleteFramebuffers(ID);
    	GL11.glDeleteTextures(texture1);
    }

	public int getTexture1() {
		return texture1;
	}

    
}
