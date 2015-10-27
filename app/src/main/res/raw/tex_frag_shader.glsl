precision mediump float;

uniform sampler2D u_TextureUnit;
uniform sampler2D u_TextureUnit2;
varying vec2 v_TextureCoords;

void main(){
    gl_FragColor = 0.25 * texture2D( u_TextureUnit, v_TextureCoords) + 0.75 * texture2D( u_TextureUnit2, v_TextureCoords);
}