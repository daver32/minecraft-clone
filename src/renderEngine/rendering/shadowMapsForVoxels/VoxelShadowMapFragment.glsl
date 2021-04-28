#version 430

in vec2 textureCoords;
uniform sampler2D diffuseMap;


uniform sampler2D[15] albedoMaps;

in vertexData {
	vec3 texCoords;
}pass2;

out vec4 out_Color;

void main(void){
	if(texture(albedoMaps[int(pass2.texCoords.z)], pass2.texCoords.xy).a < 0.5){
		discard;
	}

	out_Color = vec4(1);
}