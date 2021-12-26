import com.jogamp.newt.event.WindowAdapter
import com.jogamp.newt.event.WindowEvent
import kotlin.system.exitProcess

/**
 * @author Jordan Abraham
 */
class GameWindowListener(
    private val gameCapture: GameCapture
) : WindowAdapter() {
    override fun windowDestroyNotify(e: WindowEvent) {
        gameCapture.animator.stop()
        exitProcess(0)
    }
}
