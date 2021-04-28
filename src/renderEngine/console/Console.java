package renderEngine.console;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.lwjgl.input.Keyboard;

import renderEngine.binds.BindManager;
import renderEngine.binds.ConditionBind;
import renderEngine.binds.HoldBind;
import renderEngine.binds.PressBind;
import renderEngine.console.commands.ClearCommand;
import renderEngine.console.commands.ConsoleCommand;
import renderEngine.console.commands.ExitCommand;
import renderEngine.console.commands.HelpCommand;

public class Console {
	private static String currentText = "";
	private static ArrayList<String> consoleOutput = new ArrayList<String>();
	private static ArrayList<String> history = new ArrayList<String>();
	private static boolean hasFocus;
	private static PressBind focusBind = new PressBind(Keyboard.KEY_GRAVE, false);
	private static PressBind backspaceBind = new PressBind(Keyboard.KEY_BACK, false);
	private static HoldBind backspaceHoldBind = new HoldBind(Keyboard.KEY_BACK, false);
	private static PressBind enterBind = new PressBind(Keyboard.KEY_RETURN, false);
	private static PressBind upBind = new PressBind(Keyboard.KEY_UP, false);
	private static PressBind downBind = new PressBind(Keyboard.KEY_DOWN, false);
	private static int historyIndex;
	private static int maxOutputs = 10;
	private static ScriptEngine evalEngine;
	private static ConditionBind pasteBind = new ConditionBind(Keyboard.KEY_LCONTROL, Keyboard.KEY_V, false);
	private static ArrayList<ConsoleCommand> commands = new ArrayList<ConsoleCommand>();
	
	private static ConsoleKey[] keys = new ConsoleKey[]{
		new ConsoleKey(Keyboard.KEY_0, "0"),
		new ConsoleKey(Keyboard.KEY_1, "1"),	
		new ConsoleKey(Keyboard.KEY_2, "2"),	
		new ConsoleKey(Keyboard.KEY_3, "3"),
		new ConsoleKey(Keyboard.KEY_4, "4"),
		new ConsoleKey(Keyboard.KEY_5, "5"),
		new ConsoleKey(Keyboard.KEY_6, "6"),
		new ConsoleKey(Keyboard.KEY_7, "7"),
		new ConsoleKey(Keyboard.KEY_8, "8"),
		new ConsoleKey(Keyboard.KEY_9, "9"),
		new ConsoleKey(Keyboard.KEY_NUMPAD0, "0"),
		new ConsoleKey(Keyboard.KEY_NUMPAD1, "1"),	
		new ConsoleKey(Keyboard.KEY_NUMPAD2, "2"),	
		new ConsoleKey(Keyboard.KEY_NUMPAD3, "3"),
		new ConsoleKey(Keyboard.KEY_NUMPAD4, "4"),
		new ConsoleKey(Keyboard.KEY_NUMPAD5, "5"),
		new ConsoleKey(Keyboard.KEY_NUMPAD6, "6"),
		new ConsoleKey(Keyboard.KEY_NUMPAD7, "7"),
		new ConsoleKey(Keyboard.KEY_NUMPAD8, "8"),
		new ConsoleKey(Keyboard.KEY_NUMPAD9, "9"),
		new ConsoleKey(Keyboard.KEY_SPACE, " "),
		new ConsoleKey(Keyboard.KEY_UNDERLINE, "_"),
		new ConsoleKey(Keyboard.KEY_ADD, "+"),
		new ConsoleKey(Keyboard.KEY_MINUS, "-"),
		new ConsoleKey(Keyboard.KEY_EQUALS, "_"),
		new ConsoleKey(Keyboard.KEY_PERIOD, "."),
		new ConsoleKey(Keyboard.KEY_SLASH, "="),
		new ConsoleKey(Keyboard.KEY_SUBTRACT, "-"),
		new ConsoleKey(Keyboard.KEY_MULTIPLY, "*"),
		new ConsoleKey(Keyboard.KEY_DIVIDE, "/"),
		new ConsoleKey(Keyboard.KEY_LBRACKET, "("),
		new ConsoleKey(Keyboard.KEY_RBRACKET, ")"),
		new ConsoleKey(Keyboard.KEY_A, "A"),
		new ConsoleKey(Keyboard.KEY_B, "B"),
		new ConsoleKey(Keyboard.KEY_C, "C"),
		new ConsoleKey(Keyboard.KEY_D, "D"),
		new ConsoleKey(Keyboard.KEY_E, "E"),
		new ConsoleKey(Keyboard.KEY_F, "F"),
		new ConsoleKey(Keyboard.KEY_G, "G"),
		new ConsoleKey(Keyboard.KEY_H, "H"),
		new ConsoleKey(Keyboard.KEY_I, "I"),
		new ConsoleKey(Keyboard.KEY_J, "J"),
		new ConsoleKey(Keyboard.KEY_K, "K"),
		new ConsoleKey(Keyboard.KEY_L, "L"),
		new ConsoleKey(Keyboard.KEY_M, "M"),
		new ConsoleKey(Keyboard.KEY_N, "N"),
		new ConsoleKey(Keyboard.KEY_O, "O"),
		new ConsoleKey(Keyboard.KEY_P, "P"),
		new ConsoleKey(Keyboard.KEY_Q, "Q"),
		new ConsoleKey(Keyboard.KEY_R, "R"),
		new ConsoleKey(Keyboard.KEY_S, "S"),
		new ConsoleKey(Keyboard.KEY_T, "T"),
		new ConsoleKey(Keyboard.KEY_U, "U"),
		new ConsoleKey(Keyboard.KEY_V, "V"),
		new ConsoleKey(Keyboard.KEY_W, "W"),
		new ConsoleKey(Keyboard.KEY_X, "X"),
		new ConsoleKey(Keyboard.KEY_Y, "Y"),
		new ConsoleKey(Keyboard.KEY_Z, "Z"),
		
	};
	
	public static void init(){
		BindManager.addBind(focusBind);
		BindManager.addBind(backspaceHoldBind);
		//BindManager.addBind(pasteBind);
		
		ScriptEngineManager mgr = new ScriptEngineManager();
		evalEngine = mgr.getEngineByName("JavaScript");
		commands.add(new ClearCommand());
		commands.add(new ExitCommand());
		commands.add(new HelpCommand());
	}
	
	public static void update(){
		
		if(hasFocus){
			if(focusBind.isPressed()){
				hasFocus = false;
				return;
			}
		}else if(focusBind.isPressed()){
			hasFocus = true;
		}
		
		if(hasFocus){
			String str = "";
			if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
				for(ConsoleKey key : keys){
					if(key.getPressed()){
						str = key.string;
						break;
					}
				}
			}

			if(str.equals("")){
				backspaceBind.update(0);
				enterBind.update(0);
				upBind.update(0);
				downBind.update(0);
				pasteBind.update(0);
				if(backspaceBind.isPressed()){
					try{
						currentText = currentText.substring(0, currentText.length() - 1);
					}catch(StringIndexOutOfBoundsException e){}
					
				}else if(backspaceHoldBind.getPressed() > 500){
					try{
						currentText = currentText.substring(0, currentText.length() - 1);
					}catch(StringIndexOutOfBoundsException e){}
				}else if(enterBind.isPressed()){
					addToOutput(currentText);
					if(currentText.startsWith("m ")){
						try {
							addToOutput("> " + evalEngine.eval(currentText.substring(2, currentText.length())).toString());
						}catch (ScriptException e) {
							addToOutput("> [Error] Invalid syntax.");
						}
						return;
					}
					
					addToOutput("> " + processCommand(currentText));
					
					addToHistory(currentText);
					currentText = "";
					historyIndex = 0;
					
				}else if(upBind.isPressed()){
					try{
						currentText = history.get(historyIndex);
					}catch(IndexOutOfBoundsException e){}
					if(historyIndex < history.size()){
						historyIndex++;
					}
				}else if(downBind.isPressed()){
					try{
						currentText = history.get(historyIndex);
					}catch(IndexOutOfBoundsException e){}
					if(historyIndex > 0){
						historyIndex--;
					}
					
				}else if(pasteBind.isPressed()){

					try {
						currentText += Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
					} catch (HeadlessException | UnsupportedFlavorException | IOException e) {}
				}
				return;
			}
			
			
			
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
				str = str.toUpperCase();
			}else{
				str = str.toLowerCase();
			}
			
			currentText += str;

		}
		

	}
	
	private static void addToOutput(String s){
		consoleOutput.add(0, s);
		if(consoleOutput.size() > maxOutputs){
			consoleOutput.remove(consoleOutput.size()-1);
		}
	}
	
	private static void addToHistory(String s){
		history.add(0, s);
	}
	
	private static String processCommand(String s){
		if(s.equals("")){
			return "";
		}
		
		String[] strings = s.split(" ");
		for(ConsoleCommand c : commands){
			if(c.getName().equals(strings[0])){
				return c.trigger(strings);
			}
		}
		
		return "[Error] No such command '" + strings[0] + "'";
		
	}
	
	
	public static boolean getFocus(){
		return hasFocus;
	}

	public static String getCurrentText() {
		return currentText;
	}

	public static ArrayList<String> getConsoleOutput() {
		return consoleOutput;
	}
	
	public static void clearConsole(){
		consoleOutput.clear();
	}

	public static ArrayList<ConsoleCommand> getCommands() {
		return commands;
	}
	
	public static void addCommand(ConsoleCommand c){
		commands.add(c);
	}

	public static void processExternalCommands(String[] commands){
		for(String s : commands){
			addToOutput("+ " + processCommand(s));
		}
	}

	public static void setFocus(boolean b) {
		hasFocus = b;
		
	}
}
