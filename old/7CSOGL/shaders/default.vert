#version 330 core
layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 inTexCoord;

out vec2 texCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    gl_Position = projection * view * model * (vec4(pos, 1.0) + vec4((gl_InstanceID % 16) * 8 - 64, ((gl_InstanceID % 256) / 16) * 8 - 64, (gl_InstanceID / 256) * 8 - 64, 0));
    texCoord = inTexCoord;
}
