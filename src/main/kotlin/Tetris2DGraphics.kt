import Constants.COLS
import Constants.ROWS
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

    private lateinit var scoreRenderer: TextRenderer
    private lateinit var fpsRenderer: TextRenderer
    private lateinit var gameOverRenderer: TextRenderer
    private lateinit var pauseMenuRenderer: TextRenderer

    init {
        GLProfile.initSingleton()
        attachRenderers()
    }

    override fun init(drawable: GLAutoDrawable) {}
    override fun dispose(drawable: GLAutoDrawable) = graphics.glDispose()

    override fun display(drawable: GLAutoDrawable) {
        val scale = gameCapture.scale
        val half = scale / 2
        val quarter = scale / 4
        val eighth = scale / 8

        drawable.gl.gL2.glClear(GL.GL_COLOR_BUFFER_BIT)
        graphics.prePaint(drawable.context)

        val width = drawable.surfaceWidth
        val height = drawable.surfaceHeight
        // Find the center of the game relative to the game window.
        val deltaX = (width - COLS * scale) / 2
        val deltaY = (height - ROWS * scale) / 2

        // Draw the grid and stylize.
        repeat(COLS) { x ->
            repeat(ROWS) { y ->
                val color = gameCapture.tetris.let {
                    val playing = it.playing && !it.paused
                    val backdrop = Color(128, 124, 124)
                    with(if (playing) it.getColor(x, y) else it.getColor(x, y).darker().darker()) {
                        if (x == 0 || x == 11 || y == 0 || y == 21 && this != backdrop) {
                            return@with if (playing) backdrop else backdrop.darker().darker()
                        }
                        this
                    }
                }
                val widthX = deltaX + (x * scale)
                val widthY = deltaY + (y * scale)
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
        // Draw the score backdrop.
        graphics.color = Color.BLACK
        graphics.fillRect(deltaX + 3, deltaY + ((scale * (ROWS - 1)) + eighth), scale * 6 - 4, scale - quarter)

        // Draw the score.
        scoreRenderer.let {
            it.beginRendering(width, height)
            it.setColor(Color.WHITE)
            it.draw("SCORE: ${gameCapture.tetris.score}", deltaX + 5, (height - (((scale * (ROWS - 1)) + scale - eighth)) + eighth) - deltaY)
            it.endRendering()
        }

        // Draw the game FPS.
        fpsRenderer.let {
            it.beginRendering(width, height)
            it.setColor(Color.GREEN)
            it.draw("FPS: ${gameCapture.animator.lastFPS}", deltaX + 1, (height - scale + (half + eighth)) - deltaY)
            it.endRendering()
        }

        if (gameCapture.tetris.playing.not()) {
            // Draw the game over overlay.
            gameOverRenderer.let {
                it.beginRendering(width, height)
                it.setColor(Color.WHITE)
                it.draw("GAME OVER", (width / 2) - graphics.stringHelper.getFontMetrics(it.font).stringWidth("GAME OVER") / 2, height / 2)
                it.endRendering()
            }
        } else {
            if (gameCapture.tetris.paused) {
                // Draw the pause menu.
                pauseMenuRenderer.let {
                    it.beginRendering(width, height)
                    it.setColor(Color.WHITE)
                    it.draw("PAUSED", (width / 2) - graphics.stringHelper.getFontMetrics(it.font).stringWidth("PAUSED") / 2, height / 2)
                    it.endRendering()
                }
            }
        }
        graphics.postPaint()
    }

    override fun reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
        // Limit how small the game can look.
        if (height / ROWS < 15) return
        gameCapture.scale = height / ROWS
        // Dispose of the current renderers and update them due to a possible change in scaling.
        disposeRenderers()
        attachRenderers()
    }

    private fun attachRenderers() {
        scoreRenderer = TextRenderer(Font("Default", Font.BOLD, gameCapture.scale / 2 + 6), true, true)
        fpsRenderer = TextRenderer(Font("Default", Font.BOLD, gameCapture.scale / 2), true, true)
        gameOverRenderer = TextRenderer(Font("Default", Font.BOLD, gameCapture.scale / 2), true, true)
        pauseMenuRenderer = TextRenderer(Font("Default", Font.BOLD, gameCapture.scale / 2), true, true)
    }

    private fun disposeRenderers() {
        scoreRenderer.dispose()
        fpsRenderer.dispose()
        gameOverRenderer.dispose()
        pauseMenuRenderer.dispose()
    }
}
