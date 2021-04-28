#version 430

layout (points) in;
layout (triangle_strip, max_vertices = 6) out;

in vertexData{
	vec3 ws_position;
	float size;
	vec3 particleColor;
}pass1[];
 
out vertexData {
    vec3 vs_position;
    vec3 color;
}pass2;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

const float[4][2] quad = {
	{-0.5, -0.5},
	{0.5, -0.5},
	{-0.5, 0.5},
	{0.5, 0.5}
};

const int[2][3] quadIndices = {
	{0, 1, 2},
	{3, 2, 1}
};

void main(void){
	
	vec4 vs_position = viewMatrix * vec4(pass1[0].ws_position, 1);

	
	for(int t = 0; t < 2; t++){
		for(int i = 0; i < 3; i++){
			pass2.vs_position = vs_position.xyz;
			pass2.color = pass1[0].particleColor;
	
			vec4 vs_pos = vs_position.xyzw;
			int index = quadIndices[t][i];
			vec2 offset = vec2(quad[index][0], quad[index][1]);
			vs_pos.xy += offset * pass1[0].size;

			gl_Position = projectionMatrix * vs_pos;
		
			EmitVertex();
		}
		EndPrimitive();
	}
}