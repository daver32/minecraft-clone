package renderEngine.binds;

import org.lwjgl.input.Keyboard;

public class ConditionBind extends Bind{

	private int key;
	private PressBind pressBind;
	private boolean isPressed, inputRetrieved = true;
	
	public ConditionBind(int key0, int key1, boolean isMouseBind){
		this.key = key0;
		pressBind = new PressBind(key1, isMouseBind);
	}
	
	@Override
	public void update(double time) {
		if(inputRetrieved && Keyboard.isKeyDown(key)){
			pressBind.update(time);
			if(pressBind.isPressed()){
				isPressed = true;
				inputRetrieved = false;
			}else{
				isPressed = false;
			}
		}
	}
	
	public boolean isPressed(){
		inputRetrieved = true;
		return isPressed;
	}

}
