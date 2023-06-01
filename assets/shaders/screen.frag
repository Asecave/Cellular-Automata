#version 330 core

in vec2 v_texCoords;
uniform sampler2D u_texture;

uniform vec2 frameDimensions;

layout(location=0) out vec4 fragColor;

const float diffuseA = 1f;
const float diffuseB = 0.345f;
const float feedRate = 0.0402f;
const float killRate = 0.062f;

vec3 get(int offsetX, int offsetY) {
	return texture2D(u_texture,
			(v_texCoords + vec2(offsetX, offsetY) / frameDimensions)).rgb;
}

vec3 laplace() {
	vec3 v = vec3(0f);
	v += get(0, 0) * -1f;
	v += get(1, 0) * 0.2f;
	v += get(0, 1) * 0.2f;
	v += get(-1, 0) * 0.2f;
	v += get(0, -1) * 0.2f;
	v += get(1, 1) * 0.05f;
	v += get(1, -1) * 0.05f;
	v += get(-1, 1) * 0.05f;
	v += get(-1, -1) * 0.05f;
	return v;
}

void main() {

	vec3 old = get(0, 0);
	float a = old.g;
	float b = old.b;
	vec3 laplace = laplace();

	float newA;
	float newB;
	newA = a + diffuseA * laplace.g - a * b * b + feedRate * (1 - a);
	newB = b + diffuseB * laplace.b + a * b * b - (killRate + feedRate) * b;

	newA = clamp(newA, 0f, 1f);
	newB = clamp(newB, 0f, 1f);

	fragColor = vec4(0f, newA, newB, 1f);
}
