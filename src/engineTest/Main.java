package engineTest;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.entities.Light;
import voxelEngine.Voxel;
import voxelEngine.VoxelArray;
import voxelEngine.VoxelGeomArray;
import voxelEngine.VoxelVao;
import voxelGame.blocks.DirtBlock;

public class Main {
	
	public static boolean stop = false;

	public static VoxelVao createVao(){
		
		VoxelArray arr = new VoxelArray(16, 16, 16, 0, 0, 0, null);
		for(int x = 0; x < 16; x++){
			for(int y = 0; y < 16; y++){
				for(int z = 0; z < 16; z++){
					

					if(Math.sqrt(Math.pow(x-7, 2) + Math.pow(y-7, 2) + Math.pow(z-7, 2)) > 7.5){
						@SuppressWarnings("unused")
						Voxel v = new DirtBlock();

						
							
						//arr.voxels[x][y][z] = v;
							
					}
					
					
					
					
					
				}
			}
		}
		
		//arr.voxels[5][5][5] = new Voxel(1);
		
		
		//arr.removeInvalidVoxels();
		
		long sttime = System.nanoTime();
		VoxelGeomArray array = arr.convertToVaoArray();
		System.out.println((System.nanoTime() - sttime) / 1e6);
		return array.convertToVao();
	}
	
	public static void main(String[] args) {
		/*
		DisplayManager.createDisplay(false, 800, 600);

		ResourceManager.init();
		
		MasterRenderer renderer = new MasterRenderer();
		
		
		
		Entity cube = new Entity(new TexturedModel(ModelResources.get("cube"), MaterialResources.get("stone")));
		cube.setScale(2.5f);
		cube.setPos(new Vector3f(0,15,-5));
		
		Entity monkey = new Entity(new TexturedModel(ModelResources.get("monkey"), MaterialResources.get("glass")));
		monkey.setScale(5);
		monkey.setPos(new Vector3f(8,7,0));
		
		Entity teapot = new Entity(new TexturedModel(ModelResources.get("teapot"), MaterialResources.get("glass")));
		teapot.setScale(5);
		teapot.setPos(new Vector3f(-8,7,0));
		
		Entity terr = new Entity(new TexturedModel(ModelResources.get("terrain"), MaterialResources.get("grass")));
		terr.setPos(new Vector3f(0,0,0));
		terr.setScale(10);
		
		Entity transp1 = new Entity(new TexturedModel(ModelResources.get("cube"), MaterialResources.get("rock")));
		transp1.setPos(new Vector3f(0,0,16));
		transp1.setScale(5);
		
		Entity sphere = new Entity("sphere", "ice");
		sphere.setPos(new Vector3f(0,16,16));
		sphere.setScale(5);
		
		Entity cage = new Entity("testCage", "stone");
		cage.setPos(new Vector3f(0,32,16));
		
		Entity room = new Entity("testRoom", "tiles");
		room.setPos(new Vector3f(0,32,16));
		
		Entity gem = new Entity("gem", "glass");
		gem.setPos(new Vector3f(0,5,5));
		
		Entity dragon = new Entity("dragon1", "ice");
		dragon.setPos(new Vector3f(0,2,15));
		
		//VoxelVao vox = createVao();
		VoxelVao.albedoIds[0] = MaterialResources.get("grass").getAlbedoMap().getID();
		VoxelVao.albedoIds[1] = MaterialResources.get("test").getAlbedoMap().getID();
		
		//Entity transp2 = new Entity(new TexturedModel(ModelResources.get("teapot"), MaterialResources.get("glass")));
		//transp2.setPos(new Vector3f(0,0,10));
		//transp2.setScale(5);
		
		Player player = new Player();
		Camera c = player.getCamera();
		
		renderer.setSkybox(SkyboxResources.get("sky1"));
		
		Sun sun = new Sun(new Vector3f(1,1,1), new Vector3f(1.1f,1,0.9f), 1.8f);
		sun = new Sun(new Vector3f(1,1,1), new Vector3f(1.05f,1,0.95f), 1.8f);

		
		renderer.setSun(sun);
		
		Mouse.setGrabbed(true);
		
		
		ArrayList<Light> lights = new ArrayList<Light>();
		float m = 2;
		for(int i = 0; i < m; i++){
			lights.add(new Light(new Vector3f(i / m, 1 - i / m, 1), new Vector3f(i * 2, 10, 0), 30));
		}

		long t = System.currentTimeMillis();
		
		while(!Display.isCloseRequested() && !stop){
			t = System.currentTimeMillis();

			updateLights(lights, t);
			player.update();
			BindManager.update();
				
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
				break;
			}
			
			renderer.addEntity(teapot);
			
			renderer.addEntity(terr);
			
			renderer.addEntity(monkey);
			renderer.addEntity(cube);
			renderer.addEntity(sphere);
			renderer.addEntity(gem);
			renderer.addEntity(dragon);
			
			//renderer.addVoxelVao(vox);
			
			
			//renderer.addEntity(room);
			renderer.addEntity(cage);
			
			//renderer.addEntity(transp1);
			//enderer.addEntity(transp2);
			
			cube.setRotX(0);
			//sun.getDirection().x = (float) Math.sin(x/100.0);
			//sun.getDirection().z = (float) Math.cos(x/100.0);
			//sun.getDirection().z = (float) Math.sin(x/100.0);
			
			for(Light l : lights){
				renderer.addLight(l);
			}
			//renderer.addLight(new Light(new Vector3f(1,1,1), c.getPosition(), 10));
			//renderer.addLight(new Light(new Vector3f(1,1,1), new Vector3f(1,1,10), 100));
			renderer.render(c, 0);


			DisplayManager.updateDisplay();	
		}
		Loader.cleanUp();
		VoxelGeomArray.cleanUp();
		
		DisplayManager.closeDisplay();
		System.exit(-1);
	*/
		
	}
	
	@SuppressWarnings("unused")
	private static void updateLights(ArrayList<Light> lights, long t){
		int i = 0;
		for(Light l : lights){
			
			Vector3f p = l.getPosition();
			double f = i + t / 1000.0;
			p.z = (float)(Math.cos(f) * 50.0);
			p.x = (float)(Math.sin(f) * 50.0);
			i++;
		}
	}
	
	@SuppressWarnings("unused")
	private float randFloat(double min, double max){
		return (float)(min + Math.random() * (max - min));
	}
}
