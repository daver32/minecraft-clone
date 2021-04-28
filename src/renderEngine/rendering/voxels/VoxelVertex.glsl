#version 400 core

in vec3 position;
in vec3 normal;
in vec3 tangent;
in vec3 textureCoords;
in float ambientOcclusion;
in int displacement;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPosition;
uniform vec2[15] uvShifts;

uniform float time;

out vec3 vs_surfaceNormal;
out vec3 texCoords;
out vec4 vs_position;
out float occlusion;

out mat3 fromTangentSpace;

flat out int texID;

const float DISPLACEMENT_SPEED = 0.001;
const float DISPLACEMENT_FREQ = 10;
const float DISPLACEMENT_STRENGTH = 0.1;

uniform sampler2D[15] albedoMaps;
uniform sampler2D[15] normalMaps;

void main(void){
	vec4 ws_position = transformationMatrix * vec4(position,1.0);

	if(displacement != 0){
		vec3 displacement = vec3(0);
		displacement.x = sin(position.z * DISPLACEMENT_FREQ + time * DISPLACEMENT_SPEED);
		displacement.y = cos(position.x * DISPLACEMENT_FREQ + time * DISPLACEMENT_SPEED);
		displacement.z = sin(position.y * DISPLACEMENT_FREQ + time * DISPLACEMENT_SPEED);
		ws_position.xyz += displacement * DISPLACEMENT_STRENGTH;
	}

	
	vs_position = viewMatrix * ws_position;
	vec4 ss_position = projectionMatrix * vs_position;

	gl_Position = ss_position;

	vec4 ws_surfaceNormal = transformationMatrix * vec4(normal, 0);
	vs_surfaceNormal = (viewMatrix * ws_surfaceNormal).xyz;

	texCoords = textureCoords;
	texID = int(textureCoords.z);
	texCoords.xy += uvShifts[texID] * time / 1000.0;

	vec3 n_ws_normal = normalize(ws_surfaceNormal.xyz);
	vec3 n_tangent = normalize(tangent);
	vec3 n_bitangent = cross(n_ws_normal, n_tangent);
	
	mat3 toTangentSpace = mat3(
		n_tangent.x, n_bitangent.x, n_ws_normal.x,
		n_tangent.y, n_bitangent.y, n_ws_normal.y,
		n_tangent.z, n_bitangent.z, n_ws_normal.z
	);
	fromTangentSpace = inverse(toTangentSpace);
	occlusion = ambientOcclusion;
	

}
