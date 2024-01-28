package common

open class Point<T, U>(val rowIdx: Int, val colIdx: Int, val value: U, var state: T) {
    val id = "[$rowIdx,$colIdx]"
    var left: Point<T, U>? = null
    var right: Point<T, U>? = null
    var up: Point<T, U>? = null
    var down: Point<T, U>? = null

    fun neighbours() = listOfNotNull(left, right, up, down)

    override fun toString() = "[$rowIdx,$colIdx] $value $state"
}

open class PointMap<T, U>(input: String, valueMapper: (Char) -> U, defaultState: () -> T) {
    val points: List<List<Point<T, U>>> = input.split("\r\n")
        .mapIndexed { x, line ->
            line.toList().mapIndexed { y, c -> Point(x, y, valueMapper(c), defaultState()) }
        }
    val height: Int = points.size
    val length: Int = points.first().size

    init {
        for (i in 0..<height) {
            for (j in 0..<length) {
                val current = points[i][j]
                val up = points.getOrNull(i - 1)?.getOrNull(j)
                val down = points.getOrNull(i + 1)?.getOrNull(j)
                val left = points.getOrNull(i)?.getOrNull(j - 1)
                val right = points.getOrNull(i)?.getOrNull(j + 1)

                if (up != null) current.up = up
                if (down != null) current.down = down
                if (left != null) current.left = left
                if (right != null) current.right = right
            }
        }
    }
}