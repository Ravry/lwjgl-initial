#version 330 core

layout (location = 0) out vec4 FragColor;

uniform sampler2D _MainTex;

in vec2 TexCoord;

void main() {
    FragColor = vec4(texture(_MainTex, TexCoord).rgb, 1);
}