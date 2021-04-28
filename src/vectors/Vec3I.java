package vectors;

import org.lwjgl.util.vector.Vector3f;

public class Vec3I {
	public int x, y, z;
	
	public Vec3I(){}
	
	public Vec3I(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3I(Vec3I in){
		x = in.x;
		y = in.y;
		z = in.z;
	}
	
	public Vec3I(Vec3D in){
		x = (int) in.x;
		y = (int) in.y;
		z = (int) in.z;
	}
	
	public Vec3I(Vector3f in){
		x = (int)in.x;
		y = (int)in.y;
		z = (int)in.z;
	}


	////////////////////////////
	
	public void load(Vec3I v){
		x = v.x;
		y = v.y;
		z = v.z;
	}
	
	public void add(Vec3I a){
		x += a.x;
		y += a.y;
		z += a.z;
	}
	
	public void sub(Vec3I a){
		x -= a.x;
		y -= a.y;
		z -= a.z;
	}
	
	public Vec3I clone(){
		return new Vec3I(this);
	}
	
	public double calcSize(){
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	public void normalize(){
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
	
	public boolean isSameAs(Vec3I v){
		return v != null && x == v.x && y == v.y && z == v.z;
	}
	
	////////////////////////////
	
	public static double calcDistance(Vec3I vec1, Vec3I vec2){
		return Math.sqrt(Math.pow(vec1.x - vec2.x, 2) + Math.pow(vec1.y - vec2.y, 2) + Math.pow(vec1.z - vec2.z, 2));
	}
	
	public static double calcPseudoDistance(Vec3I vec1, Vec3I vec2){
		return Math.abs(vec1.x - vec2.x) + Math.abs(vec1.y - vec2.y) + Math.abs(vec1.z - vec2.z);
	}
	
	public static double dotProduct(Vec3I v1, Vec3I v2){
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}
}
