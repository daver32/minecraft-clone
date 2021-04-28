#version 400 core

in vec3 position;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec3 direction;

void main(void){
	mat4 rotMatrix = viewMatrix;
	rotMatrix[3] = vec4(0,0,0,1);

	vec4 vs_position = rotMatrix * vec4(position, 1);
	vec4 ss_position = projectionMatrix * vs_position;
	direction = position;

	gl_Position = ss_position;
}
