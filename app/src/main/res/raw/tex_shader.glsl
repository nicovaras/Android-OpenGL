uniform mat4 u_Matrix;

attribute vec4 a_Position;
attribute vec2 a_TextureCoords;

varying vec2 v_TextureCoords;

void main(){
    v_TextureCoords = a_TextureCoords;
    gl_Position = u_Matrix * a_Position;
}