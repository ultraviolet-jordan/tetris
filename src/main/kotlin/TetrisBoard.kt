import java.awt.Color

/**
 * @author Jordan Abraham
 */
class TetrisBoard {

    private val grid = Array(12) { x -> Array(22) { y -> if (x == 0 || x == 11 || y == 0 || y == 21) Color(128, 124, 124) else Color.BLACK } }
    private val points = mutableMapOf<Point, Color>()

    fun getColor(x: Int, y: Int): Color = grid[x][y]
    private fun setColor(x: Int, y: Int, color: Color) { grid[x][y] = color }
    fun collides(x: Int, y: Int): Boolean = grid[x][y] != Color.BLACK || Point(x, y) in points
    fun paintPoints(points: Array<Point>, deltaX: Int, deltaY: Int, color: Color) = points.forEach { setColor(it.x + deltaX, it.y + deltaY, color) }
    fun savePoints(points: Array<Point>, deltaX: Int, deltaY: Int, color: Color) = points.forEach { this.points[Point(it.x + deltaX, it.y + deltaY)] = color }
    fun paintSavedPoints() = points.forEach { setColor(it.key.x, it.key.y, it.value) }
    fun checkRowFilled(y: Int): Boolean = (grid.all { it[y] != Color.BLACK })

    fun wipeRow(y: Int) = grid.forEachIndexed { index, _ ->
        if (index == 0 || index == 11) return@forEachIndexed
        setColor(index, y, Color.BLACK)
        points.remove(Point(index, y))
    }

    fun shiftDown(deltaY: Int) {
        val shifted = mutableMapOf<Point, Color>()
        points.forEach {
            if (it.key.y <= deltaY) {
                // Force the grid to turn black at this current spot.
                // We also do this so the point isn't saved.
                setColor(it.key.x, it.key.y, Color.BLACK)

                setColor(it.key.x, it.key.y + 1, it.value)
                shifted[Point(it.key.x, it.key.y + 1)] = it.value
            } else {
                setColor(it.key.x, it.key.y, it.value)
                shifted[Point(it.key.x, it.key.y)] = it.value
            }
        }
        points.clear()
        points.putAll(shifted)
        paintSavedPoints()
    }
}
