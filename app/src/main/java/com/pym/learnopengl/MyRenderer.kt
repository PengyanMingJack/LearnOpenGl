package com.pym.learnopengl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.*
import android.os.Environment
import android.os.Handler
import android.util.Log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyRenderer : GLSurfaceView.Renderer {
    private var mContext: Context? = null
    private var mVertexBuffer: FloatBuffer? = null
    private var mTexVertexBuffer: FloatBuffer? = null
    private var mVertexIndexBuffer: ShortBuffer? = null
    private var mMVPMatrix = FloatArray(16)

    private var mProgram = 0
    private var mPositionHandle = 0
    private var mMatrixHandle = 0
    private var mTexCoordHandle = 0
    private var mTexSamplerHandle = 0
    private var mTexName = 0

    constructor(context: Context?) {
        this.mContext = context
        mVertexBuffer = ByteBuffer.allocateDirect(Shader.VERTEX.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(Shader.VERTEX)
        mVertexBuffer?.position(0)

        mVertexIndexBuffer = ByteBuffer.allocateDirect(Shader.VERTEX_INDEX.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(Shader.VERTEX_INDEX)
        mVertexIndexBuffer?.position(0)

        mTexVertexBuffer = ByteBuffer.allocateDirect(Shader.TEX_VERTEX.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(Shader.TEX_VERTEX)
        mTexVertexBuffer?.position(0)
    }

    override fun onDrawFrame(gl: GL10) {
        Log.d("TryOpenGL", "onDrawFrame: ")

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0)
        GLES20.glUniform1i(mTexSamplerHandle, 0)
        // 用 glDrawElements 来绘制，mVertexIndexBuffer 指定了顶点绘制顺序
        // 用 glDrawElements 来绘制，mVertexIndexBuffer 指定了顶点绘制顺序
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, Shader.VERTEX_INDEX.size,
            GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer
        )

        var bitmap = BitmapFactory.decodeResource(
            mContext!!.resources, resImage
        )
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()


        //图片保存到本地
        saveImage(1080, 1920)
//        createBitmapFromGLSurface(0, 0, 1080, 1920, gl)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        Log.d("TryOpenGL", "onSurfaceChanged: ")

        Matrix.perspectiveM(
            mMVPMatrix,
            0,
            45f,
            width.toFloat() / height,
            0.1f,
            100f
        )
        Matrix.translateM(mMVPMatrix, 0, 0f, 0f, -5f)
    }

    var resImage = R.mipmap.kaihe_00000
    fun setPic(resImage: Int) {
        this.resImage = resImage
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        Log.d("TryOpenGL", "onSurfaceCreated: ")

        mProgram = GLES20.glCreateProgram()
        val vertexShader: Int =
            Shader.loadShader(GLES20.GL_VERTEX_SHADER, Shader.VERTEX_SHADER)
        val fragmentShader: Int =
            Shader.loadShader(GLES20.GL_FRAGMENT_SHADER, Shader.FRAGMENT_SHADER)
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        GLES20.glLinkProgram(mProgram)

        GLES20.glUseProgram(mProgram)

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoord")
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        mTexSamplerHandle = GLES20.glGetUniformLocation(mProgram, "s_texture")

        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(
            mPositionHandle, 3, GLES20.GL_FLOAT, false,
            12, mVertexBuffer
        )

        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(
            mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0,
            mTexVertexBuffer
        )

        val texNames = IntArray(1)
        GLES20.glGenTextures(1, texNames, 0)
        mTexName = texNames[0]
        var bitmap = BitmapFactory.decodeResource(
            mContext!!.resources, resImage
        )
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexName)
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_REPEAT
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_REPEAT
        )
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
    }

    fun destroy() {
        GLES20.glDeleteTextures(1, intArrayOf(mTexName), 0)
    }

    fun saveImage(width: Int, height: Int) {
        if (isPhoto) {
            isPhoto = false
            val rgbaBuf = ByteBuffer.allocateDirect(width * height * 4)
            rgbaBuf.position(0)
            rgbaBuf.order(ByteOrder.LITTLE_ENDIAN)
            val start = System.nanoTime()
            GLES20.glReadPixels(
                0,
                0,
                width,
                height,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                rgbaBuf
            )
            val end = System.nanoTime()
            Log.d("TryOpenGL", "glReadPixels: " + (end - start))
            saveRgb2Bitmap(rgbaBuf, width, height)
        }
    }

    private fun saveRgb2Bitmap(buf: Buffer?, width: Int, height: Int) {
        var bos: BufferedOutputStream? = null
        try {
            val file = File(mContext?.filesDir?.absolutePath + "/gl.png")
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            bos = BufferedOutputStream(FileOutputStream(file))
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bmp.copyPixelsFromBuffer(buf)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos)
            bmp.recycle()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (bos != null) {
                try {
                    bos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    var isPhoto = true
    //图片保存并做反转
    fun createBitmapFromGLSurface(x: Int, y: Int, w: Int, h: Int, gl: GL10): Bitmap? {
        if (isPhoto) {
            isPhoto = false
            val bitmapBuffer = IntArray(w * h)
            val bitmapSource = IntArray(w * h)
            val intBuffer = IntBuffer.wrap(bitmapBuffer)
            intBuffer.position(0)
            try {
                gl.glReadPixels(
                    x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
                    intBuffer
                )
                var offset1: Int
                var offset2: Int
                for (i in 0 until h) {
                    offset1 = i * w
                    offset2 = (h - i - 1) * w
                    for (j in 0 until w) {
                        val texturePixel = bitmapBuffer[offset1 + j]
                        val blue = texturePixel shr 16 and 0xff
                        val red = texturePixel shl 16 and 0x00ff0000
                        val pixel = texturePixel and -0xff0100 or red or blue
                        bitmapSource[offset2 + j] = pixel
                    }
                }
            } catch (e: GLException) {
                return null
            }
            var bos: BufferedOutputStream? = null

            val file = File(mContext?.filesDir?.absolutePath + "/gl.png")
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            bos = BufferedOutputStream(FileOutputStream(file))
            val bmp = Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos)
            bmp.recycle()
            return bmp
        }
        return null
    }

}