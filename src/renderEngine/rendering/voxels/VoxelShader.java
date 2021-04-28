package renderEngine.rendering.voxels;


import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;

import renderEngine.rendering.ShaderProgram;
import voxelEngine.VoxelVao;

public class VoxelShader extends ShaderProgram {

	private final static String PATH = "src/renderEngine/rendering/voxels/";
	private final static String VERTEX_FILE = "VoxelVertex.glsl";
	private final static String FRAGMENT_FILE = "VoxelFragment.glsl";
	
	private int l_transformationMatrix;
	private int l_viewMatrix;
	private int l_projectionMatrix;
	private int l_time;
	
	private int[] l_albedoMaps;
	private int[] l_normalMaps;
	private int[] l_uvShifts;
	
	private int[] l_reflectivities;
	private int[] l_shineDampers;
	private int[] l_fresnels;

	public VoxelShader() {
		super(PATH + VERTEX_FILE, PATH + FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		l_transformationMatrix = super.getUniformLocation("transformationMatrix");
		l_viewMatrix = super.getUniformLocation("viewMatrix");
		l_projectionMatrix = super.getUniformLocation("projectionMatrix");
		l_time = super.getUniformLocation("time");
		
		l_albedoMaps = new int[VoxelVao.NUM_TEXTURES];
		l_normalMaps = new int[VoxelVao.NUM_TEXTURES];
		l_uvShifts = new int[VoxelVao.NUM_TEXTURES];
		l_reflectivities = new int[VoxelVao.NUM_TEXTURES];
		l_shineDampers = new int[VoxelVao.NUM_TEXTURES];
		l_fresnels = new int[VoxelVao.NUM_TEXTURES];
		for(int i = 0; i < VoxelVao.NUM_TEXTURES; i++){
			l_albedoMaps[i] = super.getUniformLocation("albedoMaps[" + i + "]");
			l_normalMaps[i] = super.getUniformLocation("normalMaps[" + i + "]");
			l_uvShifts[i] = super.getUniformLocation("uvShifts[" + i + "]");
			
			l_reflectivities[i] = super.getUniformLocation("reflectivities[" + i + "]");
			l_shineDampers[i] = super.getUniformLocation("shineDampers[" + i + "]");
			l_fresnels[i] = super.getUniformLocation("fresnels[" + i + "]");
		}
		

	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
		super.bindAttribute(4, "ambientOcclusion");
		super.bindAttribute(5, "displacement");
		
		super.bindFragOutput(0, "out_Diffuse");
		super.bindFragOutput(1, "out_Normal");
		super.bindFragOutput(2, "out_Position");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(l_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(l_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Matrix4f viewMatrix){
		super.loadMatrix(l_viewMatrix, viewMatrix);
	}	
	
	protected void prepare() {
		super.loadFloat(l_time, (float) (System.nanoTime()/1e6 % 1e5));
		
		for(int i = 0; i < VoxelVao.NUM_TEXTURES; i++){
			super.loadInt(l_albedoMaps[i], i);
			super.loadInt(l_normalMaps[i], i+VoxelVao.NUM_TEXTURES);
			
			try{
				super.loadVector(l_uvShifts[i], VoxelVao.uvShifts[i]);
			}catch(NullPointerException e){}
			
			try{
				super.loadFloat(l_reflectivities[i], VoxelVao.shineInfos[i].x);
				super.loadFloat(l_shineDampers[i], VoxelVao.shineInfos[i].y);
				super.loadFloat(l_fresnels[i], VoxelVao.shineInfos[i].z);
			}catch(NullPointerException e){}
		}
	}
}
