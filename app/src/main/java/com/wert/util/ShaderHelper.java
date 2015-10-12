package com.wert.util;

import android.util.Log;

import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode){
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode){
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode){
        final int shaderId = glCreateShader(type);
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);
        Log.v(TAG, "Results of compiling source:" + glGetShaderInfoLog(shaderId));
        return shaderId;
    }

    public static int linkProgram(int vertexId, int fragmentId){
        final int programId = glCreateProgram();
        glAttachShader(programId, vertexId);
        glAttachShader(programId, fragmentId);
        glLinkProgram(programId);
        return programId;
    }

    public static boolean validateProgram(int programId){
        glValidateProgram(programId);

        final int[] status = new int[1];
        glGetProgramiv(programId, GL_VALIDATE_STATUS, status, 0);
        if(status[0] == 0){
            Log.e(TAG, "Error building shader program: " + glGetProgramInfoLog(programId));
        }

        return status[0] != 0;
    }
}
