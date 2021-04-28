package vectors;

import org.lwjgl.util.vector.Vector3f;

public class Vec3D {
	public double x, y, z;
	
	public Vec3D(){}
	
	public Vec3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3D(Vec3D in){
		x = in.x;
		y = in.y;
		z = in.z;
	}
	
	public Vec3D(Vec3I in){
		x = in.x;
		y = in.y;
		z = in.z;
	}
	
	public Vec3D(Vector3f in){
		x = in.x;
		y = in.y;
		z = in.z;
	}


	////////////////////////////
	
	public void scale(double v){
		x *= v;
		y *= v;
		z *= v;
	}
	
	public void load(Vec3D v){
		x = v.x;
		y = v.y;
		z = v.z;
	}
	
	public void add(Vec3D a){
		x += a.x;
		y += a.y;
		z += a.z;
	}
	
	public void sub(Vec3D a){
		x -= a.x;
		y -= a.y;
		z -= a.z;
	}
	
	public Vec3D clone(){
		return new Vec3D(this);
	}
	
	public double calcSize(){
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	public void normalise(){
		double size = calcSize();
		x /= size;
		y /= size;
		z /= size;
	}
	
	public void multiply(double m){
		x *= m;
		y *= m;
		z *= m;
	}
	
	public String toString(){
		return x + ", " + y + ", " + z;
	}
	
	public Vector3f getVector3f(){
		return new Vector3f((float)x, (float)y, (float)z);
	}
	
	////////////////////////////
	
	public static double calcDistance(Vec3D vec1, Vec3D vec2){
		return Math.sqrt(Math.pow(vec1.x - vec2.x, 2) + Math.pow(vec1.y - vec2.y, 2) + Math.pow(vec1.z - vec2.z, 2));
	}
	
	public static double calcPseudoDistance(Vec3D vec1, Vec3D vec2){
		return Math.abs(vec1.x - vec2.x) + Math.abs(vec1.y - vec2.y) + Math.abs(vec1.z - vec2.z);
	}
	
	public static double dotProduct(Vec3D v1, Vec3D v2){
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}
}
