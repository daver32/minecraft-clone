package tools;

import org.lwjgl.util.vector.Vector3f;

public class Colors {

	public static Vector3f rgb(int r, int g, int b){
		return new Vector3f(r / 255f, g / 255f, b / 255f);
	}
}
