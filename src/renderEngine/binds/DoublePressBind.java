package renderEngine.binds;

public class DoublePressBind extends Bind{
	
	private double inBetweenTime;
	private PressBind singlePressBind;
	private boolean firstAlreadyPressed;
	private boolean inputRetrieved = true;
	private double firstPressTime;
	
	private boolean isPressed;

	public DoublePressBind(int key, boolean isMouseBind, double time) {
		inBetweenTime = time;
		singlePressBind = new PressBind(key, isMouseBind);

	}
	
	@Override
	public void update(double time) {

		if(inputRetrieved){
			if(firstAlreadyPressed){
				double timeEclapsed = time - firstPressTime;
				if(timeEclapsed < inBetweenTime){

					singlePressBind.update(timeEclapsed);
					isPressed = singlePressBind.isPressed();
					if(isPressed){
						firstAlreadyPressed = false;
						inputRetrieved = false;
					}
				}else{
					firstAlreadyPressed = false;
					isPressed = false;
				}
					
				
			}else{
				isPressed = false;
				singlePressBind.update(time);
				firstAlreadyPressed = singlePressBind.isPressed();
				firstPressTime = time;
			}
		}
	}
	
	public boolean isPressed(){
		inputRetrieved = true;
		return isPressed;
		
	}
}
