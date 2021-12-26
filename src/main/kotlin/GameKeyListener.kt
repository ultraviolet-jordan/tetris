import com.jogamp.newt.event.KeyEvent
import com.jogamp.newt.event.KeyListener

/**
 * @author Jordan Abraham
 */
class GameKeyListener(
    private val gameCapture: GameCapture
) : KeyListener {

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_LEFT -> gameCapture.game.moveOnXAxis(-1)
            KeyEvent.VK_RIGHT -> gameCapture.game.moveOnXAxis(1)
            KeyEvent.VK_DOWN -> gameCapture.game.moveOnYAxis(1)
        }
    }

    override fun keyReleased(e: KeyEvent) {
    }
}
