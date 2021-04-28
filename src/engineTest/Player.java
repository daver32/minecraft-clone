package engineTest;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.entities.Camera;

public class Player {
	private Camera c;
	
	public Player(){
		c = new Camera();
	}
	
	public void update(){
		double yawRads = -Math.toRadians((int)c.getYaw()-90);
		double pitchRads = -Math.toRadians((int)c.getPitch());
		double xzLen = Math.cos(pitchRads);
		Vector3f forward = new Vector3f((float)(xzLen * Math.cos(yawRads)), (float)Math.sin(pitchRads), (float)(xzLen * Math.sin(-yawRads)));
		float spd = 0.1f;
		forward.x *= spd; forward.y *= spd; forward.z *= spd;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			Vector3f.add(c.getPosition(), forward, c.getPosition());
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			forward.negate();
			Vector3f.add(c.getPosition(), forward, c.getPosition());
		}
		
		int hw = Display.getWidth()/2;
		int hh = Display.getHeight()/2;
		
		float xdiff = (Mouse.getX() - hw) * 0.1f;
		float ydiff = (Mouse.getY() - hh) * 0.1f;
		
		c.getRotation().y += xdiff;
		c.getRotation().x -= ydiff;
		
		Mouse.setCursorPosition(hw, hh);
	}
	
	public Camera getCamera(){
		return c;
	}
}
