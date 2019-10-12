#version 120

attribute vec3 pos;
attribute vec3 normal;
attribute vec2 uv;
attribute vec4 color;

varying vec4 v_col;
varying vec2 v_uv;

void main() {
    gl_Position = vec4(pos, 1.0);

    v_uv = vec2(pos.x, pos.y);

    v_col = vec4(abs(pos.x), abs(pos.y), abs(pos.z), 1.0);
}