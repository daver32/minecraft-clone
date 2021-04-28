#version 400 core

in vec2 textureCoords;

uniform sampler2D depthTexture;
uniform sampler2D normalTexture;
uniform sampler2D positionTexture;

uniform float multiplier;

uniform vec3[100] sampleKernel;
uniform int kernelSize;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

layout (location = 0) out vec4 out_Color;

const float radius = 2.5;

const float nearPlane = 0.001;
const float farPlane = 1000;

float getDepth(vec2 coords){
	vec3 vs_position = texture(positionTexture, coords).rgb;
	return -vs_position.z;
}

float invertDepth(float d){
	return 1-d;
}

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

vec3 randVector(vec2 seed){
	vec3 result = vec3(0);
	result.x = rand(seed);
	result.y = rand(vec2(seed.y, seed.x));
	result.z = rand(-seed*2);
	return result;
}

bool checkCoords(vec2 coords){
	return !(coords.x > 1 || coords.y > 1 || coords.x < 0 || coords.y < 0);
}

float linearize(float depth){
	return depth;
	//return (2 * nearPlane) / (farPlane + nearPlane - depth * (farPlane - nearPlane));
}

float calcOcclusion(mat3 toTangentSpace, vec3 origin, float depth, float randValue){
	float result = 0;
	int divider = kernelSize;
	
	for(int i = 0; i < kernelSize; i++){
	
		vec3 point = toTangentSpace * sampleKernel[i];
		point = point * radius + origin;
		
		vec4 ss_position = projectionMatrix * vec4(point, 1.0);
		
		vec2 coords = ss_position.xy / ss_position.w / 2 + 0.5;
		if(!checkCoords(coords)){
			divider--;
			continue;
		}
		
		//float sampleDepth = linearize((texture(depthTexture, coords).r)) + 0.01;
		
		float sampleDepth = getDepth(coords) + 0.000;
		
		float diff = sampleDepth - depth;
		if(diff > 0 || abs(diff) > radius){
			result += 1;
		}
	}
	return result / divider;
}

void main(void){
	float sampledDepth = texture(depthTexture, textureCoords).r;
	if(sampledDepth > 0.99998){
		out_Color = vec4(1);
		//return;
	}

	//float depth = linearize(sampledDepth);

	
	
	
	vec3 n_vs_normal = normalize(texture(normalTexture, textureCoords).rgb);
	vec3 vs_position = texture(positionTexture, textureCoords).rgb;
	float depth = -vs_position.z;
	
	vec3 randomVec = normalize(randVector(textureCoords));
	//randomVec = normalize(vec3(1,1,1));
	vec3 n_vs_tangent = normalize(randomVec - n_vs_normal * dot(randomVec, n_vs_normal));
	vec3 n_vs_bitangent = cross(n_vs_normal, n_vs_tangent);
	mat3 toTangentSpace = mat3(n_vs_tangent, n_vs_bitangent, n_vs_normal);
	
	float occlusion = calcOcclusion(toTangentSpace, vs_position, depth, randomVec.x);
	
	//out_Color = vec4(0,0,0,1-occlusion);
	out_Color = vec4(vec3(occlusion),1);
	//out_Color = vec4(vec3(depth), 1);
}