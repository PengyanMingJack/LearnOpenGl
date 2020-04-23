package com.pym.learnopengl

import android.opengl.GLES20

object Shader {
    val VERTEX_SHADER = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 a_texCoord;" +
            "varying vec2 v_texCoord;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  v_texCoord = a_texCoord;" +
            "}"
    val FRAGMENT_SHADER = "precision mediump float;" +
            "varying vec2 v_texCoord;" +
            "uniform sampler2D s_texture;" +
            "void main() {" +
            "  gl_FragColor = texture2D(s_texture, v_texCoord);" +
            "}"

    val VERTEX =
        floatArrayOf(1f, 1f, 0f, -1f, 1f, 0f, -1f, -1f, 0f, 1f, -1f, 0f)
    val VERTEX_INDEX = shortArrayOf(
        0, 1, 2, 0, 2, 3
    )
    val TEX_VERTEX = floatArrayOf( // in clockwise order:
        1f, 0f,// bottom right
        0f, 0f,//bottom left
        0f, 1f,  // top left
        1f, 1f// top right
    )

    fun loadShader(type: Int, shaderCode: String?): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}