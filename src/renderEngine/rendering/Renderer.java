package renderEngine.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import renderEngine.models.RawModel;
import renderEngine.tools.Loader;

public abstract class Renderer {
	public abstract void cleanUP();
	private static RawModel quad = null;
	private static RawModel cube = null;
	
	public enum Blend{
		ALPHA,
		ADDITIVE,
		MULTIPLY,
		NONE
	}
	
	protected void setBlending(Blend mode){
		if(mode == Blend.ALPHA){
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}else if(mode == Blend.ADDITIVE){
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
		}else if(mode == Blend.MULTIPLY){
			GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
		}else if(mode == Blend.NONE){
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
	
	protected void clear(double r, double g, double b, double a){
		GL11.glClearColor((float)r, (float)g, (float)b, (float)a);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}
	
	protected void disableArrays(int n){
		for(int i = 0; i < n; i++){
			GL20.glDisableVertexAttribArray(i);
		}
	}
	
	protected void enableArrays(int n){
		for(int i = 0; i < n; i++){
			GL20.glEnableVertexAttribArray(i);
		}
	}
	
	protected static RawModel getQuad(){
		if(quad == null){
			quad = Loader.genQuad();
		}
		return quad;
	}
	
	protected static RawModel getCube(){
		if(cube == null){
			cube = Loader.genCube();
		}
		return cube;
	}
	
	protected void unbindTextures(int n){
		for(int i = 0; i < n; i++){
			GL13.glActiveTexture(GL13.GL_TEXTURE0+n);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}
	}
}
