package voxelGame.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.entities.Camera;
import vectors.Vec3D;
import vectors.Vec3I;
import voxelEngine.Chunk;
import voxelEngine.Voxel;
import voxelEngine.World;
import voxelGame.blocks.Block;
import voxelGame.physics.HitboxAABB;
import voxelGame.threads.WorldManagerThread;

public class Player extends Entity{
	
	private Camera camera = new Camera();
	private Vec3D forwardVec;
	private Vec3I destroyPosition;
	private Vec3I buildPosition;
	
	private Vec3D position = new Vec3D(0, 0, 0);

	private long buildCooldown = 0;
	private static long buildPeriod = 100;
	
	private long jumpCooldown = 0;
	private static long jumpPeriod = 100;
	private static double jumpPower = 0.17f;
	private boolean isOnGround = false;
	private static double gravity = 0.0004f;
	
	private Vec3D velocity = new Vec3D();
	private static double terminalVelocity = 1;
	private static double runningAccel = 0.005f;
	private static double airSteeringAccel = 0.001f;
	
	private static double groundFriction = 0.05f;
	private static double airFriction = 0.01f;
	
	private boolean noclip = true;
	private boolean noclipSwitched = false;
	
	int chunkX = (int) (position.x / Chunk.WIDTH), chunkZ = (int) (position.z / Chunk.WIDTH);
	
	private int blockID = 1;
	
	private HitboxAABB hitbox = new HitboxAABB(new Vec3D(), new Vec3D(0.5f, 2.45f, 0.5f));
	
	public Player(){
		position.y = 128;
		WorldManagerThread.shiftChunks(chunkX, chunkZ);
		
	}

	public boolean update(double timeElapsed){
		try{

			updateCam(camera);
			updateAimPosition();
			updatePhysics(timeElapsed);
			
			buildCooldown -= timeElapsed;
			if(buildCooldown < 0){buildCooldown = 0;}

			if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
				if(!noclipSwitched){
					noclip = !noclip;
				}
				noclipSwitched = true;
			}else{
				noclipSwitched = false;
			}

			
			
			
			if(Mouse.isButtonDown(0)){
				if(buildCooldown == 0 && destroyBlock()){
					buildCooldown = buildPeriod;
				}
			}else if(Mouse.isButtonDown(1)){
				if(buildCooldown == 0 && buildBlock()){
					buildCooldown = buildPeriod;
				}
			}else{
				buildCooldown = 0;
			}
				
			if(Keyboard.isKeyDown(Keyboard.KEY_1)){
				blockID = 1;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_2)){
				blockID = 2;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_3)){
				blockID = 3;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_4)){
				blockID = 4;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_5)){
				blockID = 5;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_6)){
				blockID = 6;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_7)){
				blockID = 7;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_8)){
				blockID = 8;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_9)){
				blockID = 9;
			}else if(Keyboard.isKeyDown(Keyboard.KEY_0)){
				blockID = 10;
			}
			
			updateChunkPos();
			
			
		}catch(IllegalStateException e){
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	

	
	private boolean collides(Vec3D position){
		if(noclip){
			return false;
		}
		
		
		World w = WorldManagerThread.world;
		hitbox.setPosition(new Vec3D(position.x - 0.25f, position.y - 2.35f, position.z - 0.25f));

		
		Vec3D[] hitboxPoints = hitbox.getTransformedPoints();
		for(Vec3D point : hitboxPoints){
			int ix = (int)Math.floor(point.x);
			int iy = (int)Math.floor(point.y);
			int iz = (int)Math.floor(point.z);
			Block block = (Block) w.getAt(ix, iy, iz);
			if(block != null && block.isSolid()){
				return true;
			}
		}
		/*
		for(int x = -2; x < 2; x++){
			for(int y = -2; y < 2; y++){
				for(int z = -2; z < 2; z++){
					int ix = (int)Math.floor(x + p.x);
					int iy = (int)Math.floor(y + p.y);
					int iz = (int)Math.floor(z + p.z);
					Block block = (Block) w.getAt(ix, iy, iz);
					if(block != null && block.isSolid() && block.getHitbox(ix, iy, iz).calcIntersection(hitbox)){
						return true;
					}
				}
			}
		}
		*/
		
		return false;
	}
	
	private Vec3D calcRightVector(Vec3D forward){
		return new Vec3D(Vector3f.cross(forward.getVector3f(), new Vec3D(0, -1, 0).getVector3f(), null));
	}
	
	private void updatePhysics(double timeElapsed){
		
		Vec3D forward = new Vec3D(forwardVec);

		if(!noclip){
			forward.y = 0;
		}
		
		forward.normalise();
		
		
		Vec3D right = calcRightVector(forward);
		double rightMul = 0;
		double accel =  noclip ? 0.06f : (isOnGround ? runningAccel : airSteeringAccel);
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			rightMul = accel;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			rightMul = -accel;
		}
		right.scale(rightMul);
		
		double forwardMul = 0;
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			forwardMul = accel;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			forwardMul = -accel;
		}
		forward.scale(forwardMul);
		
		Vec3D runAccel = new Vec3D(forward.x + right.x, forward.y + right.y, forward.z + right.z);
		runAccel.scale(timeElapsed);
		
		velocity = addVec(velocity, runAccel);
		
		double friction = (noclip ? 0.1f : (isOnGround ? groundFriction : airFriction)) * timeElapsed;

		velocity.x /= 1f + friction;
		velocity.z /= 1f + friction;
		if(noclip){
			velocity.y /= 1f + friction;
		}


		if(!noclip){
			velocity.y -= gravity * timeElapsed;
		}

		

		if(isOnGround && jumpCooldown <= jumpPeriod){
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
				jumpCooldown = jumpPeriod;
				velocity.y += jumpPower;

			}
		}else{
			jumpCooldown -= timeElapsed;
		}
		
		checkTerminalVelocity();
		addVelocity();
	}
	
	private void addVelocity(){
		Vec3D p = position;
		Vec3D finalPos = new Vec3D(p);
		
		double u;
		
		u = p.x + velocity.x;
		if(!collides(new Vec3D(u, p.y, p.z))){
			finalPos.x = u;
		}else{
			velocity.x /= 2;
		}

		u = p.y + velocity.y;
		if(!collides(new Vec3D(finalPos.x, u, p.z))){
			finalPos.y = u;
			isOnGround = false;
		}else{
			if(velocity.y < 0){
				isOnGround = true;
			}
			velocity.y = 0;
		}
		
		u = p.z + velocity.z;
		if(!collides(new Vec3D(finalPos.x, finalPos.y, u))){
			finalPos.z = u;
		}else{
			velocity.z /= 2;
		}

		position = finalPos;
	}
	
	private void checkTerminalVelocity(){
		double vel = velocity.calcSize();
		if(vel > terminalVelocity){
			velocity.normalise();
			velocity.scale(terminalVelocity);
		}
	}
	
	private void updateChunkPos(){
		Vec3D pos = position;
		int x = (int)pos.x;
		int z = (int)pos.z;
		
		int nChunkX = x / Chunk.WIDTH;
		int nChunkZ = z / Chunk.WIDTH;
		
		if(nChunkX != chunkX || nChunkZ != chunkZ){
			WorldManagerThread.shiftChunks(nChunkX - chunkX, nChunkZ - chunkZ);
			chunkX = nChunkX;
			chunkZ = nChunkZ;
		}
	}
	
	public Block getInHandBlock(){
		return Block.getByID(blockID);
	}
	
	private boolean buildBlock(){
		if(buildPosition != null){
			Block toAdd = Block.getByID(blockID);
			if(toAdd != null){
				int bx = buildPosition.x; int by = buildPosition.y; int bz = buildPosition.z; 
				if(toAdd.isSolid()){
					if(
							hitbox.calcIntersection(toAdd.getHitbox(bx, by, bz))
							){
						return false;
					}

				}
				
				WorldManagerThread.setBlock(this, bx, by, bz, toAdd, 0, true);
				return true;
			}
		}else{}
		return false;
	}
	
	private boolean destroyBlock(){
		if(destroyPosition != null){
			WorldManagerThread.setBlock(this, destroyPosition.x, destroyPosition.y, destroyPosition.z, null, 0, true);
			return true;
		}else{
			return false;
		}
		
	}
	
	private void updateAimPosition(){
		int maxSteps = 10000;
		double resolution = 1e2;
		World w = WorldManagerThread.world;
		

		Vec3D pointer = cloneVec(position);
		Vec3D adder = cloneVec(forwardVec);
		adder.normalise();
		//adder = new Vec3D(v.x, v.y, v.z);
		adder.x /= resolution; adder.y /= resolution; adder.z /= resolution;
		
		Vec3I build = null;
		for(int i = 0; i < maxSteps; i++){
			pointer = addVec(pointer, adder);
			int px = (int) Math.floor(pointer.x);
			int py = (int) Math.floor(pointer.y);
			int pz = (int) Math.floor(pointer.z);	
			
			Voxel vox = w.getAt(px, py, pz);
			if(vox != null && vox.isSolid()){

				
				destroyPosition = new Vec3I(px, py, pz);
				buildPosition = build;
				return;
			}
			
			build = new Vec3I(px, py, pz);
		}
		destroyPosition = buildPosition = null;
	}
	
	private void updateCam(Camera c){
		
		double pitchRads = -Math.toRadians((int)c.getPitch());
		double yawRads = -Math.toRadians((int)c.getYaw()-90);
		
		double xzLen = Math.cos(pitchRads);
		forwardVec = new Vec3D((float)(xzLen * Math.cos(yawRads)), (float)Math.sin(pitchRads), (float)(xzLen * Math.sin(-yawRads)));

		int hw = Display.getWidth()/2;
		int hh = Display.getHeight()/2;
		
		float xdiff = (Mouse.getX() - hw) * 0.1f;
		float ydiff = (Mouse.getY() - hh) * 0.1f;
		
		Vector3f rot = c.getRotation();

		rot.y += xdiff;
		rot.x -= ydiff;
		
		rot.x = Math.max(Math.min(rot.x, 90), -90);
		
		Mouse.setCursorPosition(hw, hh);
	}

	public Camera getCamera() {
		return camera;
	}
	
	private Vec3D cloneVec(Vec3D v){
		return new Vec3D(v.x, v.y, v.z);
	}
	
	private static Vec3D addVec(Vec3D v1, Vec3D v2){
		return new Vec3D(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}
	
	@SuppressWarnings("unused")
	private static float clamp(float x, float min, float max){
		if(x < min){
			return min;
		}else if(x > max){
			return max;
		}
		return x;
	}

	public Vec3I getDestroyPosition() {
		return destroyPosition;
	}

	@Override
	public Vec3D getPosition() {
		return position;
	}

	public Vec3D getForwardVec() {
		return forwardVec;
	}
	
	
}
