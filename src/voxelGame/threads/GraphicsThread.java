package voxelGame.threads;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.display.DisplayManager;
import renderEngine.entities.Camera;
import renderEngine.entities.RenderEntity;
import renderEngine.entities.Sun;
import renderEngine.rendering.MasterRenderer;
import renderEngine.rendering.Scene;
import renderEngine.rendering.simpleParticles.SimpleParticle;
import renderEngine.resources.ResourceManager;
import renderEngine.resources.materialResources.MaterialResources;
import renderEngine.resources.skyboxResources.SkyboxResources;
import renderEngine.tools.Loader;
import renderEngine.tools.Screenshotter;
import tools.MTArray;
import vectors.Vec3D;
import vectors.Vec3I;
import voxelEngine.Chunk;
import voxelEngine.VoxelGeomArray;
import voxelEngine.VoxelVao;
import voxelEngine.World;
import voxelGame.entities.Entity;
import voxelGame.entities.Particle;

/*
 * A thread that contains the Opengl context. It's responsive for running
 * the rendering engine and listening to user input. 
*/

public class GraphicsThread {


	private static boolean stop = false;

	public static Vector3f pos = new Vector3f();
	
	public static MasterRenderer renderer;
	
	public static MTArray<VoxelGeomArray> geometryToLoad = new MTArray<VoxelGeomArray>();
	public static MTArray<VoxelVao> vaosToRemove = new MTArray<VoxelVao>();

	public static void run(){
		System.out.println("Graphics thread: running");
		
		DisplayManager.createDisplay(true, 1000, 600);
		ResourceManager.init();
		renderer = new MasterRenderer();

		
		VoxelVao.addTexture(MaterialResources.get("grass")); // 0
		VoxelVao.addTexture(MaterialResources.get("grassSide"));
		VoxelVao.addTexture(MaterialResources.get("dirt"));
		VoxelVao.addTexture(MaterialResources.get("rock"));
		VoxelVao.addTexture(MaterialResources.get("tallGrass"));
		VoxelVao.addTexture(MaterialResources.get("test"));
		VoxelVao.addTexture(MaterialResources.get("wood"));
		VoxelVao.addTexture(MaterialResources.get("leaves"));
		VoxelVao.addTexture(MaterialResources.get("woodPlank"));
		VoxelVao.addTexture(MaterialResources.get("grassSideRight"));
		VoxelVao.addTexture(MaterialResources.get("grassSideLeft")); // 10
		VoxelVao.addTexture(MaterialResources.get("grassSideCenter"));
		VoxelVao.addTexture(MaterialResources.get("sand"));
		
		Scene scene = new Scene();
		
		Camera c = new Camera();
		scene.skybox = SkyboxResources.get("sky7");
		Vector3f sdir = new Vector3f(1,1.5f,2);
		sdir.normalise();
		scene.sun = new Sun(sdir, new Vector3f(1.05f,1,0.95f), 1.8f);
		Mouse.setGrabbed(true);
		
		RenderEntity selectionCube = new RenderEntity("cube", "frame");
		selectionCube.setScale(0.55f);
		selectionCube.setCastsShadow(false);

		RenderEntity testCube = new RenderEntity("cube", "stone");
		testCube.setScale(1);

		
		while(!WorldManagerThread.initialized){
			delay(10);
		}
		
		int fps = 0;
		long lastFpsPrint = 0;
		long lastScreen = 0;
		
		while(WorldLoaderThread.getQueueLength() > 0){
			delay(10);
		}
		
		scene.fogBorders = new Vector2f(
			(World.DEF_RENDER_DISTANCE - 1) * Chunk.WIDTH,
			(World.DEF_RENDER_DISTANCE - 0) * Chunk.WIDTH
		);
		
		while(!stop && !Display.isCloseRequested()){
			loadGeometry();
			removeVaos();
			
			Vec3D centerPos = LogicThread.player.getPosition();
			
			ArrayList<VoxelVao> vaos = WorldManagerThread.world.getVaos(centerPos);
			scene.voxelChunks = vaos;
			scene.simpleParticles = retrieveParticles();

			c = LogicThread.player.getCamera();
			
			Vec3I sel = LogicThread.player.getDestroyPosition();
			if(sel != null){
				Vec3D pos = new Vec3D(sel);
				pos.sub(centerPos);
				pos.x += 0.5; pos.y += 0.5; pos.z += 0.5;
				selectionCube.setPos(pos.getVector3f());
				scene.addEntity(selectionCube);
			}
			
			for(Entity e : LogicThread.entities.clone()){
				if(e != null){

					RenderEntity re = e.getRenderEntity(centerPos);
					if(re != null){
						scene.addEntity(re);
					}
				}
			}

			
			renderer.render(c, 0, scene);
			DisplayManager.updateDisplay();	
			
			
			//Display.setTitle("" + Memory.getUsedMemory() / 1024);
			Display.setTitle(new Vec3I(centerPos).toString() + " " + LogicThread.player.getInHandBlock().getName());
			//System.out.println(VaoArrayGeneratorThread.getInstance().isAlive() + " " + WorldLoaderThread.getInstance().isAlive() + " " + WorldManagerThread.getInstance().isAlive());
			
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
				WorldManagerThread.world.saveAll();
				stop = true;
				while(WorldLoaderThread.chunksToSave.size() > 0){
					delay(10);
				}

			}
			
			fps++;
			long time = System.currentTimeMillis();
			if(time - lastFpsPrint > 1000){
				System.out.println("fps: " + fps + ", free memory: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024 + "MB"));
				lastFpsPrint = time;
				fps = 0;
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_N) && time - lastScreen > 1000){
				DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
				Date date = new Date();
				Vec3I ppos = new Vec3I(LogicThread.player.getPosition());
				Screenshotter.takeScreenshot("screenshots", "png", dateFormat.format(date) + "_x" + ppos.x + "y" + ppos.y + "z" + ppos.z);
				System.out.println("screenshot taken");
				
				lastScreen = time;
			}
			
			scene.entityLists.clear();
		}
		
		Loader.cleanUp();
		
		DisplayManager.closeDisplay();

		GeometryGeneratorThread.stop = true;
		LogicThread.stop = true;
		PhysicsThread.stop = true;
		WorldGenThread.stop = true;
		WorldLoaderThread.stop = true;
		WorldManagerThread.stop = true;
		
		System.out.println("Graphics thread: stopped");
	}
	
	private static void delay(long ms){try{Thread.sleep(ms);}catch(InterruptedException e){}}
	
	private static ArrayList<SimpleParticle> retrieveParticles(){
		ArrayList<SimpleParticle> r = new ArrayList<SimpleParticle>();
		MTArray<Particle> cp = LogicThread.particles.clone();
		Vec3D centerPos = LogicThread.player.getPosition();
		for(Particle p : cp){
			try{
				r.add(p.getGParticle(centerPos));
			}catch(NullPointerException e){}

		}
		return r;
	}
	
	private static void loadGeometry(){
		int s = geometryToLoad.size();
		if(s > 0){
			delay(1);
		}
		
		while(true){

			VoxelGeomArray a = geometryToLoad.get(0);
			geometryToLoad.remove(a);
			
			if(a == null){
				break;
			}else{
				VoxelVao v = a.parentArray.vao;
				if(v != null)v.delete();

				a.parentArray.vao = a.convertToVao();
				a.parentArray.voxelGeomArray = null;
			}
		}
	}
	
	private static void removeVaos(){
		while(true){
			VoxelVao a = vaosToRemove.get(0);
			vaosToRemove.remove(a);
			if(a == null){
				break;
			}else{
				a.delete();
			}
		}
	}
}
