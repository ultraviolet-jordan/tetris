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
    private val gameCapture: GameCapture
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
        (0 until 12).forEach { x ->
            (0 until 21).forEach { y ->
                gl.color = gameCapture.game.getColor(x, y)
                gl.fill3DRect(x * 25, y * 25, 25, 25, true)
            }
        }
        textRenderer.beginRendering(drawable.surfaceWidth, drawable.surfaceHeight)
        textRenderer.setColor(Color.GREEN)
        textRenderer.draw("${gameCapture.animator.fps}", 1, drawable.surfaceHeight - 15)
        textRenderer.endRendering()
    }

    override fun reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
    }
}
