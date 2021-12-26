import com.jogamp.newt.event.KeyEvent
import com.jogamp.newt.event.KeyListener
import com.jogamp.newt.event.WindowAdapter
import com.jogamp.newt.event.WindowEvent
import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.*
import com.jogamp.opengl.util.FPSAnimator
import com.jogamp.opengl.util.awt.TextRenderer
import org.anglur.joglext.jogl2d.GLGraphics2D
import java.awt.Color
import java.awt.Font
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

/**
 * @author Jordan Abraham
 */
class Tetris : GLEventListener {

    data class Point(
        val x: Int,
        val y: Int
    )

    data class Tetromino(
        val color: Color,
        val points: Array<Point>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Tetromino

            if (color != other.color) return false
            if (!points.contentEquals(other.points)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = color.hashCode()
            result = 31 * result + points.contentHashCode()
            return result
        }
    }

    private val gameRenderer = GLGraphics2D()
    private val window = GLWindow.create(GLCapabilities(null))
    private val animator = FPSAnimator(window, 60, true)
    private val gameTextRenderer = TextRenderer(Font("Default", Font.BOLD, 15), true, false)

    private val board = Array(12) { x ->
        Array(21) { y ->
            if (x == 0 || x == 11 || y == 20) Color(128, 124, 124) else Color.BLACK
        }
    }

    private val tetrominoes = arrayOf(
        Tetromino(Color.CYAN, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1))),
        Tetromino(Color.CYAN, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(1, 3))),
        Tetromino(Color.CYAN, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1))),
        Tetromino(Color.CYAN, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(1, 3))),

        Tetromino(Color.ORANGE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(2, 0))),
        Tetromino(Color.ORANGE, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 2))),
        Tetromino(Color.ORANGE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(0, 2))),
        Tetromino(Color.ORANGE, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(0, 0))),
    )

    private val saved = mutableMapOf<Point, Color>()
    private var currentTetromino = tetrominoes.random()
    private var offsetY = 0
    private var offsetX = 5

    init {
        GLProfile.initSingleton()
    }

    fun open() {
        window.addKeyListener(object : KeyListener {

            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_LEFT -> moveOnXAxis(-1)
                    KeyEvent.VK_RIGHT -> moveOnXAxis(1)
                    KeyEvent.VK_DOWN -> moveOnYAxis(1)
                }
            }

            override fun keyReleased(e: KeyEvent) {}
        })

        window.addWindowListener(object : WindowAdapter() {
            override fun windowDestroyNotify(e: WindowEvent) {
                animator.stop()
                exitProcess(0)
            }
        })

        window.addGLEventListener(this)
        window.setSize(12 * 25, 21 * 25)
        window.title = "Tetris"
        window.isResizable = false
        window.isVisible = true

        // Start the animator after the user can see game.
        animator.start()

        do {
            val time = measureTime {
                tick()
            }
            Thread.sleep(1000)
            println("Loop took $time to complete.")
        } while (!Thread.interrupted())
    }

    private fun paintSaved() = saved.forEach { board[it.key.x][it.key.y] = it.value }

    private fun paintPoints(points: Array<Point>) {
        // Paint the current points in the loop.
        points.forEach { board[it.x + offsetX][it.y] = currentTetromino.color }
        // Paint the saved points already on the board.
        paintSaved()
    }

    private fun disposeTetromino(offsetX: Int, offsetY: Int) {
        // Doing it like this is much more efficient than looping the whole board to clear the last tetromino.
        currentTetromino.points.forEach {
            board[it.x + offsetX][it.y + offsetY] = Color.BLACK
        }
    }

    private fun tick() {
        // Wipe the board. This is temporary as we repaint the saved tetrominoes next.
        disposeTetromino(offsetX, offsetY)
        // Paint the saved tetrominoes.
        paintSaved()

        if (leadingYPoints().any { collides(it.x + offsetX, it.y + offsetY + 1) }) {
            currentTetromino.points.forEach {
                saved[Point(it.x + offsetX, it.y + offsetY)] = currentTetromino.color
            }
            offsetY = 0
            offsetX = 5

            currentTetromino = tetrominoes.random()
            paintPoints(currentTetromino.points)
            return
        }

        paintPoints(currentTetromino.points.map { Point(it.x, it.y + offsetY + 1) }.toTypedArray())
        offsetY++
    }

    private fun collides(x: Int, y: Int): Boolean = board[x][y] != Color.BLACK
    private fun leadingYPoints(): List<Point> = currentTetromino.points.filter { it.y == currentTetromino.points.maxOf { p -> p.y } }
    private fun trailingXPoints(): List<Point> = currentTetromino.points.filter { it.x == currentTetromino.points.minOf { p -> p.x } }
    private fun leadingXPoints(): List<Point> = currentTetromino.points.filter { it.x == currentTetromino.points.maxOf { p -> p.x } }

    fun moveOnXAxis(x: Int) = with(if (x == -1) trailingXPoints() else leadingXPoints()) {
        // We have to check both sides of the tetromino on the x-axis since they both can collide depending on user input.
        // Check if the tetromino collides on the bottom Y-axis.
        if (any { collides(it.x + offsetX + x, it.y + offsetY + 1) }) return
        // Check if the tetromino collides on the X-axis.
        if (any { collides(it.x + offsetX + x, it.y) }) return

        offsetX += x

        disposeTetromino(if (x == -1) offsetX + 1 else offsetX - 1, offsetY)
        paintPoints(currentTetromino.points.map { Point(it.x, it.y + offsetY) }.toTypedArray())
    }

    fun moveOnYAxis(y: Int) {
        // We only have to check collision on the closest point(s) to the bottom y-axis. The user can't move up on the y-axis.
        if (leadingYPoints().any { collides(it.x + offsetX, it.y + y + offsetY) }) return

        offsetY += y

        disposeTetromino(offsetX, (offsetY - 1).coerceAtLeast(0))
        paintPoints(currentTetromino.points.map { Point(it.x, it.y + offsetY) }.toTypedArray())
    }

    override fun init(drawable: GLAutoDrawable) {
    }

    override fun dispose(drawable: GLAutoDrawable) {
        gameRenderer.glDispose()
    }

    override fun display(drawable: GLAutoDrawable) {
        val gl2 = drawable.gl.gL2

        gl2.glClear(GL.GL_COLOR_BUFFER_BIT)
        gameRenderer.prePaint(drawable.context)
        gameRenderer.drawRect(0, 0, drawable.surfaceWidth, drawable.surfaceHeight)
        (0 until 12).forEach { x ->
            (0 until 21).forEach { y ->
                gameRenderer.color = board[x][y]
                gameRenderer.fill3DRect(x * 25, y * 25, 25, 25, true)
            }
        }
        gameTextRenderer.beginRendering(drawable.surfaceWidth, drawable.surfaceHeight)
        gameTextRenderer.setColor(Color.GREEN)
        gameTextRenderer.draw("${animator.fps}", 1, drawable.surfaceHeight - 15)
        gameTextRenderer.endRendering()
    }

    override fun reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
    }
}
