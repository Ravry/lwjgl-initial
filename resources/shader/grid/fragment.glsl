#version 330 core

layout (location = 0) out vec4 FragColor;

uniform vec2 gridSize = vec2(10.0);
uniform vec2 subGridSize = vec2(1.0);
uniform float fadeDistance = 15.0;
uniform vec3 cameraPos;
in vec3 outWorldPos;

float gridLine(float position, float gridSize, float thickness) {
    float halfGrid = gridSize * 0.5;
    float pos = position / gridSize;
    float distToGrid = abs(fract(pos + 0.5) - 0.5) * gridSize;
    float derivative = fwidth(position);
    return 1.0 - smoothstep(thickness * derivative, (thickness + 1.0) * derivative, distToGrid);
}

void main() {
    float dist = length(outWorldPos.xz - cameraPos.xz);
    float fadeStart = fadeDistance * 0.6;
    float fadeFactor = 1.0 - smoothstep(fadeStart, fadeDistance, dist);

    float originFade = 1.0 - smoothstep(gridSize.x * 5.0, gridSize.x * 10.0, length(outWorldPos.xz));

    float majorX = gridLine(outWorldPos.x, gridSize.x, 0.5);
    float majorZ = gridLine(outWorldPos.z, gridSize.y, 0.5);
    float majorGrid = max(majorX, majorZ);

    float minorX = gridLine(outWorldPos.x, subGridSize.x, 0.3);
    float minorZ = gridLine(outWorldPos.z, subGridSize.y, 0.3);
    float minorGrid = max(minorX, minorZ) * (1.0 - majorGrid);

    vec3 xColor = vec3(0.7, 0.7, 0.7);
    vec3 zColor = vec3(0.8, 0.8, 0.8);
    vec3 subColor = vec3(0.8);

    float centerX = 1.0 - smoothstep(0.0, 0.02, abs(outWorldPos.x));
    float centerZ = 1.0 - smoothstep(0.0, 0.02, abs(outWorldPos.z));
    float centerLine = max(centerX, centerZ);

    vec3 finalColor = mix(
    mix(subColor, mix(xColor, zColor, float(majorZ > majorX)), majorGrid),
    vec3(1.0), centerLine * 0.5
    );

    float alpha = (majorGrid * 0.6 + minorGrid * 0.3) * fadeFactor * originFade;

    if (alpha < 0.01) {
        discard;
    } else {
        FragColor = vec4(finalColor, alpha);
    }
}
