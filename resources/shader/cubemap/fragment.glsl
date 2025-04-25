#version 330 core

layout (location = 0) out vec4 FragColor;

in vec3 TexCoord;

uniform samplerCube skybox;

void main() {
    FragColor = vec4(texture(skybox, TexCoord).rgb, 1);
}
