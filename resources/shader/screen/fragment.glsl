#version 330 core

layout (location = 0) out vec4 FragColor;

uniform sampler2D _MainTex;
uniform sampler2D _DepthTex;

uniform int g_Active;

in vec2 TexCoord;

void main() {
    if (g_Active == 0) {
        FragColor = vec4(texture(_MainTex, TexCoord).rgb, 1);
    }
    else if (g_Active == 1) {
        FragColor = vec4(vec3(texture(_DepthTex, TexCoord).r), 1);
    }
}