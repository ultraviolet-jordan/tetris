import java.awt.Color

/**
 * @author Jordan Abraham
 */
class Tetris {

    private val board = TetrisBoard()

    private var tetromino = tetrominoes.random()
    private var offsetX = 0
    private var offsetY = 0

    init {
        setOffset(5, 0)
    }

    @Synchronized
    fun tick() = tetromino.let {
        val x = offsetX
        val y = offsetY

        // Wipe the board. This is temporary as we repaint the saved tetrominoes next.
        disposeTetromino(x, y)

        // Checks if the current tetromino landed on the closest Y-axis.
        if (it.pointsAxis(deltaY = 1).none { point -> board.collides(point.x + x, point.y + y + 1) }) {
            // Move the current tetromino and change the offset.
            paintPoints(it.points.map { point -> Point(point.x, point.y + y + 1) }.toTypedArray())
            setOffset(x, y + 1)
            return
        }
        // Save the tetromino that just landed.
        board.savePoints(it.points, x, y, it.color)

        // The default offset.
        setOffset(5, 0)

        val next = tetrominoes.random()
        setTetromino(next)
        // Paint the new tetromino.
        paintPoints(next.points)

        (1 until 21).forEach { deltaY ->
            if (board.checkRowFilled(deltaY)) {
                board.wipeRow(deltaY)
                board.shiftDown(deltaY)
            }
        }
    }

    private fun setOffset(x: Int, y: Int) {
        offsetX = x
        offsetY = y
    }

    private fun paintPoints(points: Array<Point>) {
        // Paint the current tetromino the user has control of.
        board.paintPoints(points, offsetX, 0, tetromino.color)
        // Paint the saved points already on the board.
        board.paintSavedPoints()
    }

    private fun disposeTetromino(deltaX: Int, deltaY: Int) {
        // Doing it like this is much more efficient than looping the whole board to clear the last tetromino.
        board.paintPoints(tetromino.points, deltaX, deltaY, Color.BLACK)
    }

    private fun setTetromino(tetromino: Tetromino) {
        this.tetromino = tetromino
    }

    @Synchronized
    fun moveOnXAxis(deltaX: Int) = tetromino.let {
        val x = offsetX
        val y = offsetY
        // We have to check both sides of the tetromino on the x-axis since they both can collide depending on user input.
        val counterClockwise = deltaX == -1
        // Check if the tetromino collides on the bottom Y-axis.
        if (it.pointsAxis().any { point -> board.collides(point.x + x, point.y + y + 1) }) return
        // Check if the tetromino collides on the X-axis.
        if (it.pointsAxis(counterClockwise, deltaX = 1).any { point -> board.collides(point.x + (if (counterClockwise) x - 1 else x + 1), point.y + y) }) return
        setOffset(x + deltaX, y)

        disposeTetromino(if (deltaX == -1) x + deltaX + 1 else x + deltaX - 1, y)
        paintPoints(it.points.map { point -> Point(point.x, point.y + y) }.toTypedArray())
    }

    @Synchronized
    fun moveOnYAxis(deltaY: Int) = tetromino.let {
        val x = offsetX
        val y = offsetY
        // We only have to check collision on the closest point(s) to the bottom y-axis. The user can't move up on the y-axis.
        if (it.pointsAxis(deltaY = 1).none { point -> board.collides(point.x + x, point.y + y + 1) }) {
            setOffset(x, y + deltaY)

            // coerceAtLeast() because a new tetromino starts at the very top.
            disposeTetromino(x, (y + deltaY - 1).coerceAtLeast(0))
            paintPoints(it.points.map { point -> Point(point.x, point.y + y + deltaY) }.toTypedArray())
        }
    }

    fun rotate(counterClockwise: Boolean) = tetromino.let {
        val x = offsetX
        val y = offsetY
        // Create a new collection because we only need to rotate between all possible variations of the same kind of tetromino.
        // This is based on color since each one is a unique color.
        val nextPossible = tetrominoes.filter { tetromino -> it.color == tetromino.color }.toList()
        val index = nextPossible.indexOf(it)
        // The next possible tetromino we can use depending on the rotation type.
        val next = nextPossible.elementAtOrElse(if (counterClockwise) index - 1 else index + 1) { if (counterClockwise) nextPossible.last() else nextPossible.first() }
        // Check for collision for the next possible tetromino.
        if (next.points.filter { point -> point !in it.points }.any { point -> board.collides(point.x + x, point.y + y) }) return

        // Set the new tetromino and repaint.
        disposeTetromino(x, y)
        setTetromino(next)
        paintPoints(next.points.map { point -> Point(point.x, point.y + y) }.toTypedArray())
    }

    @Synchronized
    fun getColor(x: Int, y: Int): Color = board.getColor(x, y)

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
            Tetromino(CYAN, arrayOf(Point(1, 2), Point(2, 2), Point(3, 2), Point(4, 2))),
            Tetromino(CYAN, arrayOf(Point(2, 1), Point(2, 2), Point(2, 3), Point(2, 4))),
            Tetromino(CYAN, arrayOf(Point(1, 2), Point(2, 2), Point(3, 2), Point(4, 2))),
            Tetromino(CYAN, arrayOf(Point(2, 1), Point(2, 2), Point(2, 3), Point(2, 4))),

            Tetromino(ORANGE, arrayOf(Point(1, 2), Point(2, 2), Point(3, 2), Point(3, 1))),
            Tetromino(ORANGE, arrayOf(Point(2, 1), Point(2, 2), Point(2, 3), Point(3, 3))),
            Tetromino(ORANGE, arrayOf(Point(1, 2), Point(2, 2), Point(3, 2), Point(1, 3))),
            Tetromino(ORANGE, arrayOf(Point(2, 1), Point(2, 2), Point(2, 3), Point(1, 1))),

            Tetromino(BLUE, arrayOf(Point(1, 2), Point(2, 2), Point(3, 2), Point(3, 3))),
            Tetromino(BLUE, arrayOf(Point(2, 1), Point(2, 2), Point(2, 3), Point(1, 3))),
            Tetromino(BLUE, arrayOf(Point(1, 2), Point(2, 2), Point(3, 2), Point(1, 1))),
            Tetromino(BLUE, arrayOf(Point(2, 1), Point(2, 2), Point(2, 3), Point(3, 1))),

            Tetromino(YELLOW, arrayOf(Point(1, 1), Point(1, 2), Point(2, 1), Point(2, 2))),
            Tetromino(YELLOW, arrayOf(Point(1, 1), Point(1, 2), Point(2, 1), Point(2, 2))),
            Tetromino(YELLOW, arrayOf(Point(1, 1), Point(1, 2), Point(2, 1), Point(2, 2))),
            Tetromino(YELLOW, arrayOf(Point(1, 1), Point(1, 2), Point(2, 1), Point(2, 2))),

            Tetromino(GREEN, arrayOf(Point(2, 1), Point(3, 1), Point(1, 2), Point(2, 2))),
            Tetromino(GREEN, arrayOf(Point(1, 1), Point(1, 2), Point(2, 2), Point(2, 3))),
            Tetromino(GREEN, arrayOf(Point(2, 1), Point(3, 1), Point(1, 2), Point(2, 2))),
            Tetromino(GREEN, arrayOf(Point(1, 1), Point(1, 2), Point(2, 2), Point(2, 3))),

            Tetromino(PURPLE, arrayOf(Point(2, 1), Point(1, 2), Point(2, 2), Point(3, 2))),
            Tetromino(PURPLE, arrayOf(Point(2, 1), Point(1, 2), Point(2, 2), Point(2, 3))),
            Tetromino(PURPLE, arrayOf(Point(1, 2), Point(2, 2), Point(3, 2), Point(2, 3))),
            Tetromino(PURPLE, arrayOf(Point(2, 1), Point(2, 2), Point(3, 2), Point(2, 3))),

            Tetromino(RED, arrayOf(Point(1, 1), Point(2, 1), Point(2, 2), Point(3, 2))),
            Tetromino(RED, arrayOf(Point(2, 1), Point(1, 2), Point(2, 2), Point(1, 3))),
            Tetromino(RED, arrayOf(Point(1, 1), Point(2, 1), Point(2, 2), Point(3, 2))),
            Tetromino(RED, arrayOf(Point(2, 1), Point(1, 2), Point(2, 2), Point(1, 3))),
        )
    }
}
