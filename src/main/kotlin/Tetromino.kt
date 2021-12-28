import java.awt.Color

/**
 * @author Jordan Abraham
 */
data class Tetromino(
    val color: Color,
    val points: Array<Point>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tetromino

        if (!points.contentEquals(other.points)) return false

        return true
    }

    override fun hashCode(): Int {
        return points.contentHashCode()
    }
}

fun Tetromino.pointsAxis(
    left: Boolean = false,
    deltaX: Int = 0,
    deltaY: Int = 0,
    otherPoints: Array<Point>? = null
): List<Point> = points.filter { Point(if (left) it.x - deltaX else it.x + deltaX, it.y + deltaY) !in (otherPoints ?: points) }
