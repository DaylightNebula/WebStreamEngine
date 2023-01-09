#version 450 core

in vec2 pass_uvs;

out vec4 out_Color;

uniform vec4 color;

void main(){
	out_Color = color;
}