package renderEngine.console.commands;

import renderEngine.console.Console;

public class ClearCommand extends ConsoleCommand{

	public ClearCommand(){
		super("clear");
	}
	
	public String getHelp() {
		return "'clear', clears the console";
	}

	public String trigger(String[] s) {
		Console.clearConsole();
		return "Console cleared";
	}
}
