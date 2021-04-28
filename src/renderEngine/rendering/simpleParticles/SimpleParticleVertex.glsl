#version 430

in vec3 position;
in float scale;
in vec3 color; 

out vertexData{
	vec3 ws_position;
	float size;
	vec3 particleColor;
}pass1;



void main(void){
	pass1.ws_position = position + gl_InstanceID;
	pass1.size = scale;
	pass1.particleColor = color;
	
	
}
