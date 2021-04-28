package renderEngine.rendering;


import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;

import renderEngine.entities.Camera;
import renderEngine.framebuffers.FrameBuffers;
import renderEngine.framebuffers.GBuffer;
import renderEngine.framebuffers.PPBuffer;
import renderEngine.framebuffers.ShadowMapFBO;
import renderEngine.framebuffers.TempBuffer;
import renderEngine.rendering.blending.BlendRenderer;
import renderEngine.rendering.colorCorrection.CCRenderer;
import renderEngine.rendering.deferred.DeferredRenderer;
import renderEngine.rendering.entities.EntityRenderer;
import renderEngine.rendering.envMap.EnvMapRenderer;
import renderEngine.rendering.gaussianBlur.Blur;
import renderEngine.rendering.gaussianBlur.BlurRenderer;
import renderEngine.rendering.radialBlur.RadialBlurRenderer;
import renderEngine.rendering.settings.TextureSettings;
import renderEngine.rendering.shadowMaps.SunShadowMapRenderer;
import renderEngine.rendering.simpleParticles.SimpleParticleRenderer;
import renderEngine.rendering.sky.SkyRenderer;
import renderEngine.rendering.ssao.SsaoRenderer;
import renderEngine.rendering.ssr.SSRRenderer;
import renderEngine.rendering.voxels.VoxelRenderer;
import renderEngine.textures.Cubemap;
import renderEngine.tools.Maths;

public class MasterRenderer extends Renderer{
	
	private EntityRenderer entityRenderer;
	private VoxelRenderer voxelRenderer;
	private DeferredRenderer deferredRenderer;
	private BlendRenderer blendRenderer;
	private BlurRenderer blurRenderer;
	private SsaoRenderer ssaoRenderer;
	private EnvMapRenderer envMapRenderer;
	private SSRRenderer ssrRenderer;
	private SkyRenderer skyRenderer;
	private SunShadowMapRenderer ssmRenderer;
	private SimpleParticleRenderer spRenderer;
	private RadialBlurRenderer rbRenderer;
	private CCRenderer ccRenderer;
	
	private float nearPlane = 0.01f, farPlane = 700, fov = 100;
	
	private Matrix4f projectionMatrix;
	
	private GBuffer gbuffer;
	private PPBuffer ppbuffer;
	@SuppressWarnings("unused")
	private TempBuffer tempBuffer, bloomBuffer, ssaoBuffer1, ssaoBuffer2;
	private ShadowMapFBO shadowMap;
	
	private Cubemap envMap;
	
	private final int WIDTH, HEIGHT;
	
	private TextureSettings texSettings = new TextureSettings(true, false);
	
	public MasterRenderer(){
		WIDTH = Display.getWidth();
		HEIGHT = Display.getHeight();
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.setIdentity();
		updateProjectionMatrix();
		
		entityRenderer = new EntityRenderer(projectionMatrix);
		voxelRenderer = new VoxelRenderer(projectionMatrix);
		deferredRenderer = new DeferredRenderer(projectionMatrix);
		blendRenderer = new BlendRenderer();
		blurRenderer = new BlurRenderer();
		ssaoRenderer = new SsaoRenderer();
		envMapRenderer = new EnvMapRenderer();
		ssrRenderer = new SSRRenderer(projectionMatrix);
		skyRenderer = new SkyRenderer(projectionMatrix);
		ssmRenderer = new SunShadowMapRenderer(projectionMatrix);
		spRenderer = new SimpleParticleRenderer(projectionMatrix);
		rbRenderer = new RadialBlurRenderer();
		ccRenderer = new CCRenderer();
		
		gbuffer = new GBuffer(WIDTH, HEIGHT);
		ppbuffer = new PPBuffer(WIDTH, HEIGHT); 
		
		tempBuffer = new TempBuffer(WIDTH/2, HEIGHT/2); 
		bloomBuffer = new TempBuffer(WIDTH/2, HEIGHT/2); 
		
		ssaoBuffer1 = new TempBuffer(WIDTH/2, HEIGHT/2); 
		ssaoBuffer2 = new TempBuffer(WIDTH/2, HEIGHT/2); 
		
		envMap = new Cubemap(256);
		
		shadowMap = new ShadowMapFBO(4096);
	}

	
	public void render(Camera c, int targetFboID, Scene s){
		Matrix4f viewMatrix = Maths.createViewMatrix(c);
		
		//envMapRenderer.render(envMap, c.getPosition(), s.entityLists);
		
		skyRenderer.render(gbuffer, viewMatrix, s.skybox.getID(), s.sun);
		
		entityRenderer.renderAll(gbuffer, s.entityLists, viewMatrix, c, texSettings);
		voxelRenderer.renderAll(gbuffer, s.voxelChunks, viewMatrix);

		spRenderer.render(gbuffer, viewMatrix, s.simpleParticles);

		Matrix4f[] ssmMatrices = null;
		if(!Keyboard.isKeyDown(Keyboard.KEY_M)){
			ssmMatrices = ssmRenderer.renderAll(shadowMap, s.sun, c, viewMatrix, s.entityLists, s.voxelChunks, nearPlane, farPlane);
		}


		deferredRenderer.render(gbuffer, ppbuffer, viewMatrix, s.lightList, s.skybox.getID(), envMap.CUBEMAP_ID, s.sun, c, shadowMap.getTextureID(), ssmMatrices, s.fogBorders);
		
		
		
		
		//ssrRenderer.render(gbuffer, ppbuffer, viewMatrix, skybox.CUBEMAP_ID, envMap.CUBEMAP_ID);
		
		
		//renderTransparentObjects(viewMatrix, c);
		//envMapRenderer.renderToEnvMap(envMap, c, entityLists);
		

		
		tempBuffer.bind();
		blurRenderer.blur(ppbuffer.getBloomMap(), Blur.VERTICAL, 100);
		bloomBuffer.bind();
		blurRenderer.blur(tempBuffer.getTexture1(), Blur.HORIZONTAL, 50);
		/*
		ssaoBuffer1.bind();
		clear(0,0,0,0);
		ssaoRenderer.render(gbuffer.getDepthTextureID(), gbuffer.getNormalID(), gbuffer.getPositionTextureID(), viewMatrix, projectionMatrix);
		ssaoBuffer2.bind();
		clear(0,0,0,0);
		blurRenderer.blur(ssaoBuffer1.getTexture1(), Blur.HORIZONTAL, 25);
		ssaoBuffer1.bind();
		clear(0,0,0,0);
		blurRenderer.blur(ssaoBuffer2.getTexture1(), Blur.VERTICAL, 25);
		*/
		
		FrameBuffers.bindFrameBuffer(targetFboID, WIDTH, HEIGHT);
	
		
		//blendRenderer.blend(ppbuffer.getRawColorMap(), Blend.NONE);
		//blendRenderer.blend(ppbuffer.getRawColorMap(), Blend.NONE);
		ccRenderer.render(ppbuffer.getRawColorMap());
		
		if(!Keyboard.isKeyDown(Keyboard.KEY_O)){
			//blendRenderer.blend(ssaoBuffer1.getTexture1(), 1, Blend.MULTIPLY);
		}
		
		blendRenderer.blend(bloomBuffer.getTexture1(), 2, Blend.ADDITIVE);

		//rbRenderer.blur(ppbuffer.getRawColorMap(), new Vector2f(0, 0), RadialBlur.OUTWARDS, 0.5f);
		/*
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, targetFboID);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, shadowMap.getID());
		GL11.glDrawBuffer(GL11.GL_BACK);    
		GL30.glBlitFramebuffer(0, 0, shadowMap.getWidth(), shadowMap.getHeight(), 0, 0, 300, 300, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		 */

	}
	
	private void updateProjectionMatrix(){
		Matrix4f newMatrix = Maths.createProjectionMatrix(fov, farPlane, nearPlane);
		Matrix4f.load(newMatrix, projectionMatrix);
	}
	
	public float getNearPlane() {
		return nearPlane;
	}

	public void setNearPlane(float nearPlane) {
		this.nearPlane = nearPlane;
	}

	public float getFarPlane() {
		return farPlane;
	}


	public void setFarPlane(float farPlane) {
		this.farPlane = farPlane;
	}


	public TextureSettings getTexSettings() {
		return texSettings;
	}

	public void setTexSettings(TextureSettings texSettings) {
		this.texSettings = texSettings;
	}

	@Override
	public void cleanUP() {
		entityRenderer.cleanUP();
		deferredRenderer.cleanUP();
		blendRenderer.cleanUP();
		blurRenderer.cleanUP();
		ssaoRenderer.cleanUP();
		envMapRenderer.cleanUP();
		ssrRenderer.cleanUP();
		skyRenderer.cleanUP();
		spRenderer.cleanUP();
		ssmRenderer.cleanUP();
		rbRenderer.cleanUP();
		ccRenderer.cleanUP();
	}



	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setFov(float fov){
		this.fov = fov;
		updateProjectionMatrix();
	}
	
	
}
