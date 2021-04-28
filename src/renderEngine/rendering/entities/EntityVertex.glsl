
#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in vec3 tangent;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPosition;

out vec2 uvCoords;
out vec3 vs_surfaceNormal;
out vec3 ts_toCameraVector;
out vec3 vs_position;
out mat3 toTangentSpace;
out mat3 fromTangentSpace;
out vec2 scr_position;
out mat4 fromViewSpace;
out vec3 ws_toCamera;

void main(void){

	vec4 ws_position = transformationMatrix * vec4(position,1.0);
	vec4 vs_position4 = viewMatrix * ws_position;
	vec4 ss_position = projectionMatrix * vs_position4;
	
	fromViewSpace = inverse(viewMatrix);
	ws_toCamera = cameraPosition - ws_position.xyz;

	gl_Position = ss_position;
	
	uvCoords = textureCoords;
	vec3 ws_surfaceNormal = (transformationMatrix * vec4(normal, 0)).xyz;
	vs_surfaceNormal = (viewMatrix * vec4(ws_surfaceNormal, 0)).xyz;
	
	vec3 n_ws_normal = normalize(ws_surfaceNormal);
	vec3 n_tangent = normalize(tangent);
	vec3 n_bitangent = cross(n_ws_normal, n_tangent);
	
	toTangentSpace = mat3(
		n_tangent.x, n_bitangent.x, n_ws_normal.x,
		n_tangent.y, n_bitangent.y, n_ws_normal.y,
		n_tangent.z, n_bitangent.z, n_ws_normal.z
	);

	fromTangentSpace = inverse(toTangentSpace);
	
	
	//ts_toCameraVector = toTangentSpace * (inverse(viewMatrix) * (inverse(projectionMatrix) * vec4(0,0,-1,1) - ws_position)).xyz;
	ts_toCameraVector = toTangentSpace * (cameraPosition - ws_position.xyz);
	
	vs_position = vs_position4.xyz;
	scr_position = ss_position.xy / ss_position.w + 1;
}
