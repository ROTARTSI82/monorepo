#version 120

attribute vec3 pos;
attribute vec2 uv;

varying vec2 v_uv;

uniform mat4 proj;
uniform mat4 view;

void main() {
    gl_Position = proj * view * vec4(pos, 1.0);
    //    gl_Position = vec4(pos, 1.0);

    v_uv = vec2(pos.x, pos.y);
    //    v_uv = uv;
}