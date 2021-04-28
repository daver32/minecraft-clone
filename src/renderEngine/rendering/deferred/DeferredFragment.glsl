#version 430
#extension GL_NV_shadow_samplers_cube : enable

in vec2 textureCoords;
in vec2 screenPosXY;

uniform sampler2D albedoTexture;
uniform sampler2D normalTexture;
uniform sampler2D depthTexture;
uniform sampler2D positionTexture;
uniform sampler2D shineTexture;
uniform samplerCube skybox;
uniform samplerCube envMap;
uniform sampler2D shadowmapTexture;

uniform vec3[300] lightPositions;
uniform vec3[300] lightColors;
uniform float[300] lightStrengths;
uniform int lightCount;
uniform vec3 sunDirection;
uniform vec3 sunColor;
uniform vec3 camPosition;

uniform mat4[4] ssmMatrices;
uniform int numCascades;
uniform bool drawSunShadows;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec2 fogBorders = vec2(0, 0);
uniform vec3 fogColor = vec3(0.8);

layout (location = 0) out vec4 out_Diffuse;
layout (location = 2) out vec3 out_Bloom;

const bool reflectEnvMap = false;

const float NEAR_PLANE = 0.001;
const float FAR_PLANE = 500;

const float BLOOM_STRENGTH = 2;

const vec3 ambientLighting = vec3(0.8);
const bool enableSSR = false;

const float shadowmapBias = 0;//0.001;
const float shadowmapBlur = 0.001;

const int shadowSmoothingRange = 2;
const bool useShadowSmoothing = false;

const int[4][2] dirs2d = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
const float smcs = 0.0005;

const bool useSunShafts = true;

vec3 createBloomColor(vec3 col){
	vec3 result = vec3(0);
	result.r = clamp(col.r-1, 0, 1);
	result.g = clamp(col.g-1, 0, 1);
	result.b = clamp(col.b-1, 0, 1);
	return result;
}

float linearizeDepth(float depth){
    return 2.0 * NEAR_PLANE * FAR_PLANE / (FAR_PLANE + NEAR_PLANE - (2.0 * depth - 1.0) * (FAR_PLANE - NEAR_PLANE));
}

bool isBetween(float x, float min, float max){
	return x >= min && x <= max;
}

vec2 calcCoords(vec2 inCoords, int cascade){
	float w = int(pow(numCascades, 0.5));

	int x = cascade % int(w);
	int y = cascade / int(w);
	
	float iw = 1.0 / w;

	vec2 res = inCoords + vec2(x, y);
	res *= iw;
			
	return res;
}

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float sampleDepth(vec3 ws_position, mat4 matrix, int cascade){
	vec4 coords = matrix * vec4(ws_position, 1);
	
	coords.xyz = (coords.xyz+1)/2;
	coords.xy = calcCoords(coords.xy, cascade);
	
	float sampledDepth = texture(shadowmapTexture, coords.xy).r;
	
	float fragDepth = coords.z - shadowmapBias;

	return fragDepth >= sampledDepth ? 1 : 0;
}


float shadowIntensity(vec3 ws_position, mat4 invViewMatrix, vec3 vs_position){
	if(!drawSunShadows){
		return 0;
	}

	int cascade = 0;
	bool invalid = true;
	vec4 coords = vec4(0);
	mat4 ssmMatrix;
	for(int c = 0; c < 4; c++){
		ssmMatrix = ssmMatrices[c];
		coords = ssmMatrix * vec4(ws_position, 1);

		if(isBetween(coords.x, -1, 1) && isBetween(coords.y, -1, 1) && isBetween(coords.z, -1, 1)){
			cascade = c;
			invalid = false;
			break;
		}
	}
	
	if(invalid){
		return 0;
	}
	
	if(useShadowSmoothing){
		float resIntensity = 0;
		for(int x = -shadowSmoothingRange; x < shadowSmoothingRange; x++){
			for(int y = -shadowSmoothingRange; y < shadowSmoothingRange; y++){
				vec2 offset = (vec2(x, y)) / 200 / vs_position.z;
				vec4 ws_position_b = texture(positionTexture, textureCoords + offset);

				ws_position_b.w = 1;
				ws_position_b = invViewMatrix * ws_position_b;
			
				resIntensity += sampleDepth(ws_position_b.xyz, ssmMatrix, cascade);
			}
		}
		resIntensity /= pow(shadowSmoothingRange + 1, 2);
		return resIntensity;
	}else{
	
		coords.xyz = (coords.xyz+1)/2;
		coords.xy = calcCoords(coords.xy, cascade);
		
		float sampledDepth = texture(shadowmapTexture, coords.xy).r;
		
		float fragDepth = coords.z - shadowmapBias;
		bool result = fragDepth >= sampledDepth;

		if(result){
			return 1;
		}else{
		
			if(vs_position.z < 20){
				for(int i = 0; i < 4; i++){
					vec2 offset = vec2(dirs2d[i][0], dirs2d[i][1]) * smcs / vs_position.z;
					sampledDepth = texture(shadowmapTexture, coords.xy + offset).r;
					
					if(fragDepth >= sampledDepth){
						return 1;
					}
				}
			}

			
			return 0;
		}
	}

	

}



vec3 sampleSun(vec3 dir){
	float sunDot = dot(normalize(sunDirection), dir);
	sunDot = max(sunDot, 0);
	return pow(sunDot, 500) * sunColor;
}

bool isValidCoords(vec2 coords){
	if(coords.x > 0 && coords.y > 0 && coords.x < 1 && coords.y < 1){
		return true;
	}
	return false;
}

vec3 sampleSunShafts(){
	int numSteps = 100;
	vec3 sunShaftsColor = vec3(0);
	float mul = 1;
	if(useSunShafts){
		mat4 viewRotationMatrix = viewMatrix;

		vec4 ss_sunPosition = projectionMatrix * viewRotationMatrix * vec4(sunDirection, 1);
		if(ss_sunPosition.w <= 0.5){
			return vec3(0);
		}else if(ss_sunPosition.w <= 0.8){
			mul = (ss_sunPosition.w - 0.5) / 0.3;
		}
		
		ss_sunPosition.xy /= ss_sunPosition.w;
		
		vec2 rBlurDir = -(screenPosXY - ss_sunPosition.xy);
		rBlurDir *= 1;
		rBlurDir /= numSteps;
		
		vec2 pointer = screenPosXY;
		int divider = numSteps;
		for(int i = 0; i < numSteps; i++){
			pointer += rBlurDir;
			vec2 uv = pointer/2+0.5;
			
			if(isValidCoords(uv)){
				vec3 shPos = texture(positionTexture, uv).rgb;
				if(shPos == vec3(0)){
					vec4 shAlb = texture(albedoTexture, uv);
					sunShaftsColor += shAlb.rgb * shAlb.a;
				}
			
			}else{
				divider--;
			}

		}
		
		sunShaftsColor /= divider;
	}
	
	return sunShaftsColor * mul;
}

void main(void){

	vec3 vs_position = texture(positionTexture, textureCoords).rgb;
	vec4 albedo = texture(albedoTexture, textureCoords);
	vec4 sampledNormal = texture(normalTexture, textureCoords);
	vec3 vs_normal = sampledNormal.rgb;

	float refrIndex = sampledNormal.a;
	vec3 sampledShineValues = texture(shineTexture, textureCoords).rgb;
	float sampledDepth = texture(shadowmapTexture, textureCoords*2).r;
	
	float reflectivity = sampledShineValues.x;
	float shineDamper = sampledShineValues.y;
	float fresnel = sampledShineValues.z;
	
	mat4 invViewMatrix = inverse(viewMatrix);
	vec3 ws_position = (invViewMatrix * vec4(vs_position, 1)).xyz;
			
	float diffuseMul = 1;
	if(length(vs_normal) < 0.1){
		mat4 viewRotationMatrix = viewMatrix;
		viewRotationMatrix[3] = vec4(0);
		//viewRotationMatrix = inverse(viewRotationMatrix);
		
		vs_normal = (viewRotationMatrix * vec4(sunDirection, 1)).xyz;
		diffuseMul = 0.5;
	}
	
	if(vs_position == vec3(0)){
		out_Diffuse = vec4(albedo);
		vec3 shafts = sampleSunShafts()/2;
		out_Diffuse.rgb += shafts;
		out_Bloom = shafts / 2;
		
		if(albedo.a > 0){
			out_Bloom += albedo.rgb * albedo.a * 3;
		}
		
		out_Bloom = createBloomColor(out_Bloom);
		
		return;
	}
	
	
	vec3 n_vs_normal = normalize(vs_normal);
	//vec3 vs_position = (viewMatrix * vec4(ws_position, 1)).xyz;
	vec3 n_vs_toCamera = normalize(vs_position);
	
	vec3 diffuseLighting = vec3(0);
	vec3 specularLighting = vec3(0);
	
	if(fresnel != 0){
		reflectivity *= pow((1-clamp(dot(n_vs_toCamera, -n_vs_normal), 0, 1)), fresnel);
	}
	
	
	for(int i = 0; i < lightCount; i++){
		vec3 vs_lightPos = lightPositions[i];
	
		vec3 vs_toLightVector = vs_lightPos - vs_position;
	
		float distance = length(vs_toLightVector);
		vec3 n_vs_lightVector = normalize(vs_toLightVector);
		float cosine = dot(n_vs_normal, n_vs_lightVector);
		float brightness = max(cosine, 0);
		float distanceFactor = sqrt(distance/lightStrengths[i]);
		diffuseLighting += lightColors[i] * brightness / distanceFactor;

		if(reflectivity != 0){
			vec3 n_vs_reflectedLight = reflect(n_vs_lightVector, n_vs_normal);
			float specularFactor = max(dot(n_vs_reflectedLight, n_vs_toCamera), 0);
			float dampedFactor = pow(specularFactor, shineDamper);
			float fresnelFactor = clamp(dot(n_vs_lightVector, n_vs_reflectedLight), 0, 1);
			fresnelFactor = fresnelFactor * fresnel;
			specularLighting += lightColors[i] * dampedFactor / distanceFactor;
		}

	}

	if(sunColor != vec3(0)){
	

		float shadow = shadowIntensity(ws_position, invViewMatrix, vs_position);
		if(shadow != 1){
			
			mat4 viewRotationMatrix = viewMatrix;
			viewRotationMatrix[3] = vec4(0);
		
			vec3 n_vs_sunDirection = (viewRotationMatrix * vec4(sunDirection, 1)).xyz;
			float cosine = dot(n_vs_normal, n_vs_sunDirection);
			float brightness = 0;
	
			brightness = max(cosine, 0);
	
			diffuseLighting += (1-shadow) * sunColor * brightness;
			
			if(reflectivity != 0){
				vec3 n_vs_reflectedLight = reflect(n_vs_sunDirection, n_vs_normal);
				float specularFactor = max(dot(n_vs_reflectedLight, n_vs_toCamera), 0);
				float dampedFactor = pow(specularFactor, shineDamper);
				float fresnelFactor = dot(n_vs_reflectedLight, n_vs_toCamera);
				fresnelFactor = fresnelFactor * fresnel;
				specularLighting += (1-shadow) * sunColor * dampedFactor;
			}
		}
	}
	
	if(!enableSSR && reflectivity != 0){
		vec3 n_vs_reflected = reflect(n_vs_toCamera, n_vs_normal);
		vec3 cubeCoords = (inverse(viewMatrix)*vec4(n_vs_reflected, 0)).xyz;
		
		vec4 reflectColor;
		if(reflectEnvMap){
			reflectColor = textureCube(envMap, cubeCoords);
			if(reflectColor.a < 1){
				reflectColor = textureCube(skybox, cubeCoords);
			}
		}else{
			reflectColor = textureCube(skybox, cubeCoords);
		}
		
		float fresnelFactor = dot(n_vs_reflected, n_vs_toCamera);
		fresnelFactor = fresnelFactor * fresnel;
		specularLighting += reflectColor.rgb;
	}else{
		out_Diffuse.a = reflectivity;
	}
	
	vec4 refraction = vec4(0);
	if(albedo.a < 1){
		
		vec3 n_ws_normal = (invViewMatrix * vec4(n_vs_normal, 0)).xyz;
		
		vec3 refrDir = refract((invViewMatrix * vec4(n_vs_toCamera, 0)).xyz, n_ws_normal, 1 / refrIndex);
		
		refraction = textureCube(envMap, refrDir);
		if(refraction.a < 1){
			vec4 skyRefract = textureCube(skybox, refrDir);
			vec3 sun = sampleSun(refrDir)*4 * (1-refraction.a);
			out_Bloom += sun*2;
			
			refraction.rgb = refraction.rgb * refraction.a + skyRefract.rgb * (1-refraction.a) + sun;
		}
	}
	
	diffuseLighting *= diffuseMul;
	if(ambientLighting != vec3(0)){
		diffuseLighting.r = max(ambientLighting.r, diffuseLighting.r);
		diffuseLighting.g = max(ambientLighting.g, diffuseLighting.g);
		diffuseLighting.b = max(ambientLighting.b, diffuseLighting.b);
	}
	
	float fogMul = 0;
	float depth = length(camPosition - ws_position);
	if(fogBorders != vec2(0, 0)){
		if(depth > fogBorders.y){
			fogMul = 1;
		}else if(depth > fogBorders.x){
			fogMul = (depth - fogBorders.x) / (fogBorders.y - fogBorders.x);
		}
	}



	out_Diffuse.rgb = (1-reflectivity) * (albedo.a * albedo.rgb * diffuseLighting + (1-albedo.a) * refraction.rgb) + specularLighting * reflectivity;
	out_Diffuse.rgb = fogMul * fogColor + (1-fogMul) * out_Diffuse.rgb;
	vec3 shafts = sampleSunShafts();
	out_Diffuse.rgb += shafts * 2;
	out_Bloom = createBloomColor(out_Diffuse.rgb);
	//out_Bloom += shafts / 2;
	
	if(length(textureCoords - vec2(0.5)) < 0.005){
		out_Diffuse.rgb = 1-out_Diffuse.rgb;
	}
	
	//out_Diffuse.rgb = vs_normal / 2 + 0.5;
}