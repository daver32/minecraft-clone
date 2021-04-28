package renderEngine.display;


import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	

	private static int fpsCap = 144;
	private static boolean useFpsCap = true;
	
	public static void createDisplay(boolean fullscreen, int width, int height){		
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		try {
			Display.setDisplayMode(new DisplayMode(width,height));
			if(fullscreen){
				Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
			}
			Display.create(new PixelFormat(), attribs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, width, height);
	}
	
	public static void updateDisplay(){
		if(useFpsCap){
			Display.sync(fpsCap);
		}
		Display.update();
		
	}
	
	public static void closeDisplay(){
		Display.destroy();
	}
	
	public static void setFpsCap(int cap){
		fpsCap = cap;
	}
	public static void setFpsCapUsage(boolean cap){
		useFpsCap = cap;
	}
	public static void toggleFpsCapUsage(){
		useFpsCap = !useFpsCap;
	}
	

}
