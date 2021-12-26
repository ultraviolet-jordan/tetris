import com.jogamp.newt.event.KeyEvent
import com.jogamp.newt.event.KeyListener

/**
 * @author Jordan Abraham
 */
class TetrisGameKeyListener(
    private val gameCapture: TetrisGameCapture
) : KeyListener {

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_LEFT -> gameCapture.tetris.moveOnXAxis(-1)
            KeyEvent.VK_RIGHT -> gameCapture.tetris.moveOnXAxis(1)
            KeyEvent.VK_DOWN -> gameCapture.tetris.moveOnYAxis(1)
        }
    }

    override fun keyReleased(e: KeyEvent) {
    }
}
