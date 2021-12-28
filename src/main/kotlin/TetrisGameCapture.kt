import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.util.FPSAnimator

/**
 * @author Jordan Abraham
 */
class TetrisGameCapture(
    window: GLWindow,
    targetFramesPerSecond: Int,
    var scale: Int,
    val tetris: Tetris
) { val animator = FPSAnimator(window, targetFramesPerSecond, true) }
