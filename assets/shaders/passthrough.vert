
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {

	gl_FragColor = sampler2D(u_texture, v_texCoords);
}
