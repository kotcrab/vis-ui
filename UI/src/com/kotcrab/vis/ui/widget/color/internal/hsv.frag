#ifdef GL_ES
precision mediump float;
#endif

uniform int u_mode; //defined in ChannelBar.java
uniform float u_h;
uniform float u_s;
uniform float u_v;

varying vec4 v_color;
varying vec2 v_texCoords;

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    if(u_mode == 4) gl_FragColor = v_color * vec4(hsv2rgb(vec3(v_texCoords.s, u_s, u_v)), 1.0); //h bar
    if(u_mode == 5) gl_FragColor = v_color * vec4(hsv2rgb(vec3(u_h, v_texCoords.s, u_v)), 1.0); //s bar
    if(u_mode == 6) gl_FragColor = v_color * vec4(hsv2rgb(vec3(u_h, u_s, v_texCoords.s)), 1.0); //v bar
}
