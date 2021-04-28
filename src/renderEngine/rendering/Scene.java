package renderEngine.rendering;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import renderEngine.entities.Light;
import renderEngine.entities.RenderEntity;
import renderEngine.entities.RenderObject;
import renderEngine.entities.Sun;
import renderEngine.rendering.simpleParticles.SimpleParticle;
import renderEngine.textures.Cubemap;
import voxelEngine.VoxelVao;

public class Scene {

	public ArrayList<ArrayList<RenderEntity>> entityLists = new ArrayList<ArrayList<RenderEntity>>();
	public ArrayList<RenderObject> transparentObjects = new ArrayList<RenderObject>();
	public ArrayList<Light> lightList = new ArrayList<Light>();
	public ArrayList<VoxelVao> voxelChunks = new ArrayList<VoxelVao>();
	public ArrayList<SimpleParticle> simpleParticles = new ArrayList<SimpleParticle>();
	
	public Vector2f fogBorders;
	
	public Cubemap skybox = new Cubemap(1);
	public Sun sun;
	
	public void addEntity(RenderEntity e){
		if(e.getTexturedModel().getMaterial().getTransparency() == 2){
			transparentObjects.add(e);
		}else{
			addToEntityList(e, entityLists);
		}
	}
	
	private void addToEntityList(RenderEntity e, ArrayList<ArrayList<RenderEntity>> entityLists){
		for(ArrayList<RenderEntity> entityList : entityLists){
			if(entityList.get(0).getTexturedModel().getModel().getVaoID() == e.getTexturedModel().getModel().getVaoID()){
				entityList.add(e);
				return;
			}
		}
		
		ArrayList<RenderEntity> entityList = new ArrayList<RenderEntity>();
		entityList.add(e);
		entityLists.add(entityList);
	}
}
