import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.util.FPSAnimator

/**
 * @author Jordan Abraham
 */
class GameCapture(
    window: GLWindow,
    targetFramesPerSecond: Int,
    val game: Game
) {
    val animator = FPSAnimator(window, targetFramesPerSecond, true)
}
