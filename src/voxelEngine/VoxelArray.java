package voxelEngine;

import voxelGame.entities.Entity;
import voxelGame.threads.GeometryGeneratorThread;
import voxelGame.threads.WorldManagerThread;

public class VoxelArray {
	public Voxel[][][] voxels;
	public boolean isEmpty = true;
	public final int width, height, depth;
	public VoxelArray[] neighbors = new VoxelArray[6]; // +X; -X; +Z; -Z; +Y; -Y
	
	public VoxelVao vao;
	public VoxelGeomArray voxelGeomArray;
	
	public final Chunk parentChunk;
	
	public final int wPosX, wPosY, wPosZ;
	
	

	public VoxelArray(int width, int height, int depth, int x, int y, int z, Chunk parent){
		this.width = width;
		this.height = height;
		this.depth = depth;
		wPosX = x;
		wPosY = y;
		wPosZ = z;
		
		parentChunk = parent;
	}
	
	public VoxelGeomArray convertToVaoArray(){
		return GeometryGenerator.convertToVaoArray(this);
	}
	
	public Voxel getAt(int x, int y, int z){
		try{
			return voxels[x][y][z];
		}catch(NullPointerException e){
			return null;
		}catch(ArrayIndexOutOfBoundsException e){}
		
		VoxelArray resArray = null;
		if(x >= width){
			x -= width;
			resArray = neighbors[0];
		}else if(x < 0){
			x += width;
			resArray = neighbors[1];
			
		}else if(y >= height){
			y -= height;
			resArray = neighbors[4];
		}else if(y < 0){
			y += height;
			resArray = neighbors[5];
			
		}else if(z >= depth){
			z -= depth;
			resArray = neighbors[2];
		}else if(z < 0){
			z += depth;
			resArray = neighbors[3];
		}
		
		if(resArray != null){
			return resArray.getAt(x, y, z);
		}
		
		return null;
	}
	
	public boolean setAt(Entity causer, int x, int y, int z, Voxel v, boolean updateNeeded, boolean priority){
		try{
			try{
				if(isEmpty && v != null){
					voxels = new Voxel[width][height][depth];
					isEmpty = false;
				}
				
				if(v == null){
					voxels[x][y][z].onDestroy(causer, WorldManagerThread.world, wPosX+x, wPosY+y, wPosZ+z);
				}
				voxels[x][y][z] = v;

				
			}catch(NullPointerException e){
				return false;
			}
			

			if(updateNeeded){

				update(priority);
				
				try{
					if(x == 0){
						neighbors[1].update(priority);
					}else if(x == width-1){
						neighbors[0].update(priority);
					}
				}catch(NullPointerException e){}

				try{
					if(y == 0){
						neighbors[5].update(priority);
					}else if(y == height-1){
						neighbors[4].update(priority);
					}
				}catch(NullPointerException e){}
				
				try{
					if(z == 0){
						neighbors[3].update(priority);
					}else if(z == depth-1){
						neighbors[2].update(priority);
					}
				}catch(NullPointerException e){}
			}
			

			
			return true;
		}catch(ArrayIndexOutOfBoundsException e){}
		
		VoxelArray resArray = null;
		if(x >= width){
			x -= width;
			resArray = neighbors[0];
		}else if(x < 0){
			x += width;
			resArray = neighbors[1];
			
		}else if(y >= height){
			y -= height;
			resArray = neighbors[4];
		}else if(y < 0){
			y += height;
			resArray = neighbors[5];
			
		}else if(z >= depth){
			z -= depth;
			resArray = neighbors[2];
		}else if(z < 0){
			z += depth;
			resArray = neighbors[3];
		}
		
		if(resArray != null){
			return resArray.setAt(causer, x, y, z, v, updateNeeded, priority);
		}
		
		return false;
	}
	
	public void update(boolean priority){
		GeometryGeneratorThread.addArray(this, priority);
	}
	
	public void createArray(){
		if(isEmpty){
			voxels = new Voxel[width][height][depth];
			isEmpty = false;
		}
	}
	
}
