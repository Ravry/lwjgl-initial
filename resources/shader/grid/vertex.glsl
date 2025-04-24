#version 330 core

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec2 inTexCoord;

out vec3 outWorldPos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 cameraPos;

void main() {
    vec4 worldPos = model * vec4(inPosition, 1.0);
    worldPos.xyz += vec3(cameraPos.x, 0, cameraPos.z);
    gl_Position = projection * view * worldPos;
    outWorldPos = worldPos.xyz;
}