package renderEngine.tools;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.entities.Camera;


public final class Maths {
	public static Matrix4f createTransformationMatrix(Vector3f translation,float rx, float ry, float rz, float scale){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		
		Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
		
		return matrix;
	}
	
	public static Matrix4f createRotationMatrix(float rx, float ry, float rz){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		
		return matrix;
	}	
	
	public static Matrix4f createViewMatrix(Camera camera){
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();

		Matrix4f.rotate((float)Math.toRadians(camera.getPitch()), new Vector3f(1,0,0),viewMatrix,viewMatrix);
		Matrix4f.rotate((float)Math.toRadians(camera.getYaw()), new Vector3f(0,1,0),viewMatrix,viewMatrix);
		Matrix4f.rotate((float)Math.toRadians(camera.getRoll()), new Vector3f(0,0,1),viewMatrix,viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f.translate(negativeCameraPos,viewMatrix,viewMatrix);		
		
		return viewMatrix;
	}
	
	public static float calculateAngle(float p1x,float p1y,float p2x,float p2y){
		float deltaX = Math.abs(p1x - p2x);
		float deltaY = Math.abs(p1y - p2y);
		float degrees = (float)(Math.atan2(deltaY,deltaX) * 180 / Math.PI);
		return degrees;
		
	}
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createProjectionMatrix(float fov, float farPlane, float nearPlane){
		float aspectRatio = (float)Display.getWidth() / (float)Display.getHeight();
		float y_scale  = (float)(1f / Math.tan(Math.toRadians(fov/2f)));
		float x_scale = y_scale/aspectRatio;
		float frustumLength = farPlane - nearPlane;
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((farPlane + nearPlane) / frustumLength);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * farPlane * nearPlane) / frustumLength);
		projectionMatrix.m33 = 0;
		
		return projectionMatrix;
	}
	
    public static Matrix4f createOrthoProjectionMatrix(float width, float height, float length) {
    	Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.setIdentity();
        projectionMatrix.m00 = 2f / width;
        projectionMatrix.m11 = 2f / height;
        projectionMatrix.m22 = -2f / length;
        projectionMatrix.m33 = 1;
        return projectionMatrix;
    }
	
	public static Vector3f rotatePoint(float rotX, float rotY, float rotZ, Vector3f point, Vector3f origin){
		double x2,y2,z2;
		Vector3f result = new Vector3f(point.x, point.y, point.z);
		
		result.x -= origin.x;
		result.y -= origin.y;
		result.z -= origin.z;

		if(rotZ != 0){
			double rads = Math.toRadians(rotZ);
	        x2 = Math.cos(rads)*result.x - Math.sin(rads)*result.y;
	        y2 = Math.sin(rads)*result.x + Math.cos(rads)*result.y;
	        z2 = result.z;
	        result = new Vector3f((float)x2, (float)y2, (float)z2);
		}
		if(rotY != 0){
			double rads = Math.toRadians(rotY);
			x2 = Math.cos(rads)*result.x + Math.sin(rads)*result.z;
	        y2 = result.y;
	        z2 = -Math.sin(rads)*result.x + Math.cos(rads)*result.z;    
	        result = new Vector3f((float)x2, (float)y2, (float)z2);
		}
		if(rotX != 0){
			double rads = Math.toRadians(rotX);
	        x2 = result.x;
	        y2 = Math.cos(rads)*result.y + -Math.sin(rads)*result.z;
	        z2 = Math.sin(rads)*result.y + Math.cos(rads)*result.z;
	        result = new Vector3f((float)x2, (float)y2, (float)z2);
		}
		
		result.x += origin.x;
		result.y += origin.y;
		result.z += origin.z;
		
		return result;
	}
	
	public static double calcDistance(Vector3f p1, Vector3f p2){
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) +  Math.pow(p1.z - p2.z, 2) + Math.pow(p1.y - p2.y, 2));
		
	}
	
	public static double calcDistance(Vector2f p1, Vector2f p2){
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) +  Math.pow(p1.y - p2.y, 2));
		
	}
	
	public static double calcPseudoDistance(Vector2f p1, Vector2f p2){
		return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
		
	}
	
	public static Vector4f matrix4fXVector4f(Vector4f v, Matrix4f m){
		Vector4f result = new Vector4f();
		result.x = m.m00*v.x + m.m01*v.y + m.m02*v.z;
		result.y = m.m10*v.x + m.m11*v.y + m.m12*v.z;
		result.z = m.m20*v.x + m.m21*v.y + m.m22*v.z;
		result.w = m.m30*v.x + m.m31*v.y + m.m32*v.z;
		return result;
		
	}
	
	public static Vector4f Vector4fXmatrix4f(Vector4f v, Matrix4f m){
		Vector4f result = new Vector4f();
		result.x = m.m00*v.x + m.m01*v.y + m.m02*v.z;
		result.y = m.m10*v.x + m.m11*v.y + m.m12*v.z;
		result.z = m.m20*v.x + m.m21*v.y + m.m22*v.z;
		result.w = m.m30*v.x + m.m31*v.y + m.m32*v.z;
		return result;
		
	}
	
	public static Vector4f vec3toVec4(Vector3f v){
		Vector4f result = new Vector4f();
		result.x = v.x;
		result.y = v.y;
		result.z = v.z;
		result.w = 0;
		return result;
	}
	
	
	public static double randomRange(double min, double max){
		return min + (max - min) * Math.random();
	}
	
	public static float randFloat(double min, double max){
		return (float) (min + Math.random() * (max - min));
	}
}
