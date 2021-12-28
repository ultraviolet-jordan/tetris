import com.jogamp.newt.event.KeyEvent
import com.jogamp.newt.event.KeyListener

/**
 * @author Jordan Abraham
 */
class TetrisGameKeyListener(
    private val gameCapture: TetrisGameCapture
) : KeyListener {

    override fun keyPressed(e: KeyEvent) {
        if (gameCapture.tetris.playing.not() && e.keyCode != KeyEvent.VK_SPACE) return
        when (e.keyCode) {
            KeyEvent.VK_LEFT -> gameCapture.tetris.moveOnXAxis(-1)
            KeyEvent.VK_RIGHT -> gameCapture.tetris.moveOnXAxis(1)
            KeyEvent.VK_DOWN -> gameCapture.tetris.rotate(false)
            KeyEvent.VK_UP -> gameCapture.tetris.rotate(true)
            KeyEvent.VK_SPACE -> if (gameCapture.tetris.playing) gameCapture.tetris.moveOnYAxis(1) else gameCapture.tetris.startNewGame()
        }
    }

    override fun keyReleased(e: KeyEvent) {
    }
}
