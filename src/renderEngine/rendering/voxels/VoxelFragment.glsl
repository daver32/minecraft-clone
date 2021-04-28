#version 400 core

in vec3 vs_surfaceNormal;
in vec3 texCoords;
in vec4 vs_position;
in float occlusion;

in mat3 fromTangentSpace;

flat in int texID;

uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

layout (location = 0) out vec4 out_Albedo;
layout (location = 1) out vec4 out_Normal;
layout (location = 2) out vec3 out_Position;
layout (location = 3) out vec3 out_Shine;

uniform sampler2D[15] albedoMaps;
uniform sampler2D[15] normalMaps;

uniform float[15] reflectivities;
uniform float[15] shineDampers;
uniform float[15] fresnels;


const bool useNormalMaps = true;

void main(void){
	vec4 sampledAlbedo = texture(albedoMaps[texID], texCoords.xy);

	if(sampledAlbedo.a < 0.1){
		discard;
	}
	
	out_Albedo.a = 1;
	
	vec3 n_vs_surfaceNormal = normalize(vs_surfaceNormal);
	
	if(useNormalMaps){
		vec3 sampledNormal = texture(normalMaps[texID], texCoords.xy).rgb;
		if(sampledNormal != vec3(0)){
			vec3 ts_normal = sampledNormal.xyz;
			ts_normal.xy = ts_normal.xy * 2 - 1;
			vec3 os_normal = fromTangentSpace * ts_normal;
			vec3 vs_normal = (viewMatrix * transformationMatrix * vec4(os_normal, 0)).rgb;
			out_Normal.xyz = vs_normal;
		}else{
			out_Normal.xyz = vs_surfaceNormal;
		}
	}else{
		out_Normal.xyz = vs_surfaceNormal;
	}
	
	out_Albedo.rgb = sampledAlbedo.rgb * occlusion;
	

	out_Position = vs_position.xyz;
	out_Shine = vec3(reflectivities[texID], shineDampers[texID], fresnels[texID]);
}