#version 430

in vertexData {
    vec3 vs_position;
    vec3 color;
}pass2;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

layout (location = 0) out vec4 out_Albedo;
layout (location = 1) out vec4 out_Normal;
layout (location = 2) out vec3 out_Position;
layout (location = 3) out vec3 out_Shine;

void main(void){
	out_Albedo = vec4(pass2.color,1);
	out_Normal = vec4(0);
	out_Position = pass2.vs_position;
	out_Shine = vec3(0);
}