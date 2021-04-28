package voxelEngine;

import voxelGame.entities.Entity;

public class Chunk {
	public static final int WIDTH = 32;
	public static final int HEIGHT = 256;
	
	public static final int NUM_ARRAYS = HEIGHT / WIDTH;
	
	//each chunk contains a fixed amount of voxel arrays to help optimisation
	public VoxelArray[] voxelArrays = new VoxelArray[NUM_ARRAYS];
	
	public boolean[] updates = new boolean[NUM_ARRAYS]; //Indicates which VAOs of the chunk need an update.
	public VoxelGeomArray[] voxelVaoArrays = new VoxelGeomArray[NUM_ARRAYS];
	public VoxelVao[] vaos = new VoxelVao[NUM_ARRAYS];
	
	//order: +x; -x; +z; -z
	public Chunk[] neighbors = new Chunk[4];
	
	public final int wPosX, wPosZ;
	
	public boolean generated = false;
	
	public Chunk(int wPosX, int wPosZ) {
		this.wPosX = wPosX;
		this.wPosZ = wPosZ;
		
		for(int i = 0; i < NUM_ARRAYS; i++){
			voxelArrays[i] = new VoxelArray(WIDTH, WIDTH, WIDTH, wPosX, i*WIDTH, wPosZ, this);
		}
		for(int i = 0; i < NUM_ARRAYS; i++){
			try{
				voxelArrays[i].neighbors[4] = voxelArrays[i+1];
			}catch(ArrayIndexOutOfBoundsException e){}
			try{
				voxelArrays[i].neighbors[5] = voxelArrays[i-1];
			}catch(ArrayIndexOutOfBoundsException e){}
			
		}
	}
	
	public Voxel getAt(int x, int y, int z){
		try{
			VoxelArray arr = voxelArrays[y / WIDTH];
			
			return arr.getAt(x, y % WIDTH, z);
			
		}catch(ArrayIndexOutOfBoundsException e){}
		return null;
	}
	
	public boolean setAt(Entity causer, int x, int y, int z, Voxel v, boolean updateNeeded, boolean priority){
		try{
			VoxelArray arr = voxelArrays[y / WIDTH];
			
			
			
			return arr.setAt(causer, x, y % WIDTH, z, v, updateNeeded, priority);
			
		}catch(ArrayIndexOutOfBoundsException e){}
		return false;
	}
	
	public boolean setAtWithBounds(int x, int y, int z, Voxel v){
		try{
			VoxelArray arr = voxelArrays[y / WIDTH];
			arr.createArray();
			
			arr.voxels[x][y%WIDTH][z] = v;
			return true;
			
		}catch(Exception e){}
		return false;
	}
	
	public void addNeighbor(Chunk n, int direction){
		neighbors[direction] = n;
		for(int i = 0; i < NUM_ARRAYS; i++){
			voxelArrays[i].neighbors[direction] = n.voxelArrays[i];
		}
	}
	
	public void removeNeighbor(int direction){
		for(int i = 0; i < NUM_ARRAYS; i++){
			voxelArrays[i].neighbors[direction] = null;
		}
		neighbors[direction] = null;
	}
	
	public void updateAllVaos(){
		for(VoxelArray a : voxelArrays){
			a.convertToVaoArray();
		}
	}
	
	public boolean allNeighborsSet(){
		for(Chunk c : neighbors){
			if(c == null){
				return false;
			}
		}
		return true;
	}
	
	public boolean allNeighborsGenerated(){
		for(Chunk c : neighbors){
			if(!c.generated){
				return false;
			}
		}
		return true;
	}
	
	void updateVaoAt(int y, boolean priority){
		VoxelArray arr = voxelArrays[y / WIDTH];
		arr.update(priority);
	}
}
