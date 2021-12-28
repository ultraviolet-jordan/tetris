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
    private val fpsRenderer = TextRenderer(Font("Default", Font.BOLD, gameCapture.scale / 2), true, true)
    private val scoreRenderer = TextRenderer(Font("Default", Font.BOLD, gameCapture.scale / 2), true, true)
    private val gameOverRenderer = TextRenderer(Font("Default", Font.BOLD, gameCapture.scale / 2), true, true)

    init { GLProfile.initSingleton() }

    override fun init(drawable: GLAutoDrawable) {
    }

    override fun dispose(drawable: GLAutoDrawable) {
        gl.glDispose()
    }

    override fun display(drawable: GLAutoDrawable) {
        drawable.gl.gL2.glClear(GL.GL_COLOR_BUFFER_BIT)
        gl.prePaint(drawable.context)
        val height = drawable.surfaceHeight / 22
        val scale = drawable.surfaceHeight / 22 / 8
        repeat(12) { x ->
            repeat(22) { y ->
                val color = if (gameCapture.tetris.playing) gameCapture.tetris.getColor(x, y) else gameCapture.tetris.getColor(x, y).darker().darker()
                gl.color = color
                gl.fillRect((x * height) + scale, (y * height) + scale, height - 2, height - 2)
                gl.color = color.brighter()
                gl.fillRect((x * height), (y * height), scale, height)
                gl.fillRect((x * height) + scale, (y * height), height - 2, scale)
                gl.color = color.darker()
                gl.fillRect((x * height) + scale, (y * height) + height - scale, height - scale, scale)
                gl.fillRect((x * height) + height - scale, (y * height), scale, height - scale)
            }
        }
        // The score backdrop.
        gl.color = Color.BLACK
        gl.fillRect(3, (height * 21) + (height / 6), height * 6 - 4, height - (height / 4))

        scoreRenderer.beginRendering(drawable.surfaceWidth, drawable.surfaceHeight)
        scoreRenderer.setColor(Color.WHITE)
        scoreRenderer.draw("Score: ${gameCapture.tetris.score}", 5, drawable.surfaceHeight - (((height * 21) + height - (height / 6))) + (height / 8))
        scoreRenderer.endRendering()

        fpsRenderer.beginRendering(drawable.surfaceWidth, drawable.surfaceHeight)
        fpsRenderer.setColor(Color.GREEN)
        fpsRenderer.draw("FPS: ${gameCapture.animator.lastFPS}", 1, drawable.surfaceHeight - 15)
        fpsRenderer.endRendering()

        if (gameCapture.tetris.playing.not()) {
            gameOverRenderer.beginRendering(drawable.surfaceWidth, drawable.surfaceHeight)
            gameOverRenderer.setColor(Color.WHITE)
            gameOverRenderer.draw("Game Over", ((height * 6) - (20 * 3)) + gameCapture.scale / 3, drawable.surfaceHeight / 2)
            gameOverRenderer.endRendering()
        }
    }

    override fun reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
    }
}
