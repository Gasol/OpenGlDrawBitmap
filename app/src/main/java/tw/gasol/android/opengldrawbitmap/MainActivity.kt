package tw.gasol.android.opengldrawbitmap

import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val surfaceView = findViewById<GLSurfaceView>(R.id.surfaceView)

        val opts = BitmapFactory.Options().apply { inScaled = false }
        val bitmap =
            BitmapFactory.decodeResource(resources, R.drawable.android_symbol_green_rgb, opts)
        val renderer = SurfaceRenderer(bitmap)

        surfaceView.apply {
            setEGLContextClientVersion(2)
            debugFlags = GLSurfaceView.DEBUG_LOG_GL_CALLS or GLSurfaceView.DEBUG_CHECK_GL_ERROR
            setRenderer(renderer)
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }
    }
}