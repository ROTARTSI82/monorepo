#version 120

varying vec4 v_col;
varying vec2 v_uv;

uniform sampler2D tex;

void main() {
    gl_FragColor = mix(v_col, texture2D(tex, v_uv), 0.5);
}