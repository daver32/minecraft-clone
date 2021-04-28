#version 400 core

in vec2 textureCoords;
uniform sampler2D albedoMap;

out vec4 out_Color;

in vertexData {
	vec2 textureCoords;
}pass2;

void main(void){
	if(texture(albedoMap, pass2.textureCoords).a < 0.5){
		discard;
	}

	out_Color = vec4(1);
}