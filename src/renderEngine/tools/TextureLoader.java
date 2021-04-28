package renderEngine.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import renderEngine.textures.Cubemap;

public class TextureLoader {
	
	public static enum Format{
		RGB,
		RGBA
	}
	
	private static ByteBuffer loadImageToBuffer(BufferedImage image) throws IOException{
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(4 * image.getWidth() * image.getHeight());
        
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int rgb = pixels[image.getWidth() * y + x];
                buffer.put((byte) ((rgb >> 16) & 255)); //RED
                buffer.put((byte) ((rgb >> 8 ) & 255));	//GREEN
                buffer.put((byte) ((rgb >> 0 ) & 255)); //BLUE
                buffer.put((byte) ((rgb >> 24) & 255)); //ALPHA
            }
        }

        buffer.flip();
        return buffer;
	}
	
	public static int loadTexture(File file){
		try {
			//Uses RGBAs
			BufferedImage image = ImageIO.read(file);
			
			ByteBuffer buffer = loadImageToBuffer(image);
	        
	        int id = GL11.glGenTextures();
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	        
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_MAX_TEXTURE_LOD_BIAS, 1f);
			
	        Loader.addToRemoveTexture(id);
			
	        return id;
	        
		} catch (IOException e) {
			System.err.println("Error loading texture '" + file.getName() + "'");
			e.printStackTrace();
		}
		return 0;
	}
	
	public static Cubemap loadSkybox(File folder){

		ByteBuffer right, left, top, bottom, front, back;
		right = left = top = bottom = front = back = null;
		int width = 0, height = 0;
		
		File[] files = folder.listFiles();
		for(File file : files){
			String name = file.getName();
			try {
				BufferedImage image = ImageIO.read(file);
				if(name.endsWith("_right.png")){
					right = loadImageToBuffer(image);
					width = image.getWidth();
					height = image.getHeight();
				}else if(name.endsWith("_left.png")){
					left = loadImageToBuffer(image);
				}else if(name.endsWith("_top.png")){
					top = loadImageToBuffer(image);
				}else if(name.endsWith("_bottom.png")){
					bottom = loadImageToBuffer(image);
				}else if(name.endsWith("_front.png")){
					front = loadImageToBuffer(image);
				}else if(name.endsWith("_back.png")){
					back = loadImageToBuffer(image);
				}
			}catch (Exception e){
				continue;
			}
		}
		
		if(right != null && left != null && top != null && bottom != null && front != null && back != null){
			return new Cubemap(composeCubemap(width, height, right, left, top, bottom, front, back), width);
		}

		return null;
		
	}
	
	public static int composeCubemap(int w, int h, ByteBuffer right, ByteBuffer left, ByteBuffer top, ByteBuffer bottom, ByteBuffer front, ByteBuffer back){
		int id = GL11.glGenTextures();
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
		
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		
		ByteBuffer[] buffers = new ByteBuffer[]{right, left, top, bottom, front, back};
		for(int i = 0; i < 6; i++){
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffers[i]);
		}
		
		GL30.glGenerateMipmap(GL13.GL_TEXTURE_CUBE_MAP);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL13.GL_TEXTURE_CUBE_MAP, GL14.GL_MAX_TEXTURE_LOD_BIAS, 1f);
		
		Loader.addToRemoveTexture(id);
		return id;
	}
}
