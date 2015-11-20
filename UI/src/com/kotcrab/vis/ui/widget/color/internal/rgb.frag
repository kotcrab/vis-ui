#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

uniform int u_mode; //defined in ChannelBar.java
uniform float u_r;
uniform float u_g;
uniform float u_b;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    if(u_mode == 0) gl_FragColor = v_color * texture2D(u_texture, v_texCoords) * vec4(u_r, u_g, u_b, v_texCoords.s); //alpha bar

    if(u_mode == 4) gl_FragColor = v_color * texture2D(u_texture, v_texCoords) * vec4(v_texCoords.s, u_g, u_b, 1.0); //r bar
    if(u_mode == 5) gl_FragColor = v_color * texture2D(u_texture, v_texCoords) * vec4(u_r, v_texCoords.s, u_b, 1.0); //g bar
    if(u_mode == 6) gl_FragColor = v_color * texture2D(u_texture, v_texCoords) * vec4(u_r, u_g, v_texCoords.s, 1.0); //b bar
}