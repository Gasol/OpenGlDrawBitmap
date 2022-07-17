package tw.gasol.android.opengldrawbitmap

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import tw.gasol.android.grafika.gles.Drawable2d
import tw.gasol.android.grafika.gles.GlUtil
import tw.gasol.android.grafika.gles.Sprite2d
import tw.gasol.android.grafika.gles.Texture2dProgram
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SurfaceRenderer(private val bitmap: Bitmap) : GLSurfaceView.Renderer {

    private val displayMatrix = FloatArray(16)
    private var sprite2d: Sprite2d? = null
    private var texProgram: Texture2dProgram? = null
    private var texName: Int? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) = Unit

    private fun loadTexture(gl: GL10, bitmap: Bitmap) {
        gl.apply {
            glActiveTexture(GLES20.GL_TEXTURE0)
            glBindTexture(GLES20.GL_TEXTURE_2D, texName!!)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        gl?.apply {
            glViewport(0, 0, width, height)
            Matrix.orthoM(displayMatrix, 0, 0f, width.toFloat(), 0f, height.toFloat(), -1f, 1f)

            texProgram = Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D)
            texName = texProgram!!.createTextureObject()
            loadTexture(gl, bitmap)

            val drawable2d = Drawable2d(Drawable2d.Prefab.RECTANGLE)
            sprite2d = Sprite2d(drawable2d).apply {
                setTexture(texName!!)
                setScale(bitmap.width * 3f, bitmap.height * 3f)
                setPosition(width / 2f, height / 2f)
            }
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        gl?.apply {
            glClearColor(0.2f, 0.2f, 0.2f, 0.0f)
            glClear(GLES20.GL_COLOR_BUFFER_BIT)
            drawBox(gl)
            drawBitmap(gl)
        }
    }

    private fun drawBitmap(gl: GL10) {
        GlUtil.checkGlError("drawBitmap start")
        gl.use(GLES20.GL_BLEND) {
            glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
            sprite2d!!.draw(texProgram, displayMatrix)
        }
        GlUtil.checkGlError("drawBitmap end")
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