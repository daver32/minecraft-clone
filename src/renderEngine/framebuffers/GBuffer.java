package renderEngine.framebuffers;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import renderEngine.tools.Loader;

public class GBuffer {
	private final int albedoID, normalID, positionID, depthID, depthTextureID, reflectionID, displacementID;
	private final int WIDTH, HEIGHT, ID;
	
	
	public GBuffer(int w, int h){
		WIDTH = w;
		HEIGHT = h;
		ID = createFrameBuffer();
		albedoID = createTextureAttachment(w, h, GL11.GL_RGBA, 0);
		normalID = createTextureAttachment(w, h, GL30.GL_RGBA32F, 1);
		positionID = createTextureAttachment(w, h, GL30.GL_RGBA32F, 2);
		reflectionID = createTextureAttachment(w, h, GL30.GL_RGB16F, 3);
		displacementID = createTextureAttachment(w, h, GL30.GL_RG, 4);
		
		depthID = createDepthBufferAttachment(w, h);
		depthTextureID = createDepthTextureAttachment(w, h);
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
    	IntBuffer buffers = BufferUtils.createIntBuffer(5);
    	buffers.put(GL30.GL_COLOR_ATTACHMENT0);
    	buffers.put(GL30.GL_COLOR_ATTACHMENT1);
    	buffers.put(GL30.GL_COLOR_ATTACHMENT2);
    	buffers.put(GL30.GL_COLOR_ATTACHMENT3);
    	buffers.put(GL30.GL_COLOR_ATTACHMENT4);
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

	private static int createDepthTextureAttachment(int width, int height){
        int texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height,
                0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                texture, 0);
        return texture;
    }
    
    @SuppressWarnings("unused")
	private static int createDepthBufferAttachmentM(int width, int height) {
        int depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
        GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL11.GL_DEPTH_COMPONENT, width,
                height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                GL30.GL_RENDERBUFFER, depthBuffer);
        return depthBuffer;
    }
    
    public static int createDepthBufferAttachment(int width, int height) {
        int depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width,
                height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                GL30.GL_RENDERBUFFER, depthBuffer);
        return depthBuffer;
    }
    
    public void cleanUP(){
    	GL30.glDeleteFramebuffers(ID);
    	GL11.glDeleteTextures(albedoID);
    	GL11.glDeleteTextures(normalID);
    	GL11.glDeleteTextures(positionID);
    	GL11.glDeleteTextures(depthID);
    	GL11.glDeleteTextures(depthTextureID);
    	GL30.glDeleteRenderbuffers(depthID);
    }

	public int getAlbedoID() {
		return albedoID;
	}

	public int getNormalID() {
		return normalID;
	}

	public int getDepthTextureID() {
		return depthTextureID;
	}
	
	public int getPositionTextureID(){
		return positionID;
	}
	
	public int getReflectivityTextureID(){
		return reflectionID;
	}

	public int getDisplacementID() {
		return displacementID;
	}
	
	public int getDepthAttachmentID(){
		return depthID;
	}

	public int getFboID() {
		return ID;
	}
}
