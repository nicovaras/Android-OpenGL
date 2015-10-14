package com.wert.openGL;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.wert.util.ShaderHelper;
import com.wert.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import android.opengl.Matrix;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class OpenGLRenderer implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENTS = 4;
    private static final int COLOR_COMPONENTS = 3;
    private static final int BYTES_FLOAT= 4;
    private static final int STRIDE = (POSITION_COMPONENTS + COLOR_COMPONENTS) * BYTES_FLOAT;
    private static final String A_COLOR = "a_Color";
    private static final String A_POSITION = "a_Position";
    private static final String U_MATRIX = "u_Matrix";
    private int aColorLocation;
    private int aPositionLocation;
    private int uMatrixLocation;
    private final FloatBuffer vertexData;
    private final Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    public OpenGLRenderer(Context context){
        this.context = context;
        float[] tableVertices = {
                // Board
                    0,     0,    0, 1.4f,   1f,   1f,   1f,
                -0.5f, -0.8f,    0, 1.f, 0.7f, 0.7f, 0.7f,
                   0f, -0.8f,    0, 1.f,   1f,   1f,   1f,
                 0.5f, -0.8f,    0, 1.f, 0.7f, 0.7f, 0.7f,
                 0.5f,    0f,    0, 1.4f,   1f,   1f,   1f,
                 0.5f,  0.8f,    0, 1.8f, 0.7f, 0.7f, 0.7f,
                   0f,  0.8f,    0, 1.8f,   1f,   1f,   1f,
                -0.5f,  0.8f,    0, 1.8f, 0.7f, 0.7f, 0.7f,
                -0.5f,    0f,    0, 1.4f,   1f,   1f,   1f,
                -0.5f, -0.8f,    0, 1.f, 0.7f, 0.7f, 0.7f,

                // Line
                -0.5f,    0f,    0, 1.4f,   1f, 0.5f, 0.2f,
                 0.5f,    0f,    0, 1.4f,  .2f, 0.4f, 0.8f,

                //Players
                   0f, -0.6f,    0, 1f,  0f,   0f,   1f,
                   0f,  0.6f,    0, 1.8f,  1f,   0f,   0f,

                // Board external
                -0.6f, -0.85f,    0, 1.f,0f,0f,0f,
                 0.6f,  0.85f,    0, 1.8f,0f,0f,0f,
                -0.6f,  0.85f,    0, 1.8f,0f,0f,0f,
                -0.6f, -0.85f,    0, 1.f,0f,0f,0f,
                 0.6f, -0.85f,    0, 1.f,0f,0f,0f,
                 0.6f,  0.85f,    0, 1.8f,0f,0f,0f
        };
        vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_FLOAT)
                               .order(ByteOrder.nativeOrder())
                               .asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.098f, 0.098f, 0.439f, 0.0f );
        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.vertex_shader);
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        int program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        ShaderHelper.validateProgram(program);
        glUseProgram(program);

        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENTS, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENTS);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENTS, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        Matrix.perspectiveM(projectionMatrix, 0, 40,
                (float) width / (float) height, 1f, 10f);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        final float[] tmp = new float[16];
        Matrix.multiplyMM(tmp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(tmp, 0, projectionMatrix, 0, tmp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLES, 14, 6);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 10);
        glDrawArrays(GL_LINES, 10, 2);
        glDrawArrays(GL_POINTS, 12, 1);
        glDrawArrays(GL_POINTS, 13, 1);
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
    }
}
