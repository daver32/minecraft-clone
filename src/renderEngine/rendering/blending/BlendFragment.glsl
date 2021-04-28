#version 400 core

in vec2 textureCoords;

uniform sampler2D diffuseMap;

uniform float multiplier;

layout (location = 0) out vec4 out_Color;

void main(void){
	out_Color = texture(diffuseMap, textureCoords) * multiplier;
}