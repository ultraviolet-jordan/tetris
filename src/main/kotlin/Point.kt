/**
 * @author Jordan Abraham
 */
@JvmInline
value class Point(private val packed: Int) {

    constructor(x: Int, y: Int) : this(x shl 8 or y)

    val x: Int get() = packed shr 8
    val y: Int get() = packed and 0xFF
}
