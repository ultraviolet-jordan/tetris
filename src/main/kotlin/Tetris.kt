import java.awt.Color
import kotlin.properties.Delegates

/**
 * @author Jordan Abraham
 */
class Tetris {

    private val board = TetrisBoard()

    private lateinit var tetromino: Tetromino
    private var positionX by Delegates.notNull<Int>()
    private var positionY by Delegates.notNull<Int>()

    var playing = true
    var paused = false
    var score = 0

    init { startNewGame() }

    @Synchronized
    fun startNewGame() {
        // The player can start a new game when one ends, this is why we initialize like this.
        setPosition(5, 0)
        board.reset()
        score = 0
        tetromino = tetrominoes.random()
        paintPoints(tetromino.points)
        paused = false
        // This will ultimately start the game.
        playing = true
    }

    @Synchronized
    fun tick() = tetromino.let {
        if (playing.not() || paused) return

        val x = positionX
        val y = positionY

        // Wipe the board. This is temporary as we repaint the saved tetrominoes next.
        disposeTetromino(x, y)

        // Checks if the current tetromino landed on the closest Y-axis on another saved point.
        if (it.pointsAxis().none { point -> board.collides(point.x + x, point.y + y) } &&
            // Checks if the current tetromino landed on the closest Y-axis on a boundary.
            it.pointsAxis(deltaY = 1).none { point -> board.collides(point.x + x, point.y + y + 1) }
        ) {
            // Move the current tetromino and change the offset.
            paintPoints(it.points.filterViewable().map { point -> Point(point.x, point.y + y + 1) }.toTypedArray())
            setPosition(x, y + 1)
            return
        }

        // Save the tetromino that just landed.
        board.savePoints(it.points, x, y, it.color)

        // The default offset.
        setPosition(5, 0)

        // This is to find the farthest point in the current tetromino to the bottom of the board on the Y-axis.
        // This is much more efficient instead of unnecessarily looping the entire board to check for filled rows.
        val landedYDelta = (y - it.points.minOf { point -> point.y })

        // If this offset is <= 1 this means the tetromino crossed the border at the top when it landed. This means the game is over.
        if (landedYDelta <= 1) {
            playing = false
            paintPoints(it.points)
            return
        }

        val next = tetrominoes.random()
        setTetromino(next)
        // Paint the new tetromino.
        paintPoints(next.points)

        var scoreFromRows = 0
        // Loop from the landed y-axis delta, so we don't have to loop the entire board to check for filled rows.
        (landedYDelta..20).forEach { deltaY ->
            if (board.checkRowFilled(deltaY)) {
                if (scoreFromRows == 0) scoreFromRows = 100 else scoreFromRows *= 2

                board.wipeRow(deltaY)
                board.shiftDown(deltaY)
            }
        }
        score += scoreFromRows
    }

    @Synchronized
    fun moveOnXAxis(deltaX: Int) = tetromino.let {
        val x = positionX
        val y = positionY
        // We have to check both sides of the tetromino on the x-axis since they both can collide depending on user input.
        val counterClockwise = deltaX == -1
        // Check if the tetromino collides on the bottom Y-axis.
        if (it.pointsAxis().any { point -> board.collides(point.x + x, point.y + y + 1) }) return
        // Check if the tetromino collides on the X-axis.
        if (it.pointsAxis(counterClockwise, deltaX = 1).any { point -> board.collides(point.x + (if (counterClockwise) x - 1 else x + 1), point.y + y) }) return
        setPosition(x + deltaX, y)

        disposeTetromino(if (deltaX == -1) x + deltaX + 1 else x + deltaX - 1, y)
        paintPoints(it.points.filterViewable().map { point -> Point(point.x, point.y + y) }.toTypedArray())
    }

    @Synchronized
    fun moveOnYAxis(deltaY: Int) = tetromino.let {
        val x = positionX
        val y = positionY
        // We only have to check collision on the closest point(s) to the bottom y-axis. The user can't move up on the y-axis.
        if (it.pointsAxis(deltaY = 1).none { point -> board.collides(point.x + x, point.y + y + 1) }) {
            setPosition(x, y + deltaY)

            // coerceAtLeast() because a new tetromino starts at the very top.
            disposeTetromino(x, (y + deltaY - 1).coerceAtLeast(0))
            paintPoints(it.points.filterViewable().map { point -> Point(point.x, point.y + y + deltaY) }.toTypedArray())
        }
    }

    @Synchronized
    fun rotate(counterClockwise: Boolean) = tetromino.let {
        // No reason to actually rotate the square tetromino.
        if (it.color == YELLOW) return

        val x = positionX
        val y = positionY

        // Create a new collection because we only need to rotate between all possible variations of the same kind of tetromino.
        // This is based on color since each one is a unique color.
        // The next possible tetromino we can use depending on the rotation type.
        val nextTetromino = with(tetrominoes.filter { tetromino -> it.color == tetromino.color }.toList()) {
            elementAtOrElse(if (counterClockwise) indexOf(it) - 1 else indexOf(it) + 1) { if (counterClockwise) last() else first() }
        }
        // Check for collision on the y-axis.
        if (nextTetromino.pointsAxis(deltaY = 1, otherPoints = it.points).any { point -> board.collides(point.x + x, point.y + y + 1) }) return
        // Check for collision for the next possible tetromino.
        if (nextTetromino.pointsAxis(otherPoints = it.points).none { point -> board.collides(point.x + x, point.y + y) }) {
            // Set the new tetromino and repaint.
            disposeTetromino(x, y)
            setTetromino(nextTetromino)
            paintPoints(nextTetromino.points.filterViewable().map { point -> Point(point.x, point.y + y) }.toTypedArray())
        }
    }

    @Synchronized
    fun getColor(
        x: Int,
        y: Int
    ): Color = board.getColor(x, y)

    private fun setPosition(
        positionX: Int,
        positionY: Int
    ) {
        this.positionX = positionX
        this.positionY = positionY
    }

    private fun paintPoints(points: Array<Point>) {
        // Paint the current tetromino the user has control of.
        board.paintPoints(points, positionX, 0, tetromino.color)
        // Paint the saved points already on the board.
        board.paintSavedPoints()
    }

    private fun disposeTetromino(
        deltaX: Int,
        deltaY: Int
    ) {
        // Doing it like this is much more efficient than looping the whole board to clear the last tetromino.
        board.paintPoints(tetromino.points, deltaX, deltaY, Color.BLACK)
    }

    private fun setTetromino(tetromino: Tetromino) {
        this.tetromino = tetromino
    }

    private companion object {

        private val CYAN = Color(0, 255, 255)
        private val PURPLE = Color(161, 0, 240)
        private val ORANGE = Color(255, 120, 0)
        private val BLUE = Color(0, 0, 172)
        private val YELLOW = Color(255, 255, 0)
        private val GREEN = Color(0, 255, 0)
        private val RED = Color(255, 0, 0)

        // All the tetrominoes that can be used in the game.
        private val tetrominoes = arrayOf(
            Tetromino(CYAN, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1))),
            Tetromino(CYAN, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(1, 3))),
            Tetromino(CYAN, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1))),
            Tetromino(CYAN, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(1, 3))),

            Tetromino(ORANGE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(2, 0))),
            Tetromino(ORANGE, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 2))),
            Tetromino(ORANGE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(0, 2))),
            Tetromino(ORANGE, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(0, 0))),

            Tetromino(BLUE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(2, 2))),
            Tetromino(BLUE, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(0, 2))),
            Tetromino(BLUE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(0, 0))),
            Tetromino(BLUE, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 0))),

            Tetromino(YELLOW, arrayOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1))),
            Tetromino(YELLOW, arrayOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1))),
            Tetromino(YELLOW, arrayOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1))),
            Tetromino(YELLOW, arrayOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1))),

            Tetromino(GREEN, arrayOf(Point(1, 0), Point(2, 0), Point(0, 1), Point(1, 1))),
            Tetromino(GREEN, arrayOf(Point(0, 0), Point(0, 1), Point(1, 1), Point(1, 2))),
            Tetromino(GREEN, arrayOf(Point(1, 0), Point(2, 0), Point(0, 1), Point(1, 1))),
            Tetromino(GREEN, arrayOf(Point(0, 0), Point(0, 1), Point(1, 1), Point(1, 2))),

            Tetromino(PURPLE, arrayOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(2, 1))),
            Tetromino(PURPLE, arrayOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(1, 2))),
            Tetromino(PURPLE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 2))),
            Tetromino(PURPLE, arrayOf(Point(1, 0), Point(1, 1), Point(2, 1), Point(1, 2))),

            Tetromino(RED, arrayOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(2, 1))),
            Tetromino(RED, arrayOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(0, 2))),
            Tetromino(RED, arrayOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(2, 1))),
            Tetromino(RED, arrayOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(0, 2)))
        )
    }
}
