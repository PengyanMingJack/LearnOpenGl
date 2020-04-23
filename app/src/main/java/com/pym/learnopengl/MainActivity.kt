package com.pym.learnopengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    var mRenderer: MyRenderer? = null
    var list = Arrays.asList(
        R.mipmap.kaihe_00000,
        R.mipmap.kaihe_00000,
        R.mipmap.kaihe_00001,
        R.mipmap.kaihe_00002,
        R.mipmap.kaihe_00003,
        R.mipmap.kaihe_00004,
        R.mipmap.kaihe_00005,
        R.mipmap.kaihe_00006,
        R.mipmap.kaihe_00007,
        R.mipmap.kaihe_00008,
        R.mipmap.kaihe_00009,
        R.mipmap.kaihe_00010,
        R.mipmap.kaihe_00011,
        R.mipmap.kaihe_00012,
        R.mipmap.kaihe_00013,
        R.mipmap.kaihe_00014,
        R.mipmap.kaihe_00015,
        R.mipmap.kaihe_00016,
        R.mipmap.kaihe_00017,
        R.mipmap.kaihe_00018
        , R.mipmap.kaihe_00019
    )
    var i: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mGLSurfaceView.setEGLContextClientVersion(2)
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        mRenderer = MyRenderer(this)
        mGLSurfaceView.setRenderer(mRenderer)
        mGLSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        getImage(0)
    }

    fun getImage(index: Int): Int {
        Handler().postDelayed(Runnable {
            i++
            mRenderer?.setPic(getImage(i))
            mGLSurfaceView?.requestRender()
        }, 60)
        if (index >= list.size - 1) {
            return list[list.size - 1]
        } else {
            return list[index]
        }
    }

    override fun onPause() {
        super.onPause()
        mGLSurfaceView?.onPause()

    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRenderer?.destroy()
    }
}
