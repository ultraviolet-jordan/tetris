import com.jogamp.opengl.GL
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.GLEventListener
import com.jogamp.opengl.GLProfile
import com.jogamp.opengl.util.awt.TextRenderer
import org.anglur.joglext.jogl2d.GLGraphics2D
import java.awt.Color
import java.awt.Font

/**
 * @author Jordan Abraham
 */
class Tetris2DGraphics(
    private val gameCapture: TetrisGameCapture
) : GLEventListener {

    private val gl = GLGraphics2D()
    private val textRenderer = TextRenderer(Font("Default", Font.BOLD, 15), true, false)

    init {
        GLProfile.initSingleton()
    }

    override fun init(drawable: GLAutoDrawable) {
    }

    override fun dispose(drawable: GLAutoDrawable) {
        gl.glDispose()
    }

    override fun display(drawable: GLAutoDrawable) {
        drawable.gl.gL2.glClear(GL.GL_COLOR_BUFFER_BIT)
        gl.prePaint(drawable.context)
        gl.drawRect(0, 0, drawable.surfaceWidth, drawable.surfaceHeight)
        val width = 25
        val height = 25
        repeat(12) { x ->
            repeat(22) { y ->
                val color = gameCapture.tetris.getColor(x, y)
                gl.color = color
                gl.fillRect((x * 25) + 3, (y * 25) + 3, width - 2, height - 2)
                gl.color = color.brighter()
                gl.fillRect((x * 25), (y * 25), 3, height)
                gl.fillRect((x * 25) + 3, (y * 25), width - 2, 3)
                gl.color = color.darker()
                gl.fillRect((x * 25) + 3, (y * 25) + height - 3, width - 3, 3)
                gl.fillRect((x * 25) + width - 3, (y * 25), 3, height - 3)
            }
        }
        textRenderer.beginRendering(drawable.surfaceWidth, drawable.surfaceHeight)
        textRenderer.setColor(Color.GREEN)
        textRenderer.draw("FPS: ${gameCapture.animator.lastFPS}", 1, drawable.surfaceHeight - 15)
        textRenderer.endRendering()
    }

    override fun reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
    }
}
