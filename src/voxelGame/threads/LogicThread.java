package voxelGame.threads;

import org.lwjgl.input.Mouse;

import tools.MTArray;
import vectors.Vec3D;
import voxelGame.entities.Entity;
import voxelGame.entities.Explosion;
import voxelGame.entities.Particle;
import voxelGame.entities.Player;

// A thread that updates the world. It contains the main game loop.

public class LogicThread extends Thread{
	
	private static LogicThread instance;
	
	private LogicThread(){}
	
	public static boolean stop = false;
	
	public static Player player = new Player();
	
	public static final int TICKRATE = 100;
	public static final double MS_PER_TICK = 1000.0 / TICKRATE;
	
	
	public static MTArray<Particle> particles = new MTArray<Particle>();
	public static MTArray<Entity> entities = new MTArray<Entity>();

	public static void runThread(){
		instance = new LogicThread();
		instance.start();
	}

	public void run() {
		System.out.println("Logic thread: running");

		while(!WorldManagerThread.initialized){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}
		
		double lastTickTime = System.nanoTime() / 1e6;
		double t = 0;
		while(!WorldManagerThread.initialized){
			delay(10);
		}
		while(WorldLoaderThread.getQueueLength() > 0){
			delay(10);
		}
		
		while(!stop){

			double time = System.nanoTime() / 1e6;
			double timeElapsed = time - lastTickTime;
			if(timeElapsed > MS_PER_TICK){
				player.update(timeElapsed);
				updateParticles(timeElapsed);
				updateEntities(timeElapsed);
				
				try{
					if(Mouse.isButtonDown(3)){
						/*
						Vector3f p0 = player.getPosition();
						Vector3f d = player.getForwardVec();
						Vector3f p = new Vector3f(p0.x + d.x, p0.y + d.y, p0.z + d.z);
						Vector3f c = new Vector3f(0.8f, 0.3f, 0);
						ParticleEmitter.emitParticles(player, p.x-0.1f, p.y-0.1f, p.z-0.1f, p.x+0.1f, p.y+0.1f, p.z+0.1f, c, 0.1f, 0.01f, 0.001f, 5, 10, 0.1f, 0.2f, 5000, 10000);
						//System.out.println(particles.size());
						*/
						
						if(time - t > 100){
							try{
								Explosion.addExplosion(new Vec3D(player.getDestroyPosition()), 100);
								t = time;
							}catch(NullPointerException e){
								
							}

						}

					}
					delay(1);
				}catch(IllegalStateException e){}

				lastTickTime = time;

			}

		}
		
		System.out.println("Logic thread: stopped");
	}
	
	private static void delay(long ms){try{Thread.sleep(ms);}catch(InterruptedException e){}}
	
	private void updateParticles(double timeElapsed){
		for(Particle p : particles.clone()){
			if(p == null || p.update(timeElapsed)){
				particles.remove(p);
			}
		}
	}
	
	private void updateEntities(double timeElapsed){
		for(Entity e : entities.clone()){
			if(e == null || e.update(timeElapsed)){
				entities.remove(e);
			}
		}
	}
	
	public void end(){
		stop = true;
	}

	static LogicThread getInstance() {
		return instance;
	}
	
	
}
