#version 330 core

layout (location = 0) in vec2 vertex_co;
layout (location = 1) in mat4 model;
layout (location = 5) in vec2 sampling_bl;
layout (location = 6) in vec2 sampling_extent;

uniform mat4 projection_mat;
uniform mat4 view_mat; // do i want to use a mat3 or mat4?

out vec2 tex_co;

out mat4 dbg_test;

void main() {
    // will this mess up the order of operations for pre-multiplication
    dbg_test = mat4(model);
//    gl_Position = projection_mat * vec4(view_mat * model * vec3(vertex_co, 1.0), 1.0);
    gl_Position = projection_mat * view_mat * model * vec4(vertex_co.x, vertex_co.y, 1, 1);

    vec2 h = (sampling_extent / 2);
    tex_co = sampling_bl + h + vertex_co * h;
}
