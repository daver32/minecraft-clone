package voxelEngine.lighting;

import voxelEngine.Voxel;
import voxelEngine.World;

public class Lighting {

	public static void addLight(World w, int x, int y, int z, int range){
		for(int ix = x - range; ix < x + range; ix++){
			for(int iy = y - range; iy < y + range; iy++){
				for(int iz = z - range; iz < z + range; iz++){
					Voxel v = w.getAt(ix, iy, iz);
					if(v != null){
						v.lightRefs.add(new VoxelLightSourceReferrence(x, y, z, 1));
					}
				}
			}
		}
	}
	
}
