import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.GLCapabilities
import kotlin.time.measureTime

/**
 * @author Jordan Abraham
 */
class TetrisGameWindow(
    tetris: Tetris
) {

    private val window = GLWindow.create(GLCapabilities(null))
    private val gameCapture = TetrisGameCapture(window, 60, tetris)

    init {
        window.addKeyListener(TetrisGameKeyListener(gameCapture))
        window.addWindowListener(TetrisWindowListener(gameCapture))
        window.addGLEventListener(Tetris2DGraphics(gameCapture))

        window.setSize(12 * 25, 22 * 25)
        window.title = "Tetris"
        window.isResizable = false
        window.isVisible = true

        // Start the animator after the user can see game.
        gameCapture.animator.setUpdateFPSFrames(3, null)
        gameCapture.animator.start()

        do {
            val time = measureTime { gameCapture.tetris.tick() }
            Thread.sleep(1000)
            println("Loop took $time to complete.")
        } while (!Thread.interrupted())
    }
}
