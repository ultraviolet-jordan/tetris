import com.jogamp.newt.event.KeyEvent
import com.jogamp.newt.event.KeyListener

/**
 * @author Jordan Abraham
 */
class TetrisGameKeyListener(
    private val gameCapture: TetrisGameCapture
) : KeyListener {

    override fun keyPressed(e: KeyEvent) = gameCapture.tetris.let {
        if (it.playing.not() && e.keyCode != KeyEvent.VK_SPACE) return
        if (it.paused && e.keyCode != KeyEvent.VK_ESCAPE) return
        when (e.keyCode) {
            KeyEvent.VK_LEFT -> it.moveOnXAxis(-1)
            KeyEvent.VK_RIGHT -> it.moveOnXAxis(1)
            KeyEvent.VK_DOWN -> it.rotate(false)
            KeyEvent.VK_UP -> it.rotate(true)
            KeyEvent.VK_SPACE -> if (it.playing) it.moveOnYAxis(1) else it.startNewGame()
            KeyEvent.VK_ESCAPE -> it.paused = !it.paused
        }
    }

    override fun keyReleased(e: KeyEvent) { }
}
