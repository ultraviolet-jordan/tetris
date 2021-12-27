import java.awt.Color

/**
 * @author Jordan Abraham
 */
class TetrisBoard {

    private val grid = Array(12) { x -> Array(21) { y -> if (x == 0 || x == 11 || y == 20) Color(128, 124, 124) else Color.BLACK } }
    private val savedTetrominoes = mutableMapOf<Point, Color>()

    fun get(x: Int, y: Int): Color = grid[x][y]
    fun set(x: Int, y: Int, color: Color) {
        grid[x][y] = color
    }

    fun collides(x: Int, y: Int): Boolean = Point(x, y) in savedTetrominoes || grid[x][y] != Color.BLACK
    fun paintTetromino(points: Array<Point>, deltaX: Int, deltaY: Int, color: Color) = points.forEach { set(it.x + deltaX, it.y + deltaY, color) }
    fun saveTetromino(points: Array<Point>, deltaX: Int, deltaY: Int, color: Color) = points.forEach { savedTetrominoes[Point(it.x + deltaX, it.y + deltaY)] = color }
    fun paintSavedTetrominoes() = savedTetrominoes.forEach { set(it.key.x, it.key.y, it.value) }
    fun checkRowComplete(y: Int): Boolean = (grid.all { it[y] != Color.BLACK })
    fun removePoint(x: Int, y: Int) = savedTetrominoes.remove(Point(x, y))
    fun shiftDown(deltaY: Int) = with(mutableMapOf<Point, Color>()) {
        putAll(savedTetrominoes)
        savedTetrominoes.clear()
        forEach {
            if (it.key.y <= deltaY) {
                set(it.key.x, it.key.y, Color.BLACK)
                savedTetrominoes[Point(it.key.x, it.key.y + 1)] = it.value
            } else {
                set(it.key.x, it.key.y, it.value)
                savedTetrominoes[Point(it.key.x, it.key.y)] = it.value
            }
        }
    }
}
