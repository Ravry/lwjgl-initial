#version 330 core
in vec2 texCoord;
out vec4 FragColor;

uniform sampler2D fontTexture;
uniform vec4 fontColor;

void main() {
    float alpha = texture(fontTexture, texCoord).r;
    FragColor = vec4(fontColor.rgb, fontColor.a * alpha);
}