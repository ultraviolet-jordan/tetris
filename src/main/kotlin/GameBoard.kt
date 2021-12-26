import java.awt.Color

/**
 * @author Jordan Abraham
 */
class GameBoard {

    private val board = Array(SIZEX) { x -> Array(SIZEY) { y -> if (x == 0 || x == 11 || y == 20) DARK_GREY else Color.BLACK } }

    fun set(x: Int, y: Int, color: Color) {
        board[x][y] = color
    }

    fun get(x: Int, y: Int): Color = board[x][y]

    fun isColor(x: Int, y: Int, color: Color): Boolean = board[x][y] == color

    fun fill(points: Array<Point>, offsetX: Int, offsetY: Int, color: Color) {
        points.forEach { set(it.x + offsetX, it.y + offsetY, color) }
    }

    private companion object {
        // 10 for game tetrominoes and 2 for the boundaries.
        private const val SIZEX = 12

        // 20 for the game tetrominoes and 1 for the boundary.
        private const val SIZEY = 21

        private val DARK_GREY = Color(128, 124, 124)
    }
}
