#version 120

attribute vec2 pos;
attribute vec2 inTexCoord;

varying vec2 texCoord;

void main() {
    gl_Position = vec4(pos.x, pos.y, 0.0, 1.0);
    texCoord = inTexCoord;
}
