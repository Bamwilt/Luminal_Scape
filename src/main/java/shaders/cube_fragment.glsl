#version 330 core
in vec3 vertexColor;
out vec4 FragColor;

uniform vec3 overrideColor;

void main() {
    FragColor = vec4(vertexColor * overrideColor, 1.0);
}
