package renderEngine.binds;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class MouseDragBind extends Bind {

	private int key;
	private boolean isMouseBind, isPressed;

	private int[] startingPoint, currentPoint;
	
	public MouseDragBind(int key, boolean isMouseBind) {
		this.key = key;
		this.isMouseBind = isMouseBind;
		startingPoint = new int[2];
		currentPoint = new int[2];
	}
	
	@Override
	public void update(double time) {
		boolean buttonIsPressed = isMouseBind ? Mouse.isButtonDown(key) : Keyboard.isKeyDown(key);

		if(buttonIsPressed){
			
			int mouseX = Mouse.getX();
			int mouseY = Mouse.getY();
			
			if(!isPressed){
				isPressed = true;
				startingPoint[0] = mouseX;
				startingPoint[1] = mouseY;
			}else{
				currentPoint[0] = mouseX;
				currentPoint[1] = mouseY;
			}
		}else{
			isPressed = false;
		}
	}
	
	public int[] getDrag(){
		if(isPressed){
			return new int[]{currentPoint[0] - startingPoint[0], currentPoint[1] - startingPoint[1]};
		}else{
			return null;
		}
	}
}
