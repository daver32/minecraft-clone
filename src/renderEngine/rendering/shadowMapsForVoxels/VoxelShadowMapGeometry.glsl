#version 430

layout (triangles) in;
layout (triangle_strip, max_vertices = 12) out;

in vertexData{
	vec3 texCoords;
	vec3 ws_position;
}pass1[];
 
out vertexData {
	vec3 texCoords;
}pass2;

uniform mat4[4] pvMatrices;

uniform bool[4] cascadeCulls;


void main(void){

	for(int c = 0; c < 4; c++){

			
		for(int v = 0; v < 3; v++){
			vec4 ss_position = pvMatrices[c] * vec4(pass1[v].ws_position, 1);
				
	
			gl_Position = ss_position;
			gl_ViewportIndex = c;
				
			pass2.texCoords = pass1[v].texCoords;
				
			EmitVertex();
		}
			
		EndPrimitive();
		
	}
}