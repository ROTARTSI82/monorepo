#version 330 core

layout (location = 0) in vec2 vertex_co;
layout (location = 1) in mat3 model;
layout (location = 4) in vec2 sampling_bl;
layout (location = 5) in vec2 sampling_extent;

uniform mat4 projection_mat;
uniform mat3 view_mat; // do i want to use a mat3 or mat4?

out vec2 tex_co;

void main() {
    // will this mess up the order of operations for pre-multiplication
    gl_Position = (vec4(view_mat * model * vec3(vertex_co, 1.0), 1.0));

    vec2 h = (sampling_extent / 2);
    tex_co = sampling_bl + h + vertex_co * h;
}
