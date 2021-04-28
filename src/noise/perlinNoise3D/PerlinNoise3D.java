package noise.perlinNoise3D;

public class PerlinNoise3D {
	
	private double seedX, seedY, seedZ, scale;
	
	public PerlinNoise3D(double seed, double scale){
		seedX = seed * 6.14515156;
		seedY = seed * 6.454564564;
		seedZ = seed * 6.35245;
		this.scale = scale;
	}
	
	public boolean sample(double x, double y, double z, double treshold){
		//System.out.println(sample(x, y, z));
		return sample(x, y, z) < treshold;
	}
	
	public double sample(double x, double y, double z){
		double floorX = Math.floor(x / scale);
		double floorY = Math.floor(y / scale);
		double floorZ = Math.floor(z / scale);
		
		double startX = floorX * scale;
		double startY = floorY * scale;
		double startZ = floorZ * scale;
		
		double u = (x - startX) / scale;
		double v = (y - startY) / scale;
		double t = (z - startZ) / scale;
		
		
		double[][] cornerVectors = getVectors(startX, startY, startZ);
		double[][] toCornerVectors = new double[][]{
			new double[]{x - (startX+scale), y - (startY+scale), z - (startZ+scale)},
			new double[]{x - (startX+scale), y - (startY+scale), z - (startZ)},
			new double[]{x - (startX+scale), y - (startY), z - (startZ+scale)},
			new double[]{x - (startX+scale), y - (startY), z - (startZ)},
			new double[]{x - (startX), y - (startY+scale), z - (startZ+scale)},
			new double[]{x - (startX), y - (startY+scale), z - (startZ)},
			new double[]{x - (startX), y - (startY), z - (startZ+scale)},
			new double[]{x - (startX), y - (startY), z - (startZ)},
		};
		
		double[] dotProducts = new double[8];
		for(int i = 0; i < 8; i++){
			double dot = dot3D(cornerVectors[i], toCornerVectors[i]);
			dotProducts[i] = dot;
		}
		

		return (interp(dotProducts, u, v, t)/scale+1)/2;
	}
	
	private double interp(double[] dotProducts, double relativeX, double relativeY, double relativeZ){
		double result = 0;
		relativeX = smoothFunction(relativeX);
		relativeY = smoothFunction(relativeY);
		relativeZ = smoothFunction(relativeZ);

		result += dotProducts[7] * (1-relativeX) * (1-relativeY) * (1-relativeZ);
		result += dotProducts[6] * (1-relativeX) * (1-relativeY) * (relativeZ);
		result += dotProducts[5] * (1-relativeX) * (relativeY) * (1-relativeZ);
		result += dotProducts[4] * (1-relativeX) * (relativeY) * (relativeZ);
		result += dotProducts[3] * (relativeX) * (1-relativeY) * (1-relativeZ);
		result += dotProducts[2] * (relativeX) * (1-relativeY) * (relativeZ);
		result += dotProducts[1] * (relativeX) * (relativeY) * (1-relativeZ);
		result += dotProducts[0] * (relativeX) * (relativeY) * (relativeZ);


		return result;
	}
	
	private double smoothFunction(double x){
		x = 6*Math.pow(x, 5) - 15*Math.pow(x, 4) + 10*Math.pow(x, 3);
		return x;
	}
	
	private double[][] getVectors(double floorX, double floorY, double floorZ){
		double[][] coords = new double[][]{
			new double[]{floorX+scale, floorY+scale, floorZ+scale},
			new double[]{floorX+scale, floorY+scale, floorZ},
			new double[]{floorX+scale, floorY, floorZ+scale},
			new double[]{floorX+scale, floorY, floorZ},
			new double[]{floorX, floorY+scale, floorZ+scale},
			new double[]{floorX, floorY+scale, floorZ},
			new double[]{floorX, floorY, floorZ+scale},
			new double[]{floorX, floorY, floorZ},
		};
		
		double[][] result = new double[8][3];
		
		for(int i = 0; i < 8; i++){
			double x = coords[i][0];
			double y = coords[i][1];
			double z = coords[i][2];
			result[i] = new double[]{(rand(x, y, z, seedX)-0.5)*2, (rand(x, y, z, seedY)-0.5)*2, (rand(x, y, z, seedZ)-0.5)*2};
		}
		
		return result;
	}
	
	private static double rand(double x, double y, double z, double seed){
		//43758.5453
	    return fract(Math.sin(dot(x, y, z, 12.9898, 78.233, 145.5521)) * seed);
	}
	
	private static double dot3D(double[] v1, double[] v2){
		return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
	}
	
	private static double dot(double x1, double y1, double z1, double x2, double y2, double z2){
		return x1*x2 + y1*y2 + z1*z2;
	}
	
	private static double fract(double x){
		return x - Math.floor(x);
	}
}