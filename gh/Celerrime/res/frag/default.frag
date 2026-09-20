// This fragment shader is no longer boring
#version 330 core

layout(location = 0) out vec4 diffuse_color;

uniform sampler2D tex;

in vec2 tex_co; // this variable name is now pretty misleading lol
in float alpha_mult_io;

flat in vec2 sample_extent_io;
flat in vec2 sample_bl_io;

void main() {
    diffuse_color = texture(tex, sample_bl_io + mod(tex_co, sample_extent_io));
    diffuse_color.w *= alpha_mult_io;
}
