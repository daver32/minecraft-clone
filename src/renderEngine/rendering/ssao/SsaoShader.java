package renderEngine.rendering.ssao;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.rendering.ShaderProgram;

public class SsaoShader extends ShaderProgram {

	private final static String VERTEX_FILE = "src/renderEngine/rendering/ssao/SsaoVertex.glsl";
	private final static String FRAGMENT_FILE = "src/renderEngine/rendering/ssao/SsaoFragment.glsl";
	
	private static final int MAX_KERNEL_SIZE = 100;
	
	private int[] l_kernel;
	private int l_kernelSize;
	
	private int l_depthTexture;
	private int l_normalTexture;
	private int l_positionTexture;
	
	private int l_viewMatrix;
	private int l_projectionMatrix;
	
	public SsaoShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		l_kernelSize = super.getUniformLocation("kernelSize");
		l_kernel = new int[MAX_KERNEL_SIZE];
		for(int i = 0; i < MAX_KERNEL_SIZE; i++){
			l_kernel[i] = super.getUniformLocation("sampleKernel[" + i + "]");
		}
		
		l_depthTexture = super.getUniformLocation("depthTexture"); 
		l_normalTexture = super.getUniformLocation("normalTexture"); 
		l_positionTexture = super.getUniformLocation("positionTexture"); 
		
		l_viewMatrix = super.getUniformLocation("viewMatrix");
		l_projectionMatrix = super.getUniformLocation("projectionMatrix");
	}
	
	public void loadKernel(Vector3f[] sampleKernel){
		int size = 0;
		for(int i = 0; i < sampleKernel.length; i++){
			if(i == MAX_KERNEL_SIZE){
				break;
			}
			super.loadVector(l_kernel[i], sampleKernel[i]);
			size++;
		}
		super.loadInt(l_kernelSize, size);
	}
	
	public void loadViewMatrix(Matrix4f viewMatrix){
		super.loadMatrix(l_viewMatrix, viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(l_projectionMatrix, matrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void prepare() {
		super.loadInt(l_depthTexture, 0);
		super.loadInt(l_normalTexture, 1);
		super.loadInt(l_positionTexture, 2);
	}
}
