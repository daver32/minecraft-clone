#version 400 core

in vec2 position;
out vec2 textureCoords;
out vec2 ss_position;

void main(void){
	vec4 worldPosition = vec4(position.x,position.y, 1, 1);
	gl_Position = worldPosition;
	textureCoords = vec2((position.x+1.0)/2.0, (position.y+1.0)/2.0);
	ss_position = gl_Position.xy;
}

