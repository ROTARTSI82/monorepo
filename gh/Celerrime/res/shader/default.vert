#version 330 core

layout (location = 0) in vec2 vertex_co;
layout (location = 1) in mat2 model;
layout (location = 3) in vec3 translate;
layout (location = 4) in float alpha_mult;

layout (location = 5) in vec2 sampling_bl;
layout (location = 6) in vec2 sampling_extent;

uniform mat4 projection_mat;
uniform mat4 view_mat;

out vec2 tex_co;
out float alpha_mult_io;

void main() {
    mat4 model_mat4 = mat4(model);
    model_mat4[3] = vec4(translate, 1.0);

    alpha_mult_io = alpha_mult;

    gl_Position = projection_mat * view_mat * model_mat4 * vec4(vertex_co.x, vertex_co.y, 0, 1);

    vec2 h = (sampling_extent / 2);
    tex_co = sampling_bl + h + vertex_co * h;
}
