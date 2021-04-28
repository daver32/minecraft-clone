#version 400 core
#extension GL_NV_shadow_samplers_cube : enable

in vec2 uvCoords;
in vec3 vs_surfaceNormal;
in vec3 ts_toCameraVector;
in vec3 vs_position;
in mat3 toTangentSpace;
in mat3 fromTangentSpace;
in mat4 fromViewSpace;
in mat4 fromScreenSpace;
in vec2 scr_position;
in vec3 ws_toCamera;

uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D heightMap;

uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

layout (location = 0) out vec4 out_Albedo;
layout (location = 1) out vec4 out_Normal;
layout (location = 2) out vec3 out_Position;
layout (location = 3) out vec3 out_Shine;

const int POM_NUM_LAYERS = 100;
const float POM_SCALE = 10;

uniform bool useParallaxMaping;
uniform bool useNormalMaps;

uniform float reflectivity;
uniform float shineDamper;
uniform float fresnel;
uniform float refractiveIndex;

uniform bool transparency;
uniform samplerCube envMap;

float getHeight(vec2 coords){
	return (texture(heightMap, coords).r) * POM_SCALE;
}


vec2 getParallaxUV(vec3 direction){

	float layerDepth = POM_SCALE / POM_NUM_LAYERS;
	
	vec3 pointer = vec3(uvCoords, 0);
	vec3 step = direction;
	step.x *= -1;
	//step /= step.z;
	step *= layerDepth;
	
	for(int i = 0; i < POM_NUM_LAYERS; i++){
		float height = getHeight(pointer.xy);


		if(pointer.z >= height){
		
			for(int i2 = 0; i2 < 10; i2++){
				step /= 2;
				height = getHeight(pointer.xy);
				
				if(pointer.z >= height){
					pointer -= step;
				}else{
					pointer += step;
				}
	
			}
			return pointer.xy;
		}
		
		pointer += step;

	} 
	
	return uvCoords;
}

void main(void){
	vec3 n_ts_toCameraVector = normalize(ts_toCameraVector);
	vec3 n_vs_surfaceNormal = normalize(vs_surfaceNormal);
	
	float dotSurface = dot(n_ts_toCameraVector, n_vs_surfaceNormal);
	
	vec3 ts_parallelParallaxDir = toTangentSpace * (fromViewSpace * vec4(0,0,1,0)).xyz;

	//vec2 processedUV = useParallaxMaping ? getParallaxUV(normalize(ts_parallelParallaxDir)) : uvCoords;
	vec2 processedUV = useParallaxMaping ? getParallaxUV(n_ts_toCameraVector) : uvCoords;
	
	//if(processedUV.x > 1 || processedUV.x < 0 || processedUV.y > 1 || processedUV.y < 0){
	//	discard;
	//}
	
	vec4 sampledDiffuse = texture(albedoMap, processedUV);
	vec4 sampledNormal = texture(normalMap, processedUV);
	
	if(useNormalMaps){
		vec3 ts_normal = sampledNormal.xyz;
		ts_normal.xy = ts_normal.xy * 2 - 1;
		vec3 os_normal = fromTangentSpace * ts_normal;
		vec3 vs_normal = (viewMatrix * transformationMatrix * vec4(os_normal, 0)).rgb;
		out_Normal.xyz = vs_normal;
	}else{
		out_Normal.xyz = vs_surfaceNormal;
	}

	out_Normal.a = refractiveIndex;


	out_Albedo = sampledDiffuse;

	if(!transparency && out_Albedo.a < 1){
		discard;
		//out_Albedo.a = 1;
	}else if(out_Albedo.a == 0){
		out_Albedo.a = 0.01;
	}

	out_Position = vs_position;
	out_Shine = vec3(reflectivity, shineDamper, fresnel);
}