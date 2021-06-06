// This fragment shader is BORING AS FUCK
#version 330 core

layout(location = 0) out vec4 diffuse_color;

uniform sampler2D tex;
in vec2 tex_co;
in float alpha_mult_io;

void main() {
    diffuse_color = texture2D(tex, tex_co);
    diffuse_color.w *= alpha_mult_io;
}
