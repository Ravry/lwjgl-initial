#version 330 core

layout (location = 0) out vec4 FragColor;

uniform vec2 gridSize = vec2(5.0);
uniform vec2 subGridSize = vec2(1.0);
uniform float lineWidth = 0.01;
uniform float subLineWidth = 0.05;
uniform float fadeDistance = 20.0;

uniform vec3 cameraPos;
in vec3 outWorldPos;

void main() {
    float dist = length(outWorldPos - vec3(cameraPos.x, 0.0, cameraPos.z));

    float fadeFactor = clamp(1.0 - (dist / fadeDistance), 0.0, 1.0);

    float majorX = step(1.0 - lineWidth, abs(fract(outWorldPos.x / gridSize.x)));
    float majorY = step(1.0 - lineWidth, abs(fract(outWorldPos.z / gridSize.y)));
    float majorGrid = max(majorX, majorY);

    float minorX = step(1.0 - subLineWidth, abs(fract(outWorldPos.x / subGridSize.x)));
    float minorY = step(1.0 - subLineWidth, abs(fract(outWorldPos.z / subGridSize.y)));
    float minorGrid = max(minorX, minorY);

    if (majorGrid >= 1.0) {
        FragColor = vec4(vec3(1), 0.8 * fadeFactor);
    } else if (minorGrid >= 1.0) {
        FragColor = vec4(vec3(.8), 0.5 * fadeFactor);
    } else {
        discard;
    }
}
