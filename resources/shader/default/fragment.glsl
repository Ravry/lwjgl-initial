#version 330 core

layout (location = 0) out vec4 FragColor;

uniform sampler2D _MainTex;

in vec2 outTexCoord;

void main() {
    FragColor = vec4(texture(_MainTex, outTexCoord).rgb, 1);
}