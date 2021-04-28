package renderEngine.console.commands;

public abstract class ConsoleCommand {
	public abstract String getHelp();
	public abstract String trigger(String[] s);
	private String name;
	
	public ConsoleCommand(String n){
		name = n;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
