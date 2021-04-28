package noise.perlinNoise1D;

public class PerlinNoise1D {
	
	private double seedX, scale;
	
	public PerlinNoise1D(double seed, double scale){
		seedX = seed;
		this.scale = scale;
	}
	
	public double sample(double x){
		double floorX = Math.floor(x / scale);
		
		double startX = floorX * scale;
		
		double u = (x - startX) / scale;
		
		double[] cornerVectors = getVectors(startX);
		double[] toCornerVectors = new double[]{
			x - (startX),
			x - (startX + scale),
		};
		
		double[] dotProducts = new double[2];
		for(int i = 0; i < 2; i++){
			double dot = cornerVectors[i] * toCornerVectors[i];
			dotProducts[i] = dot;
		}
		

		return (interp(dotProducts, u)/scale+1)/2;
	}
	
	private double interp(double[] dotProducts, double relativeX){
		double result = 0;
		relativeX = smoothFunction(relativeX);

		result += dotProducts[0] * (1-relativeX);
		result += dotProducts[1] * relativeX;

		return result;
	}
	
	private double smoothFunction(double x){
		x = 6*Math.pow(x, 5) - 15*Math.pow(x, 4) + 10*Math.pow(x, 3);
		return x;
	}
	
	private double[] getVectors(double floorX){
		double[] coords = new double[]{
			floorX,
			floorX + scale,
		};
		
		double[] result = new double[2];
		
		for(int i = 0; i < 2; i++){
			double x = coords[i];
			result[i] = (rand(x, seedX)-0.5)*2;
		}
		
		return result;
	}
	
	private static double rand(double x, double seed){
		//43758.5453
	    return fract(Math.sin(dot(x, seed))*43758.5453);
	}
	
	private static double dot(double x1, double x2){
		return x1*x2;
	}
	
	private static double fract(double x){
		return x - Math.floor(x);
	}
}
