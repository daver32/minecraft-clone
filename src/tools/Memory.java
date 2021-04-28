package tools;

public class Memory {

	public static long getUsedMemory(){
		Runtime r = Runtime.getRuntime();
		
		return r.freeMemory();
	}
}
