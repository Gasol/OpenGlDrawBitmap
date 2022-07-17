package tw.gasol.android.opengldrawbitmap

import android.util.Size
import javax.microedition.khronos.opengles.GL10

interface Drawer {
    fun draw(gl: GL10, canvasSize: Size)
}