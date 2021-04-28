package renderEngine.console;

import renderEngine.binds.PressBind;

class ConsoleKey {
	public String string;
	private PressBind bind;
	
	public ConsoleKey(int integer, String string) {
		this.string = string;
		bind = new PressBind(integer, false);
	}
	
	public boolean getPressed(){
		bind.update(0);
		return bind.isPressed();
	}
}
