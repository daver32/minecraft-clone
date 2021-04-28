#version 400 core

in vec3 position;
in vec2 coords;

uniform mat4 mMatrix;

out vec2 textureCoords;

out vertexData{
	vec2 textureCoords;
	vec3 ws_position;
}pass1;

void main(void){
	pass1.ws_position = (mMatrix * vec4(position, 1.0)).xyz;
	pass1.textureCoords = coords;
}
