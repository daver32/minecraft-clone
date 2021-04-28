package noise;

public class RandNoise {

	private double seedX;
	private double seedY;
	
	public RandNoise(double seed){
		this.seedX = seed * 46552.5453;
		seedY = 43758.5453 * seed;
	}
	
	public double sample(double x, double y){
		//43758.5453
	    return fract(Math.sin(dot(x, y , seedX, seedY))*43758.5453);
	}
	
	private static double dot(double x1, double y1, double x2, double y2){
		return x1*x2 + y1*y2;
	}
	
	private static double fract(double x){
		return x - Math.floor(x);
	}
}
