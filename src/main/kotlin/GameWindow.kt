import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.GLCapabilities
import kotlin.time.measureTime

/**
 * @author Jordan Abraham
 */
class GameWindow(
    game: Game
) {

    private val window = GLWindow.create(GLCapabilities(null))
    private val gameCapture = GameCapture(window, 60, game)

    init {
        window.addKeyListener(GameKeyListener(gameCapture))
        window.addWindowListener(GameWindowListener(gameCapture))
        window.addGLEventListener(Tetris2DGraphics(gameCapture))

        window.setSize(12 * 25, 21 * 25)
        window.title = "Tetris"
        window.isResizable = false
        window.isVisible = true

        // Start the animator after the user can see game.
        gameCapture.animator.start()

        do {
            val time = measureTime {
                gameCapture.game.tick()
            }
            Thread.sleep(1000)
            println("Loop took $time to complete.")
        } while (!Thread.interrupted())
    }
}
