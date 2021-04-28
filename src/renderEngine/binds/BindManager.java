package renderEngine.binds;

import java.util.ArrayList;

public class BindManager {
	public static ArrayList<Bind> binds = new ArrayList<Bind>();

	
	public static void update(){
		double time = System.currentTimeMillis();
		for(Bind b : binds){
			b.update(time);
		}
	}
	
	public void clearBinds(){
		binds = new ArrayList<Bind>();
	}
	
	public static void addBind(Bind b){
		binds.add(b);
	}
}
