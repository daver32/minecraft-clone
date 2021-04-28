package renderEngine.console.commands;

import renderEngine.console.Console;

public class HelpCommand extends ConsoleCommand{

	public HelpCommand(){
		super("help");
	}
	
	public String getHelp() {
		return "'help <command name>', gives you a help about using a command";
	}

	public String trigger(String[] s) {
		for(ConsoleCommand c : Console.getCommands()){
			try{
				if(c.getName().equals(s[1])){
					return c.getHelp();
				}
			}catch(ArrayIndexOutOfBoundsException e){
				return "[Error] Invalid syntax.";
			}
		}
		return "[Error] Command '" + s[1] + "' doesn't exist.";
	}
}
