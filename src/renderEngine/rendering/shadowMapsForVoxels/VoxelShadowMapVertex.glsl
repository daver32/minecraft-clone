#version 430

in vec3 position;
in vec3 normal;
in vec3 tangent;
in vec3 textureCoords;
in float ambientOcclusion;
in int displacement;

uniform vec3 mPos;

uniform float time;

out vertexData{
	vec3 texCoords;
	vec3 ws_position;
}pass1;

const float DISPLACEMENT_SPEED = 0.001;
const float DISPLACEMENT_FREQ = 10;
const float DISPLACEMENT_STRENGTH = 0.1;

void main(void){
	vec3 ws_position = position + mPos;
	
	if(displacement != 0){
		vec3 displacement = vec3(0);
		displacement.x = sin(position.z * DISPLACEMENT_FREQ + time * DISPLACEMENT_SPEED);
		displacement.y = cos(position.x * DISPLACEMENT_FREQ + time * DISPLACEMENT_SPEED);
		displacement.z = sin(position.y * DISPLACEMENT_FREQ + time * DISPLACEMENT_SPEED);
		ws_position.xyz += displacement * DISPLACEMENT_STRENGTH;
	}
	
	pass1.ws_position = ws_position;
	pass1.texCoords = textureCoords;
}
