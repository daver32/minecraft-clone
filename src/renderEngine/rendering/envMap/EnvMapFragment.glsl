#version 400 core

in vec2 uvCoords;

uniform sampler2D albedoMap;

layout (location = 0) out vec4 out_Albedo;

void main(void){
	out_Albedo = texture(albedoMap, uvCoords);
}