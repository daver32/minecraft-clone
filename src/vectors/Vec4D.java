package vectors;

import org.lwjgl.util.vector.Vector4f;

public class Vec4D {
	public double x, y, z, w;
	
	public Vec4D(){}
	
	public Vec4D(double x, double y, double z, double w){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Vec4D(Vec4D in){
		x = in.x;
		y = in.y;
		z = in.z;
		w = in.w;
	}
	
	public Vec4D(Vector4f in){
		x = in.x;
		y = in.y;
		z = in.z;
		w = in.w;
	}


	////////////////////////////
	
	public void scale(double v){
		x *= v;
		y *= v;
		z *= v;
		w *= v;
	}
	
	public void load(Vec4D v){
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
	}
	
	public void add(Vec4D a){
		x += a.x;
		y += a.y;
		z += a.z;
		w += a.w;
	}
	
	public void sub(Vec4D a){
		x -= a.x;
		y -= a.y;
		z -= a.z;
		w -= a.w;
	}
	
	public Vec4D clone(){
		return new Vec4D(this);
	}
	
	public double calcSize(){
		return Math.sqrt(x*x + y*y + z*z + w*w);
	}
	
	public void normalise(){
		double size = calcSize();
		x /= size;
		y /= size;
		z /= size;
		w /= size;
	}
	
	public void multiply(double m){
		x *= m;
		y *= m;
		z *= m;
		w *= m;
	}
	
	public String toString(){
		return x + ", " + y + ", " + z + ", " + w;
	}
	
	public Vector4f getVector4f(){
		return new Vector4f((float)x, (float)y, (float)z, (float)w);
	}
	
	////////////////////////////
	
	public static double calcDistance(Vec4D vec1, Vec4D vec2){
		return Math.sqrt(Math.pow(vec1.x - vec2.x, 2) + Math.pow(vec1.y - vec2.y, 2) + Math.pow(vec1.z - vec2.z, 2) + Math.pow(vec1.w - vec2.w, 2));
	}
	
	public static double calcPseudoDistance(Vec4D vec1, Vec4D vec2){
		return Math.abs(vec1.x - vec2.x) + Math.abs(vec1.y - vec2.y) + Math.abs(vec1.z - vec2.z) + Math.abs(vec1.w - vec2.w);
	}
	
	public static double dotProduct(Vec4D v1, Vec4D v2){
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z + v1.w * v2.w;
	}
}
