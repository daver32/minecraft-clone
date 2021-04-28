#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in vec3 tangent;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 translation;

out vec2 uvCoords;

void main(void){

	vec4 ws_position = transformationMatrix * vec4(position, 1.0);
	
	ws_position.xyz += translation;
	
	vec4 vs_position = viewMatrix * ws_position;
	vs_position.xy *= -1;
	vec4 ss_position = projectionMatrix * vs_position;

	gl_Position = ss_position;

	uvCoords = textureCoords;
}
