package voxelGame.physics;


import vectors.Vec3D;

public class HitboxAABB {

	private Vec3D position;
	private Vec3D size;
	private Vec3D[] transformedPoints;
	private Vec3D[] rawPoints;
	
	public HitboxAABB(Vec3D position, Vec3D size){
		this.position = position;
		this.size = size;
		calcPoints();
	}
	
	private void calcPoints(){
		calcRawPoints();
		calcTransformedPoints();
	}
	
	protected boolean oneSideIntersection(HitboxAABB hitbox){
		Vec3D[] minMax = calcMinMax(hitbox.getTransformedPoints());
		
		for(Vec3D p : transformedPoints){
			if(
					p.x >= minMax[0].x && p.x <= minMax[1].x && 
					p.y >= minMax[0].y && p.y <= minMax[1].y && 
					p.z >= minMax[0].z && p.z <= minMax[1].z){
				return true;
			}
		}
		
		
		
		return false;
	}
	
	private Vec3D[] calcMinMax(Vec3D[] points){
		Vec3D min = new Vec3D(points[0].x, points[0].y, points[0].z);
		Vec3D max = new Vec3D(points[0].x, points[0].y, points[0].z);
		
		for(int i = 1; i < points.length; i++){
			Vec3D point = points[i];
			
			if(point.x < min.x){
				min.x = point.x;
			}else if(point.x > max.x){
				max.x = point.x;
			}
			
			if(point.y < min.y){
				min.y = point.y;
			}else if(point.y > max.y){
				max.y = point.y;
			}
			
			if(point.z < min.z){
				min.z = point.z;
			}else if(point.z > max.z){
				max.z = point.z;
			}
		}
		
		return new Vec3D[]{min, max};
	}
	
	public boolean calcIntersection(HitboxAABB hitbox){
		return hitbox.oneSideIntersection(this) || oneSideIntersection(hitbox);
	}
	
	private void calcRawPoints(){
		rawPoints = new Vec3D[]{
				new Vec3D(0, 0, 0),
				new Vec3D(0, 0, 0 + size.z),
				new Vec3D(0, 0 + size.y, 0),
				new Vec3D(0, 0 + size.y, 0 + size.z),
				new Vec3D(0 + size.x, 0, 0),
				new Vec3D(0 + size.x, 0, 0 + size.z),
				new Vec3D(0 + size.x, 0 + size.y, 0),
				new Vec3D(0 + size.x, 0 + size.y, 0 + size.z),
		};
	}
	
	private void calcTransformedPoints(){
		transformedPoints = new Vec3D[]{
				new Vec3D(position.x, position.y, position.z),
				new Vec3D(position.x, position.y, position.z + size.z),
				new Vec3D(position.x, position.y + size.y, position.z),
				new Vec3D(position.x, position.y + size.y, position.z + size.z),
				new Vec3D(position.x + size.x, position.y, position.z),
				new Vec3D(position.x + size.x, position.y, position.z + size.z),
				new Vec3D(position.x + size.x, position.y + size.y, position.z),
				new Vec3D(position.x + size.x, position.y + size.y, position.z + size.z),
		};
	}
	
	public boolean isPointInside(Vec3D p){
		Vec3D[] minMax = calcMinMax(transformedPoints);
		
		return (p.x >= minMax[0].x && p.x <= minMax[1].x && 
				p.y >= minMax[0].y && p.y <= minMax[1].y && 
				p.z >= minMax[0].z && p.z <= minMax[1].z);
	}

	public Vec3D getPosition() {
		return position;
	}

	public void setPosition(Vec3D position) {
		this.position = position;
		calcPoints();
	}
	
	public void setPositionAndSize(Vec3D position, Vec3D size) {
		this.position = position;
		this.size = size;
		calcPoints();
	}

	public Vec3D getSize() {
		return size;
	}

	public void setSize(Vec3D size) {
		this.size = size;
		calcPoints();
	}

	public Vec3D[] getTransformedPoints() {
		return transformedPoints;
	}

	public Vec3D[] getRawPoints() {
		return rawPoints;
	}
}
