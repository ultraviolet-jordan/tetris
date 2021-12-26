import java.awt.Color

/**
 * @author Jordan Abraham
 */
class Game {

    private val lock = Object()
    private val gameBoard = GameBoard()
    private val saved = mutableMapOf<Point, Color>()
    private var tetromino = tetrominoes.random()
    private var offset = Point(5, 0)

    fun tick() {
        synchronized(lock) {
            // Wipe the board. This is temporary as we repaint the saved tetrominoes next.
            disposeTetromino(offset.x, offset.y)
            // Paint the saved tetrominoes.
            paintSaved()

            if (yAxisFacing().any { collides(it.x + offset.x, it.y + offset.y + 1) }) {
                // Save the tetromino that just landed.
                tetromino.points.forEach { saved[Point(it.x + offset.x, it.y + offset.y)] = tetromino.color }
                // The default offset.
                offset = Point(5, 0)

                tetromino = tetrominoes.random()
                // Paint the new tetromino.
                paintPoints(tetromino.points)
                return
            }

            // Move the current tetromino and change the offset.
            paintPoints(tetromino.points.map { Point(it.x, it.y + offset.y + 1) }.toTypedArray())
            offset = Point(offset.x, offset.y + 1)
        }
    }

    private fun paintSaved() = saved.forEach { gameBoard.set(it.key.x, it.key.y, it.value) }

    private fun paintPoints(points: Array<Point>) {
        // Paint the current tetromino the user has control of.
        gameBoard.fill(points, offset.x, 0, tetromino.color)
        // Paint the saved points already on the board.
        paintSaved()
    }

    private fun disposeTetromino(offsetX: Int, offsetY: Int) {
        // Doing it like this is much more efficient than looping the whole board to clear the last tetromino.
        gameBoard.fill(tetromino.points, offsetX, offsetY, Color.BLACK)
    }

    private fun collides(x: Int, y: Int): Boolean = Point(x, y) in saved || !gameBoard.isColor(x, y, Color.BLACK)
    private fun yAxisFacing(): List<Point> = tetromino.points.filter { Point(it.x, it.y + 1) !in tetromino.points }
    private fun xAxisFacing(left: Boolean): List<Point> = tetromino.points.filter { Point(if (left) it.x - 1 else it.x + 1, it.y) !in tetromino.points }

    fun moveOnXAxis(x: Int) {
        synchronized(lock) {
            // We have to check both sides of the tetromino on the x-axis since they both can collide depending on user input.
            val left = x == -1
            // Check if the tetromino collides on the bottom Y-axis.
            if (yAxisFacing().any { collides(it.x + offset.x, it.y + offset.y + 1) }) return
            // Check if the tetromino collides on the X-axis.
            if (xAxisFacing(left).any { collides(it.x + (if (left) offset.x - 1 else offset.x + 1), it.y + offset.y) }) return

            offset = Point(offset.x + x, offset.y)

            disposeTetromino(if (x == -1) offset.x + 1 else offset.x - 1, offset.y)
            paintPoints(tetromino.points.map { Point(it.x, it.y + offset.y) }.toTypedArray())
        }
    }

    fun moveOnYAxis(y: Int) {
        synchronized(lock) {
            // We only have to check collision on the closest point(s) to the bottom y-axis. The user can't move up on the y-axis.
            if (yAxisFacing().any { collides(it.x + offset.x, it.y + offset.y + 1) }) return

            offset = Point(offset.x, offset.y + y)

            // coerceAtLeast() because a new tetromino starts at the very top.
            disposeTetromino(offset.x, (offset.y - 1).coerceAtLeast(0))
            paintPoints(tetromino.points.map { Point(it.x, it.y + offset.y) }.toTypedArray())
        }
    }

    fun getColor(x: Int, y: Int): Color {
        synchronized(lock) {
            return gameBoard.get(x, y)
        }
    }

    private companion object {

        private val PURPLE = Color(161, 0, 240)

        // All the tetrominoes that can be used in the game.
        private val tetrominoes = arrayOf(
            Tetromino(Color.CYAN, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1))),
            Tetromino(Color.CYAN, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(1, 3))),
            Tetromino(Color.CYAN, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(3, 1))),
            Tetromino(Color.CYAN, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(1, 3))),

            Tetromino(Color.ORANGE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(2, 0))),
            Tetromino(Color.ORANGE, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 2))),
            Tetromino(Color.ORANGE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(0, 2))),
            Tetromino(Color.ORANGE, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(0, 0))),

            Tetromino(Color.BLUE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(2, 2))),
            Tetromino(Color.BLUE, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(0, 2))),
            Tetromino(Color.BLUE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(0, 0))),
            Tetromino(Color.BLUE, arrayOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 0))),

            Tetromino(Color.YELLOW, arrayOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1))),
            Tetromino(Color.YELLOW, arrayOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1))),
            Tetromino(Color.YELLOW, arrayOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1))),
            Tetromino(Color.YELLOW, arrayOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1))),

            Tetromino(Color.GREEN, arrayOf(Point(1, 0), Point(2, 0), Point(0, 1), Point(1, 1))),
            Tetromino(Color.GREEN, arrayOf(Point(0, 0), Point(0, 1), Point(1, 1), Point(1, 2))),
            Tetromino(Color.GREEN, arrayOf(Point(1, 0), Point(2, 0), Point(0, 1), Point(1, 1))),
            Tetromino(Color.GREEN, arrayOf(Point(0, 0), Point(0, 1), Point(1, 1), Point(1, 2))),

            Tetromino(PURPLE, arrayOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(2, 1))),
            Tetromino(PURPLE, arrayOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(1, 2))),
            Tetromino(PURPLE, arrayOf(Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 2))),
            Tetromino(PURPLE, arrayOf(Point(1, 0), Point(1, 1), Point(2, 1), Point(1, 2))),

            Tetromino(Color.RED, arrayOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(2, 1))),
            Tetromino(Color.RED, arrayOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(0, 2))),
            Tetromino(Color.RED, arrayOf(Point(0, 0), Point(1, 0), Point(1, 1), Point(2, 1))),
            Tetromino(Color.RED, arrayOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(0, 2))),
        )
    }
}
