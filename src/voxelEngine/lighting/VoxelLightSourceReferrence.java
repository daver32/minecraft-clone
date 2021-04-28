package voxelEngine.lighting;

public class VoxelLightSourceReferrence {
	public int x, y, z;
	public float intensity;
	
	public VoxelLightSourceReferrence(int x, int y, int z, float intensity) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.intensity = intensity;
	}
}
