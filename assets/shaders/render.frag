#version 330 core

in vec2 v_texCoords;
uniform sampler2D u_texture;
uniform int frame;

uniform vec2 frameDimensions;

layout(location=0) out vec4 fragColor;

vec3 get(int offsetX, int offsetY) {
	return texture2D(u_texture,
			(v_texCoords + vec2(offsetX, offsetY) / frameDimensions)).rgb;
}

void main() {

	vec3 c = get(0, 0);

	float r = c.b * (-sin(frame / 10000.0) + 1) / 2;
	float g = c.b * (cos(frame / 10000.0) + 1) / 2;
	float b = c.b * (sin(frame / 10000.0) + 1) / 2;

	fragColor = clamp(vec4(r, g, b, 1f), 0f, 1f);
}
