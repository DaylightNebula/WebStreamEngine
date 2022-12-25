#version 450 core

in vec3 position;
in vec2 uvs;

out vec2 pass_uvs;

void main(void){
	gl_Position = vec4(position, 1.0);
	pass_uvs = uvs;
}