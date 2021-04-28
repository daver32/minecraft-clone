package renderEngine.binds;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class HoldBind extends Bind {


	private int key;
	private boolean isMouseBind, isPressed;
	private double timePressed, currentTime;

	public HoldBind(int key, boolean isMouseBind) {
		this.key = key;
		this.isMouseBind = isMouseBind;
	}
	
	
	@Override
	public void update(double time) {
		boolean buttonIsPressed = isMouseBind ? Mouse.isButtonDown(key) : Keyboard.isKeyDown(key);
		currentTime = time;
		
		if(buttonIsPressed){
			if(!isPressed){
				isPressed = true;
				timePressed = time;
			}
		}else{
			isPressed = false;
		}
	}
	
	public double getPressed(){
		if(isPressed){
			return currentTime - timePressed;
		}
		return 0;
	}

}
