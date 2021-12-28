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

    private val graphics = GLGraphics2D()

    init { GLProfile.initSingleton() }

    override fun init(drawable: GLAutoDrawable) {
    }

    override fun dispose(drawable: GLAutoDrawable) {
        graphics.glDispose()
    }

    override fun display(drawable: GLAutoDrawable) {
        // Set up the renderers here because the game scale can change.
        val fpsRenderer = TextRenderer(Font("Default", Font.BOLD, gameCapture.scale / 2), true, true)
        val scoreRenderer = TextRenderer(Font("Default", Font.BOLD, (gameCapture.scale / 2) + 8), true, true)
        val gameOverRenderer = TextRenderer(Font("Default", Font.BOLD, gameCapture.scale / 2), true, true)

        val scale = gameCapture.scale
        val half = scale / 2
        val quarter = scale / 4
        val eighth = scale / 8

        drawable.gl.gL2.glClear(GL.GL_COLOR_BUFFER_BIT)
        graphics.prePaint(drawable.context)

        repeat(12) { x ->
            repeat(22) { y ->
                val color = if (gameCapture.tetris.playing) gameCapture.tetris.getColor(x, y) else gameCapture.tetris.getColor(x, y).darker().darker()
                val widthX = x * scale
                val widthY = y * scale
                val widthScaleX = widthX + eighth
                val widthScaleY = widthY + eighth

                graphics.color = color
                graphics.fillRect(widthScaleX, widthScaleY, scale - 2, scale - 2)
                graphics.color = color.brighter()
                graphics.fillRect(widthX, widthY, eighth, scale)
                graphics.fillRect(widthScaleX, widthY, scale - 2, eighth)
                graphics.color = color.darker()
                graphics.fillRect(widthScaleX, widthY + scale - eighth, scale - eighth, eighth)
                graphics.fillRect(widthX + scale - eighth, widthY, eighth, scale - eighth)
            }
        }
        // The score backdrop.
        graphics.color = Color.BLACK
        graphics.fillRect(3, (scale * 21) + eighth, scale * 6 - 4, scale - quarter)

        scoreRenderer.beginRendering(drawable.surfaceWidth, drawable.surfaceHeight)
        scoreRenderer.setColor(Color.WHITE)
        scoreRenderer.draw("SCORE: ${gameCapture.tetris.score}", 5, drawable.surfaceHeight - (((scale * 21) + scale - eighth)) + eighth)
        scoreRenderer.endRendering()

        fpsRenderer.beginRendering(drawable.surfaceWidth, drawable.surfaceHeight)
        fpsRenderer.setColor(Color.GREEN)
        fpsRenderer.draw("FPS: ${gameCapture.animator.lastFPS}", 1, drawable.surfaceHeight - scale + (half + eighth))
        fpsRenderer.endRendering()

        if (gameCapture.tetris.playing.not()) {
            gameOverRenderer.beginRendering(drawable.surfaceWidth, drawable.surfaceHeight)
            gameOverRenderer.setColor(Color.WHITE)
            gameOverRenderer.draw("GAME OVER", ((scale * 6) - (half * 3) - quarter) + gameCapture.scale / 3, drawable.surfaceHeight / 2)
            gameOverRenderer.endRendering()
        }
    }

    override fun reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
        gameCapture.scale = height / 22
    }
}
