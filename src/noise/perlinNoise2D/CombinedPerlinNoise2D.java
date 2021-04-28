package noise.perlinNoise2D;

import java.util.ArrayList;

public class CombinedPerlinNoise2D {
	
	private double seed;
	private ArrayList<PerlinNoiseLayer> layers = new ArrayList<PerlinNoiseLayer>();
	
	public CombinedPerlinNoise2D(double seed){
		this.seed = seed;
	}
	
	public void addLayer(double scale, double factor){
		PerlinNoiseLayer l = new PerlinNoiseLayer();
		l.noise = new PerlinNoise2D(seed, scale);
		l.factor = factor;
		layers.add(l);
	}
	
	public double sample(double x, double y){
		double totalFactor = 0;
		double result = 0;
		for(PerlinNoiseLayer l : layers){
			totalFactor += l.factor;
		}
		for(PerlinNoiseLayer l : layers){
			result += l.noise.sample(x, y) * (l.factor / totalFactor);
		}
		return result;
	}
}
