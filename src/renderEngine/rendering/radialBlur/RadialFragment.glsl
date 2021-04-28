#version 400 core

in vec2 textureCoords;
in vec2 ss_position;
uniform sampler2D diffuse;

uniform bool isOutwards;
uniform float strength;
uniform vec2 center;

out vec4 out_Color;

const int num_steps = 100;

bool isValidCoords(vec2 coords){
	if(coords.x > 0 && coords.y > 0 && coords.x < 1 && coords.y < 1){
		return true;
	}
	return false;
}

void main(void){
	vec2 offsetVector = ss_position - center;
	
	offsetVector *= strength;
	offsetVector /= num_steps;
	
	if(isOutwards){
		offsetVector = - offsetVector;
	}
	
	vec3 finalColor = vec3(0, 0, 0);
	vec2 pointer = ss_position;
	for(int i = 0; i < num_steps; i++){
		pointer += offsetVector;
		offsetVector /= 1.05;
		vec2 uv = (pointer+1)/2;
		if(isValidCoords(uv)){
			vec3 sampledColor = texture(diffuse, uv).xyz;
			finalColor += sampledColor;
		}

	}
	finalColor /= num_steps;
	

	out_Color = vec4(finalColor, 1);
}



