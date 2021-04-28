#version 430

in vec3 position;

out vec2 textureCoords;
out vec2 screenPosXY;

void main(void){
	gl_Position = vec4(position.xy, 1, 1);
	screenPosXY = position.xy;
	textureCoords = vec2(position.xy+1)/2;
}
