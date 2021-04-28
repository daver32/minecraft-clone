package renderEngine.console.commands;

public class ExitCommand extends ConsoleCommand{

	public ExitCommand(){
		super("exit");
	}
	
	public String getHelp() {
		return "'exit', exits the game";
	}

	public String trigger(String[] s) {
		return "Exiting...";
	}
}
