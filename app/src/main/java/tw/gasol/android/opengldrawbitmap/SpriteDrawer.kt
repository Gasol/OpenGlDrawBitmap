package tw.gasol.android.opengldrawbitmap

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Size
import tw.gasol.android.grafika.gles.Drawable2d
import tw.gasol.android.grafika.gles.GlUtil
import tw.gasol.android.grafika.gles.Sprite2d
import tw.gasol.android.grafika.gles.Texture2dProgram
import javax.microedition.khronos.opengles.GL10

class SpriteDrawer(private var bitmap: Bitmap? = null) : Drawer {
    private val projectionMatrix = FloatArray(16)
    private var canvasSize: Size? = null

    private var sprite2d: Sprite2d? = null
    private var texProgram: Texture2dProgram? = null
    private var texName: Int? = null

    private var isInitialized = false
    private var updateTexture = false
    private var recycleBitmap = false


    fun init() {
        texProgram = Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D)
        texName = texProgram!!.createTextureObject()

        val drawable2d = Drawable2d(Drawable2d.Prefab.RECTANGLE)
        sprite2d = Sprite2d(drawable2d).apply {
            setTexture(texName!!)
        }
        isInitialized = true
    }

    fun setScale(scaleX: Float, scaleY: Float) {
        sprite2d?.setScale(scaleX, scaleY)
    }

    fun setPosition(x: Float, y: Float) {
        sprite2d?.setPosition(x, y)
    }

    fun setRotation(angle: Float) {
        sprite2d?.rotation = angle
    }

    fun setBitmap(bitmap: Bitmap, recycle: Boolean = false) {
        this.bitmap = bitmap
        updateTexture = true
        this.recycleBitmap = recycle
    }

    private fun loadTexture(gl: GL10, bitmap: Bitmap) {
        gl.apply {
            glActiveTexture(GLES20.GL_TEXTURE0)
            glBindTexture(GLES20.GL_TEXTURE_2D, texName!!)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
    }

    override fun draw(gl: GL10, canvasSize: Size) {
        check(isInitialized) { "Square is not initialized" }
        if (bitmap != null && updateTexture) {
            loadTexture(gl, bitmap!!)
            if (recycleBitmap) {
                bitmap!!.recycle()
                bitmap = null
            }
            updateTexture = false
        }
        if (this.canvasSize != canvasSize) {
            this.canvasSize = canvasSize
            Matrix.orthoM(
                projectionMatrix,
                0,
                0f,
                canvasSize.width.toFloat(),
                0f,
                canvasSize.height.toFloat(),
                -1f,
                1f
            )
        }

        drawBitmap(gl, projectionMatrix)
    }

    private fun drawBitmap(gl: GL10, projection: FloatArray) {
        GlUtil.checkGlError("drawBitmap start")
        gl.use(GLES20.GL_BLEND) {
            glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
            sprite2d!!.draw(texProgram, projection)
        }
        GlUtil.checkGlError("drawBitmap end")
    }
}