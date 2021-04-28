package renderEngine.entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.tools.Maths;

public class Sun {
	public Vector3f direction, color;
	public float strength;
	
	public static int NUM_CASCADES = 4;
	private static double CASCADE_FRACT = 1.0 / NUM_CASCADES;
	
	private static float SHADOWMAP_OFFSET = 1000;
	
	private static float shadowDistance = 256;
	
	private static Vector4f[] nearPoints = {
		new Vector4f(-1, -1, -1, 1),
		new Vector4f( 1, -1, -1, 1),
		new Vector4f(-1,  1, -1, 1),
		new Vector4f( 1,  1, -1, 1),
	};
	
	private static Vector4f[] farPoints = {
		new Vector4f(-1, -1, 1, 1),
		new Vector4f( 1, -1, 1, 1),
		new Vector4f(-1,  1, 1, 1),
		new Vector4f( 1,  1, 1, 1),
	};
	
	
	public Sun(Vector3f direction, Vector3f color) {
		super();
		this.direction = direction;
		this.color = color;
	}
	
	public Sun(Vector3f direction, Vector3f color, float strength) {
		super();
		this.direction = new Vector3f(direction);
		this.direction.normalise();
		this.color = color;
		this.strength = strength;
	}
	
	public Vector3f getRenderColor(){
		return new Vector3f(color.x * strength, color.y * strength, color.z * strength);
	}
	
	public Matrix4f[] getSunPVMatrices(Matrix4f pm, Matrix4f vm, Camera cam, float nPlane, float fPlane){

		float frustumSize = fPlane - nPlane;
		float ratio = shadowDistance / frustumSize;
		
		Vector4f[] nPointsWS = new Vector4f[4];
		Vector4f[] fPointsWS = new Vector4f[4];
		
		Matrix4f ipvm = Matrix4f.invert(Matrix4f.mul(pm, vm, null), null);
		
		if(ipvm == null){
			throw new NullPointerException();
		}
		
		for(int i = 0; i < 4; i++){
			
			Vector4f near = Matrix4f.transform(ipvm, nearPoints[i], null);
			Vector4f far = Matrix4f.transform(ipvm, farPoints[i], null);
			
			pespDiv(near);
			pespDiv(far);
			
			far = lerpVec(near, far, ratio);

			nPointsWS[i] = near; fPointsWS[i] = far;
		}
		

		
		Vector4f[] c_nPointsWS = new Vector4f[4];
		Vector4f[] c_fPointsWS = new Vector4f[4];
		
		Matrix4f[] res = new Matrix4f[NUM_CASCADES];
		for(int c = 0; c < NUM_CASCADES; c++){
			for(int i = 0; i < 4; i++){
				
				c_nPointsWS[i] = lerpVec(nPointsWS[i], fPointsWS[i], Math.pow(c * CASCADE_FRACT, 2.5));
				c_fPointsWS[i] = lerpVec(nPointsWS[i], fPointsWS[i], Math.pow((c+1) * CASCADE_FRACT, 2.5));
			}
			
			Vector3f cpoint = calcCenterPoint(c_nPointsWS, c_fPointsWS);
			Matrix4f sunvm = calcLightViewMatrix(cpoint);
			res[c] = calcCascadePVMatrix(c_nPointsWS, c_fPointsWS, sunvm);
		}
		
		return res;
	}
	
	private Vector3f calcCenterPoint(Vector4f[] nearPoints, Vector4f[] farPoints){
		Vector3f total = new Vector3f();
		for(int i = 0; i < 4; i++){
			total.x += nearPoints[i].x;
			total.y += nearPoints[i].y;
			total.z += nearPoints[i].z;
			total.x += farPoints[i].x;
			total.y += farPoints[i].y;
			total.z += farPoints[i].z;
		}
		
		total.x /= 8; total.y /= 8; total.z /= 8;
		return total;
	}

	private Matrix4f calcCascadePVMatrix(Vector4f[] nearPoints, Vector4f[] farPoints, Matrix4f sunvm){
		Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		Vector3f max = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
		
		for(int i = 0; i < 4; i++){
			for(Vector4f[] points : new Vector4f[][]{nearPoints, farPoints}){
				points[i].w = 1;
				Vector4f p = Matrix4f.transform(sunvm, points[i], null);
				if(p.x < min.x){
					min.x = p.x;
				}
				if(p.x > max.x){
					max.x = p.x;
				}
				
				if(p.y < min.y){
					min.y = p.y;
				}
				if(p.y > max.y){
					max.y = p.y;
				}
				
				if(p.z < min.z){
					min.z = p.z;
				}
				if(p.z > max.z){
					max.z = p.z;
				}
			}
		}
		
		float w = (max.x - min.x);
		float h = (max.y - min.y);
		float d = (max.z - min.z);
		
		Matrix4f pm = Maths.createOrthoProjectionMatrix(w, h, d + SHADOWMAP_OFFSET / 2);

		return Matrix4f.mul(pm, sunvm, null);
	}
	
	private static Vector4f lerpVec(Vector4f v1, Vector4f v2, double val){
		Vector4f res = new Vector4f();
		res.x = (float) (v1.x * (1-val) + v2.x * val);
		res.y = (float) (v1.y * (1-val) + v2.y * val);
		res.z = (float) (v1.z * (1-val) + v2.z * val);
		res.w = (float) (v1.w * (1-val) + v2.w * val);
		return res;
	}
	
	private Matrix4f calcLightViewMatrix(Vector3f center){

		
		Vector3f idirection = new Vector3f(-direction.x, -direction.y, -direction.z);
		idirection.normalise();
		
		Vector3f transl = new Vector3f(-center.x, -center.y, -center.z);
		transl.x += idirection.x * (SHADOWMAP_OFFSET - 32)/2;
		transl.y += idirection.y * (SHADOWMAP_OFFSET - 32)/2;
		transl.z += idirection.z * (SHADOWMAP_OFFSET - 32)/2;
		
		//transl.x += idirection.x * 100;transl.y += idirection.y * 100;transl.z += idirection.z * 100;
		
		Matrix4f lightViewMatrix = new Matrix4f();
		lightViewMatrix.setIdentity();
		double pitch = Math.acos(new Vector2f(idirection.x, idirection.z).length());
        Matrix4f.rotate((float) pitch, new Vector3f(1, 0, 0), lightViewMatrix, lightViewMatrix);
        double yaw = (Math.atan(idirection.x / idirection.z));
        yaw = idirection.z > 0 ? yaw + Math.PI : yaw;
        Matrix4f.rotate((float) -yaw, new Vector3f(0, 1, 0), lightViewMatrix,
                lightViewMatrix);
        
        Matrix4f.translate(transl, lightViewMatrix, lightViewMatrix);
        
        return lightViewMatrix;
	}
	
	
	private void pespDiv(Vector4f v){
		v.x /= v.w; v.y /= v.w; v.z /= v.w;
	}
}
