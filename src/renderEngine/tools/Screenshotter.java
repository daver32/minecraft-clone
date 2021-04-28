package renderEngine.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class Screenshotter {
	
	public static void takeScreenshot(String directory, String format, String name){
		takeScreenshot(new File(directory), format, name);
	}
	public static void takeScreenshot(File directory, String format, String name){
		//if(!directory.isDirectory())return;
		if(!directory.exists()){
			directory.mkdirs();
		}
		
		GL11.glReadBuffer(GL11.GL_FRONT);
		int width = Display.getWidth();
		int height= Display.getHeight();
		int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
	
		File file = new File(directory.getPath()+"/"+name+"."+format);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		   
		for(int x = 0; x < width; x++) 
		{
		    for(int y = 0; y < height; y++)
		    {
		        int i = (x + (width * y)) * bpp;
		        int r = buffer.get(i) & 0xFF;
		        int g = buffer.get(i + 1) & 0xFF;
		        int b = buffer.get(i + 2) & 0xFF;
		        image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
		    }
		}
		   
		try {
			ImageIO.write(image, format, file);
		}catch(IOException e){
			e.printStackTrace();
		}
	 
		    
	}
}
