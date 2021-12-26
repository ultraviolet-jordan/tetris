import java.awt.Color

/**
 * @author Jordan Abraham
 */
object TetrisBoard {

    private val DARK_GREY = Color(128, 124, 124)

    private val grid = Array(12) { x -> Array(21) { y -> if (x == 0 || x == 11 || y == 20) DARK_GREY else Color.BLACK } }
    private val savedTetrominoes = mutableMapOf<Point, Color>()

    fun collides(x: Int, y: Int): Boolean = Point(x, y) in savedTetrominoes || grid[x][y] != Color.BLACK

    fun set(x: Int, y: Int, color: Color) {
        grid[x][y] = color
    }

    fun get(x: Int, y: Int): Color = grid[x][y]

    fun paintTetromino(points: Array<Point>, deltaX: Int, deltaY: Int, color: Color) = points.forEach { set(it.x + deltaX, it.y + deltaY, color) }

    fun saveTetromino(points: Array<Point>, deltaX: Int, deltaY: Int, color: Color) = points.forEach { savedTetrominoes[Point(it.x + deltaX, it.y + deltaY)] = color }

    fun paintSavedTetrominoes() = savedTetrominoes.forEach { set(it.key.x, it.key.y, it.value) }
}
