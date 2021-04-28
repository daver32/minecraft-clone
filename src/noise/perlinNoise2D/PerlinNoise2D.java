package noise.perlinNoise2D;

public class PerlinNoise2D {
	
	private double seedX, seedY, scale;
	
	public PerlinNoise2D(double seed, double scale){
		seedX = seed;
		seedY = seed * 6.454564564;
		this.scale = scale;
	}
	
	public double sample(double x, double y){
		double floorX = Math.floor(x / scale);
		double floorY = Math.floor(y / scale);
		
		double startX = floorX * scale;
		double startY = floorY * scale;
		
		double u = (x - startX) / scale;
		double v = (y - startY) / scale;
		
		
		double[][] cornerVectors = getVectors(startX, startY);
		double[][] toCornerVectors = new double[][]{
			new double[]{x - (startX), 		y - (startY)},
			new double[]{x - (startX + scale), 	y - (startY)},
			new double[]{x - (startX), 		y - (startY + scale)},
			new double[]{x - (startX + scale), 	y - (startY + scale)}
		};
		
		double[] dotProducts = new double[4];
		for(int i = 0; i < 4; i++){
			double dot = dot(cornerVectors[i][0], cornerVectors[i][1], toCornerVectors[i][0], toCornerVectors[i][1]);
			dotProducts[i] = dot;
		}
		

		return (interp(dotProducts, u, v)/scale+1)/2;
	}
	
	private double interp(double[] dotProducts, double relativeX, double relativeY){
		double result = 0;
		relativeX = smoothFunction(relativeX);
		relativeY = smoothFunction(relativeY);

		result += dotProducts[0] * (1-relativeX) * (1-relativeY);
		result += dotProducts[1] * relativeX * (1-relativeY);
		result += dotProducts[2] * (1-relativeX) * relativeY;
		result += dotProducts[3] * relativeX * relativeY;

		return result;
	}
	
	private double smoothFunction(double x){
		x = 6*Math.pow(x, 5) - 15*Math.pow(x, 4) + 10*Math.pow(x, 3);
		return x;
	}
	
	private double[][] getVectors(double floorX, double floorY){
		double[][] coords = new double[][]{
			new double[]{floorX, floorY},
			new double[]{floorX + scale, floorY},
			new double[]{floorX, floorY + scale},
			new double[]{floorX + scale, floorY + scale},
		};
		
		double[][] result = new double[4][2];
		
		for(int i = 0; i < 4; i++){
			double x = coords[i][0];
			double y = coords[i][1];
			result[i] = new double[]{(rand(x, y, seedX)-0.5)*2, (rand(x, y, seedY)-0.5)*2};
		}
		
		return result;
	}
	
	private static double rand(double x, double y, double seed){
		//43758.5453
	    return fract(Math.sin(dot(x, y , 12.9898, 78.233)) * seed);
	}
	
	private static double dot(double x1, double y1, double x2, double y2){
		return x1*x2 + y1*y2;
	}
	
	private static double fract(double x){
		return x - Math.floor(x);
	}
}
