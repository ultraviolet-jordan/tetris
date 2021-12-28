import Constants.COLS
import Constants.ROWS
import Constants.TITLE
import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.GLCapabilities
import java.awt.Toolkit
import kotlin.time.measureTime

/**
 * @author Jordan Abraham
 */
class TetrisGameWindow(
    tetris: Tetris
) {
    private val window = GLWindow.create(GLCapabilities(null))
    private val gameCapture = TetrisGameCapture(window, 144, 35, tetris)

    init {
        window.addKeyListener(TetrisGameKeyListener(gameCapture))
        window.addWindowListener(TetrisWindowListener(gameCapture))
        window.addGLEventListener(Tetris2DGraphics(gameCapture))

        // Game scaling is supported since Tetris uses squares.
        val width = COLS * gameCapture.scale
        val height = ROWS * gameCapture.scale
        window.setSize(width, height)
        // Center window to monitor screen.
        val screen = Toolkit.getDefaultToolkit().screenSize
        val deltaX = (screen.width - width) / 2
        val deltaY = (screen.height - height) / 2
        window.setPosition(deltaX, deltaY)

        window.title = TITLE
        window.isVisible = true

        // Start the animator after the user can see game.
        gameCapture.animator.setUpdateFPSFrames(5, null)
        gameCapture.animator.start()

        do {
            val time = measureTime { gameCapture.tetris.tick() }
            Thread.sleep(1000)
            println("Loop took $time to complete.")
        } while (!Thread.interrupted())
    }
}
