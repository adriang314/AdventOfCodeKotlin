package common

import kotlin.math.absoluteValue

//   ------X----->
//   |
//   |
//   |
//   Y
//   |
//   |
//   |
//   v
/**
 * Represents a position in a 2D grid.
 *
 * @param x The x coordinate. Length from the left edge of the grid.
 * @param y The y coordinate. Length from the top edge of the grid.
 */
data class Position(val x: Int, val y: Int) : Comparable<Position> {
    fun n() = Position(x, y - 1)
    fun s() = Position(x, y + 1)
    fun e() = Position(x + 1, y)
    fun w() = Position(x - 1, y)
    fun nw() = Position(x - 1, y - 1)
    fun ne() = Position(x + 1, y - 1)
    fun sw() = Position(x - 1, y + 1)
    fun se() = Position(x + 1, y + 1)

    fun shift(xShift: Int, yShift: Int) = Position(x + xShift, y + yShift)

    fun next(direction: Direction) = when (direction) {
        Direction.N -> n()
        Direction.S -> s()
        Direction.W -> w()
        Direction.E -> e()
    }

    /**
     * Returns the Manhattan distance between this position and the other one
     */
    fun distanceTo(other: Position): Long {
        return (this.x - other.x).absoluteValue + (this.y - other.y).absoluteValue.toLong()
    }

    /**
     * Comparison based on top -> bottom, left -> right rule.
     * Top left corner position (0,0) is the smallest one.
     * Bottom right corner (x,y) is the largest one.
     *
     *  ###################   *----X--->
     *  #(0,0) (1,0) (2,0)#   |
     *  #(0,1) (1,1) (2,1)#   Y
     *  #(0,2) (1,2) (2,2)#   |
     *  ###################   v
     *
     * (0,0) < (1,0) < (0,1) < (1,1)
     */
    override fun compareTo(other: Position): Int {
        val yComparison = y.compareTo(other.y)
        if (yComparison != 0)
            return yComparison
        val xComparison = x.compareTo(other.x)
        return xComparison
    }

    override fun toString() = "$x,$y"

    companion object {
        fun fromString(s: String): Position {
            val (x, y) = s.split(",").map { it.toInt() }
            return Position(x, y)
        }
    }
}

data class DirectedPosition(
    val direction: Direction,
    val position: Position,
)

enum class Direction {
    W, E, N, S;

    fun turnsTo(direction: Direction): Int {
        if (direction == this)
            return 0
        return when (direction) {
            W -> if (this == E) 2 else 1
            E -> if (this == W) 2 else 1
            N -> if (this == S) 2 else 1
            S -> if (this == N) 2 else 1
        }
    }

    companion object {
        fun from(c: Char) = when (c) {
            '>', 'E', 'e' -> E
            '<', 'W', 'w' -> W
            '^', 'N', 'n' -> N
            'v', 'S', 's' -> S
            else -> throw RuntimeException("Unknown direction")
        }
    }
}