#version 400 core
#extension GL_NV_shadow_samplers_cube : enable

in vec3 direction;

layout (location = 0) out vec4 out_Albedo;
layout (location = 1) out vec4 out_Normal;
layout (location = 2) out vec3 out_Position;
layout (location = 3) out vec3 out_Shine;

uniform samplerCube skybox;

uniform vec3 sunDirection;
uniform vec3 sunColor;

uniform vec3 fogColor = vec3(0.8);

uniform vec2 fogBorders = vec2(0.5, 0.7);

vec3 createBloomColor(vec3 col){
	vec3 result = vec3(0);
	result.r = clamp(col.r-1, 0, 1);
	result.g = clamp(col.g-1, 0, 1);
	result.b = clamp(col.b-1, 0, 1);
	return result;
}

vec3 sampleSun(vec3 dir){
	float sunDot = dot(normalize(sunDirection), dir);
	sunDot = max(sunDot, 0);
	return pow(sunDot, 500) * sunColor;
}

void main(void){
	
	vec3 n_direction = normalize(direction);
	out_Albedo = textureCube(skybox, direction);
	out_Albedo.a = 0;
	
	vec3 sun = sampleSun(n_direction);
	out_Albedo.rgb += sun;
	
	float fogFactor = 0;
	
	float height = n_direction.y / 2 + 0.5;

	fogFactor = min(1 - (height - fogBorders.x) / (fogBorders.y - fogBorders.x), 1);
	

	out_Albedo.rgb = fogColor * fogFactor + out_Albedo.rgb * (1-fogFactor);
	out_Albedo.a = length(sun);
	
	out_Position = out_Shine = vec3(0);
	out_Normal = vec4(0);
}