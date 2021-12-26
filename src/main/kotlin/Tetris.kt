import TetrisBoard.collides
import java.awt.Color

/**
 * @author Jordan Abraham
 */
class Tetris {

    private val lock = Object()
    private var offset = Point(5, 0)
    private var tetromino = tetrominoes.random()

    fun tick() {
        synchronized(lock) {
            // Wipe the board. This is temporary as we repaint the saved tetrominoes next.
            disposeTetromino(offset.x, offset.y)
            // Paint the saved tetrominoes.
            TetrisBoard.paintSavedTetrominoes()

            if (yAxisFacing().any { collides(it.x + offset.x, it.y + offset.y + 1) }) {
                // Save the tetromino that just landed.
                TetrisBoard.saveTetromino(tetromino.points, offset.x, offset.y, tetromino.color)
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

    private fun paintPoints(points: Array<Point>) {
        // Paint the current tetromino the user has control of.
        TetrisBoard.paintTetromino(points, offset.x, 0, tetromino.color)
        // Paint the saved points already on the board.
        TetrisBoard.paintSavedTetrominoes()
    }

    private fun disposeTetromino(deltaX: Int, deltaY: Int) {
        // Doing it like this is much more efficient than looping the whole board to clear the last tetromino.
        TetrisBoard.paintTetromino(tetromino.points, deltaX, deltaY, Color.BLACK)
    }

    private fun yAxisFacing(): List<Point> = tetromino.points.filter { Point(it.x, it.y + 1) !in tetromino.points }
    private fun xAxisFacing(left: Boolean): List<Point> = tetromino.points.filter { Point(if (left) it.x - 1 else it.x + 1, it.y) !in tetromino.points }

    fun moveOnXAxis(deltaX: Int) {
        synchronized(lock) {
            // We have to check both sides of the tetromino on the x-axis since they both can collide depending on user input.
            val left = deltaX == -1
            // Check if the tetromino collides on the bottom Y-axis.
            if (yAxisFacing().any { collides(it.x + offset.x, it.y + offset.y + 1) }) return
            // Check if the tetromino collides on the X-axis.
            if (xAxisFacing(left).any { collides(it.x + (if (left) offset.x - 1 else offset.x + 1), it.y + offset.y) }) return

            offset = Point(offset.x + deltaX, offset.y)

            disposeTetromino(if (deltaX == -1) offset.x + 1 else offset.x - 1, offset.y)
            paintPoints(tetromino.points.map { Point(it.x, it.y + offset.y) }.toTypedArray())
        }
    }

    fun moveOnYAxis(deltaY: Int) {
        synchronized(lock) {
            // We only have to check collision on the closest point(s) to the bottom y-axis. The user can't move up on the y-axis.
            if (yAxisFacing().any { collides(it.x + offset.x, it.y + offset.y + 1) }) return

            offset = Point(offset.x, offset.y + deltaY)

            // coerceAtLeast() because a new tetromino starts at the very top.
            disposeTetromino(offset.x, (offset.y - 1).coerceAtLeast(0))
            paintPoints(tetromino.points.map { Point(it.x, it.y + offset.y) }.toTypedArray())
        }
    }

    fun getColor(x: Int, y: Int): Color {
        synchronized(lock) {
            return TetrisBoard.get(x, y)
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
