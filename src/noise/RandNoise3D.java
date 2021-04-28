package noise;

public class RandNoise3D {

	private double seedX, seedY, seedZ;
	
	public RandNoise3D(double seed){
		this.seedX = seed * 139.4122;
		this.seedY = seed * 141.1555;
		this.seedZ = seed * 158.5124;
	}
	
	public double sample(double x, double y, double z){
		//43758.5453
	    return fract(Math.sin(dot(x, y, z , seedX, seedY, seedZ))*43758.5453);
	}
	
	private static double dot(double x1, double y1, double z1, double x2, double y2, double z2){
		return x1*x2 + y1*y2 + z1*z2;
	}
	
	private static double fract(double x){
		return x - Math.floor(x);
	}
}
