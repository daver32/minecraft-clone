#version 400 core

in vec2 textureCoords;

uniform sampler2D diffuseMap;

layout (location = 0) out vec3 out_Color;

const float contrast = 1.3;
const float brightness = 0.7;

void main(void){
	vec3 color = texture(diffuseMap, textureCoords).rgb;

	color *= brightness;
	
	color -= 0.5;
	color *= contrast;
	color += 0.5;
	


	out_Color = color;
}