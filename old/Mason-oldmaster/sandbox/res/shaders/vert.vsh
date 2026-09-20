#version 120

attribute vec4 coord;
attribute vec2 texCoord;

varying vec2 v_TexCoord;

uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;

void main() {
    gl_Position = (u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix) * coord;
    v_TexCoord = texCoord;
}
