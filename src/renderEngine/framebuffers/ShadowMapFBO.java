package renderEngine.framebuffers;


import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL41;

import renderEngine.entities.Sun;
import renderEngine.tools.Loader;


 
/**
 * The frame buffer for the shadow pass. This class sets up the depth texture
 * which can be rendered to during the shadow render pass, producing a shadow
 * map.
 * 
 * @author Karl
 *
 */
public class ShadowMapFBO {
 
    private final int WIDTH;
    private final int HEIGHT;
    private int fboID;
    private int textureID;
    
    private static boolean LINEAR_SAMPLING = false;
 
    public ShadowMapFBO(int width){
    	WIDTH = HEIGHT = width;
    	initialiseFrameBuffer();
    }
    
    public ShadowMapFBO(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
        initialiseFrameBuffer();
    }
    
	public void bind(int cascade){
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
        
        double w = (int) Math.sqrt(Sun.NUM_CASCADES);
        
        double x = cascade % (int)w;
        double y = cascade / (int)w;
        
        GL11.glViewport((int)(x*WIDTH / w), (int)(y*WIDTH / w), WIDTH/2, HEIGHT/2);
        //GL11.glViewport((int)(x*WIDTH / w), (int)(y*WIDTH / w), (int)((x+1)*WIDTH / w), (int)((y+1)*HEIGHT / w));
	}
	
	public void bind(){
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        
        int w = WIDTH/2;
        int h = HEIGHT/2;
        GL41.glViewportIndexedf(0, 0, 0, w, h);
        GL41.glViewportIndexedf(1, w, 0, w, h);
        GL41.glViewportIndexedf(2, 0, h, w, h);
        GL41.glViewportIndexedf(3, w, h, w, h);
	}
 
    protected void cleanUp() {
        GL30.glDeleteFramebuffers(fboID);
        GL11.glDeleteTextures(textureID);
    }
 
    protected void unbindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
    }
 
    protected int getShadowMap() {
        return textureID;
    }
 
    private void initialiseFrameBuffer() {
        fboID = createFrameBuffer();
        textureID = createDepthTextureAttachment(WIDTH, HEIGHT);

        unbindFrameBuffer();
    }
 
    private static int createFrameBuffer() {
        int frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glDrawBuffer(GL11.GL_NONE);
        return frameBuffer;
    }
 
    private static int createDepthTextureAttachment(int width, int height) {
        int texture = GL11.glGenTextures();
        Loader.addToRemoveTexture(texture);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT16, width, height, 0,
                GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        if(LINEAR_SAMPLING){
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        }else{
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        }
        
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, texture, 0);
        return texture;
    }

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public int getID() {
		return fboID;
	}

	public int getTextureID() {
		return textureID;
	}

}
