#version 330 core

layout (location = 0) in vec2 vertex_co;
layout (location = 1) in mat2 model;
layout (location = 3) in vec3 translate;
layout (location = 4) in float alpha_mult;

layout (location = 5) in vec2 sampling_bl;
layout (location = 6) in vec2 sampling_extent;

layout (location = 7) in vec2 num_tiles;

uniform mat4 projection_mat;
uniform mat4 view_mat;

out vec2 tex_co;
out float alpha_mult_io;

flat out vec2 sample_extent_io;
flat out vec2 sample_bl_io;

void main() {
    alpha_mult_io = alpha_mult;
    sample_extent_io = sampling_extent;
    sample_bl_io = sampling_bl;

    {
        mat4 model_mat4 = mat4(model);
        model_mat4[3] = vec4(translate, 1.0);
        gl_Position = projection_mat * view_mat * model_mat4 * vec4(vertex_co.x, vertex_co.y, 0, 1);
    }

    tex_co = num_tiles * sampling_extent * (vertex_co + vec2(1, 1)) / 2;
}
