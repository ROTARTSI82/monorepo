#version 120

varying vec2 texCoord;

uniform sampler2D texSlot;

void main() {
    gl_FragColor = texture2D(texSlot, texCoord);
}
