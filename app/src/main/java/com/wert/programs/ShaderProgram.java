package com.wert.programs;

import android.content.Context;

import com.wert.util.ShaderHelper;
import com.wert.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

public class ShaderProgram {
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_TEXTURE_UNIT2 = "u_TextureUnit2";
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoords";
    protected static final String U_COLOR = "u_Color";

    protected final int program;
    protected ShaderProgram(Context context, int vertexShaderId,
                            int fragmentShaderId){
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(
                        context, vertexShaderId),
                TextResourceReader.readTextFileFromResource(
                        context, fragmentShaderId));
    }

    public void useProgram(){
        glUseProgram(program);
    }
}
