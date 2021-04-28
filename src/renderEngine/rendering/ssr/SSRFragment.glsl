#version 400 core
#extension GL_NV_shadow_samplers_cube : enable

in vec2 textureCoords;

uniform sampler2D diffuseTexture;
uniform sampler2D depthTexture;

uniform samplerCube skybox;
uniform samplerCube envMap;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

layout (location = 1) out vec3 out_Color;

const bool reflectEnvMap = false;

uniform float nearPlane, farPlane;


float genCheckerTexture(void){
	vec4 c = texture(diffuseTexture, textureCoords);

	bool b1, b2;

	b1 = sin(textureCoords.x*100) > 0;
	b2 = sin(textureCoords.y*100) > 0;

	float f = 0;
	if(b1 != b2){f = 1;}
	return f;
}


void main(void){

	vec4 sampledDiffuse = texture(diffuseTexture, textureCoords);
	vec3 color = sampledDiffuse.rgb;
	//if(sampledDiffuse.a > 0){
	//	color = color * (1-sampledDiffuse.a) + vec3(genCheckerTexture()) * sampledDiffuse.a;
	//}

	out_Color.rgb = color;
}