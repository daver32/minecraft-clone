package renderEngine.binds;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class PressBind extends Bind{
	
	private boolean alreadyIsPressed = false;
	private boolean inputRetrieved = false;
	private int key;
	private boolean isMouseBind, isPressed;

	public PressBind(int key, boolean isMouseBind) {
		this.key = key;
		this.isMouseBind = isMouseBind;
	}
	

	@Override
	public void update(double time){
		
		boolean buttonIsPressed = isMouseBind ? Mouse.isButtonDown(key) : Keyboard.isKeyDown(key);
		
		if(buttonIsPressed){
			if(!alreadyIsPressed){
				isPressed = true;
				alreadyIsPressed = true;
				inputRetrieved = false;
			}else if(inputRetrieved){
				isPressed = false;
				alreadyIsPressed = true;
			}
		}else if(inputRetrieved){
			isPressed = false;
			alreadyIsPressed = false;
		}
	}
	
	public boolean isPressed(){
		inputRetrieved = true;
		return isPressed;
	}
}
