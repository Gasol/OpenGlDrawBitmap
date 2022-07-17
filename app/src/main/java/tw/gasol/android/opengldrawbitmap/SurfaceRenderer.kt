package tw.gasol.android.opengldrawbitmap

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Size
import tw.gasol.android.grafika.gles.GlUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SurfaceRenderer(private val bitmap: Bitmap) : GLSurfaceView.Renderer {

    private val bitmapDrawer = BitmapDrawer()

    private var canvasSize: Size? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) = Unit

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        gl?.apply {
            glViewport(0, 0, width, height)
            canvasSize = Size(width, height)
            bitmapDrawer.apply {
                init()
                setBitmap(bitmap, false)
                setScale(bitmap.width * 3f, bitmap.height * 3f)
                setPosition(width / 2f, height / 2f)
            }
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        gl?.apply {
            glClearColor(0.2f, 0.2f, 0.2f, 0.0f)
            glClear(GLES20.GL_COLOR_BUFFER_BIT)
            bitmapDrawer.draw(gl, canvasSize!!)
            drawBox(gl)
        }
    }

    private fun drawBox(gl: GL10) {
        gl.use(GLES20.GL_SCISSOR_TEST) {
            glScissor(0, 0, 100, 100)
            glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
            glClear(GLES20.GL_COLOR_BUFFER_BIT)
        }
    }
}

fun GL10.use(feature: Int, block: GL10.() -> Unit) {
    glEnable(feature)
    GlUtil.checkGlError("glEnable $feature")
    block()
    glDisable(feature)
    GlUtil.checkGlError("glDisable $feature")
}