#ifdef GL_ES
precision mediump float;
#endif

uniform float u_width;
uniform float u_height;
uniform float u_gridSize;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    vec2 res = v_texCoords.xy * vec2(u_width / u_height, 1) / vec2(u_width, u_height);
	vec2 uvx = floor(u_height * u_width / u_gridSize * res);
	vec2 uvy = floor(u_height * u_height / u_gridSize * res);
	bool isEven = (mod(uvx.x + uvy.y, 2.0) == 0.0);
	gl_FragColor = isEven ? v_color * vec4(0.6, 0.6, 0.6, 1.0) : v_color * vec4(0.4, 0.4, 0.4, 1.0);
}
