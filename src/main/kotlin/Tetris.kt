import java.awt.Color

/**
 * @author Jordan Abraham
 */
class Tetris {

    private val lock = Object()
    private val board = TetrisBoard()

    private var offset = Point(5, 0)
    private var tetromino = tetrominoes.random()

    fun tick() {
        synchronized(lock) {
            // Wipe the board. This is temporary as we repaint the saved tetrominoes next.
            disposeTetromino(offset.x, offset.y)
            // Paint the saved tetrominoes.
            board.paintSavedTetrominoes()

            // Checks if the current tetromino landed on the closest Y-axis.
            if (tetromino.pointsAxis(deltaY = 1).any { board.collides(it.x + offset.x, it.y + offset.y + 1) }) {
                // Save the tetromino that just landed.
                board.saveTetromino(tetromino.points, offset.x, offset.y, tetromino.color)

                // The default offset.
                offset = Point(5, 0)

                tetromino = tetrominoes.random()
                // Paint the new tetromino.
                paintPoints(tetromino.points)

                repeat(21) { y ->
                    // Check for the game boundary on the Y-axis.
                    if (y == 0 || y == 21) return@repeat
                    if (board.checkRowComplete(y)) {
                        repeat(11) { x ->
                            // Check for the game boundary on the X-axis.
                            if (x != 0 && x != 11) {
                                board.set(x, y, Color.BLACK)
                                board.removePoint(x, y)
                            }
                        }
                        board.shiftDown(y)
                        board.paintSavedTetrominoes()
                    }
                }
                return
            }

            // Move the current tetromino and change the offset.
            paintPoints(tetromino.points.map { Point(it.x, it.y + offset.y + 1) }.toTypedArray())
            offset = Point(offset.x, offset.y + 1)
        }
    }

    private fun paintPoints(points: Array<Point>) {
        // Paint the current tetromino the user has control of.
        board.paintTetromino(points, offset.x, 0, tetromino.color)
        // Paint the saved points already on the board.
        board.paintSavedTetrominoes()
    }

    private fun disposeTetromino(deltaX: Int, deltaY: Int) {
        // Doing it like this is much more efficient than looping the whole board to clear the last tetromino.
        board.paintTetromino(tetromino.points, deltaX, deltaY, Color.BLACK)
    }

    fun moveOnXAxis(deltaX: Int) {
        synchronized(lock) {
            // We have to check both sides of the tetromino on the x-axis since they both can collide depending on user input.
            val counterClockwise = deltaX == -1
            // Check if the tetromino collides on the bottom Y-axis.
            if (tetromino.pointsAxis(deltaY = 0).any { board.collides(it.x + offset.x, it.y + offset.y + 1) }) return
            // Check if the tetromino collides on the X-axis.
            if (tetromino.pointsAxis(counterClockwise, deltaX = 1).any { board.collides(it.x + (if (counterClockwise) offset.x - 1 else offset.x + 1), it.y + offset.y) }) return

            offset = Point(offset.x + deltaX, offset.y)

            disposeTetromino(if (deltaX == -1) offset.x + 1 else offset.x - 1, offset.y)
            paintPoints(tetromino.points.map { Point(it.x, it.y + offset.y) }.toTypedArray())
        }
    }

    fun moveOnYAxis(deltaY: Int) {
        synchronized(lock) {
            // We only have to check collision on the closest point(s) to the bottom y-axis. The user can't move up on the y-axis.
            if (tetromino.pointsAxis(deltaY = 1).any { board.collides(it.x + offset.x, it.y + offset.y + 1) }) return

            offset = Point(offset.x, offset.y + deltaY)

            // coerceAtLeast() because a new tetromino starts at the very top.
            disposeTetromino(offset.x, (offset.y - 1).coerceAtLeast(0))
            paintPoints(tetromino.points.map { Point(it.x, it.y + offset.y) }.toTypedArray())
        }
    }

    fun rotate(counterClockwise: Boolean) {
        synchronized(lock) {
            // Create a new collection because we only need to rotate between all possible variations of the same kind of tetromino.
            // This is based on color since each one is a unique color.
            val possibleTetrominoes = tetrominoes.filter { it.color == tetromino.color }.toList()
            val index = possibleTetrominoes.indexOf(tetromino)
            // The next possible tetromino we can use depending on the rotation type.
            val next = possibleTetrominoes.elementAtOrElse(if (counterClockwise) index - 1 else index + 1) { if (counterClockwise) possibleTetrominoes.last() else possibleTetrominoes.first() }
            // Check for collision for the next possible tetromino.
            if (next.points.filter { Point(it.x, it.y) !in tetromino.points }.any { board.collides(it.x + offset.x, it.y + offset.y) }) return

            // Set the new tetromino and repaint.
            disposeTetromino(offset.x, offset.y)
            tetromino = next
            paintPoints(tetromino.points.map { Point(it.x, it.y + offset.y) }.toTypedArray())
        }
    }

    fun getColor(x: Int, y: Int): Color {
        synchronized(lock) {
            return board.get(x, y)
        }
    }

    private companion object {

        private val CYAN = Color(0, 255, 255)
        private val PURPLE = Color(161, 0, 240)
        private val ORANGE = Color(255, 120, 0)
        private val BLUE = Color(0, 0, 172)
        private val YELLOW = Color(255, 255, 0)
        private val GREEN = Color(0, 255, 0)

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

            Tetromino(Color.RED, arrayOf(Point(1, 1), Point(2, 1), Point(2, 2), Point(3, 2))),
            Tetromino(Color.RED, arrayOf(Point(2, 1), Point(1, 2), Point(2, 2), Point(1, 3))),
            Tetromino(Color.RED, arrayOf(Point(1, 1), Point(2, 1), Point(2, 2), Point(3, 2))),
            Tetromino(Color.RED, arrayOf(Point(2, 1), Point(1, 2), Point(2, 2), Point(1, 3))),
        )
    }
}
