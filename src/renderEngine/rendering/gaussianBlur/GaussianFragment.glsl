#version 400 core

in vec2 textureCoords;
uniform sampler2D diffuse;

uniform bool isHorizontal; //eadawdw
uniform int diameter;

out vec4 out_Color;

bool isValidCoords(vec2 coords){
	if(coords.x > 0 && coords.y > 0 && coords.x < 1 && coords.y < 1){
		return true;
	}
	return false;
}

void main(void){
	if(diameter > 0){
		vec4 avg = vec4(0);
		vec2 textureSize = textureSize(diffuse,0);
		vec2 perPixel = isHorizontal ? vec2(1.0/textureSize.x, 0) : vec2(0, 1.0/textureSize.y);
		
		int steps = diameter;
		float radius = diameter/2.0;
		
		float total = 0;
		for(int i = 0; i < diameter+1; i++){
			float pos = i - radius;
			vec2 coords = vec2(textureCoords.x + pos*perPixel.x, 1-textureCoords.y + pos*perPixel.y);
			float toCenter = abs(pos);
			float toEdge = ((radius - toCenter)/(pow(radius, 2)));
			
			if(isValidCoords(coords)){
				vec4 color = texture(diffuse, coords);
				avg += color * toEdge;
				total += toEdge;
			}
			
		}
		
		if(total < 1){
			vec2 coords = vec2(textureCoords.x, 1-textureCoords.y);
			vec4 color = texture(diffuse, coords);
			avg += color * (1 - total);
			
		}
		
		out_Color = avg;

	}else{
		vec2 coords = vec2(textureCoords.x, 1-textureCoords.y);
		out_Color = texture(diffuse, coords);
	}
}



