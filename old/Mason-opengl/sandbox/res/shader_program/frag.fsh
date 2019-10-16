#version 120

varying vec2 v_uv;

uniform sampler2D tex;

void main() {
    gl_FragColor = texture2D(tex, v_uv);
    //    gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
}