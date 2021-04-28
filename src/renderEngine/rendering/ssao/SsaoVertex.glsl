#version 400 core

in vec3 position;
out vec2 textureCoords;

void main(void){
	gl_Position = vec4(position.xy, 1, 1);
	textureCoords = vec2(position.xy+1)/2;
}
