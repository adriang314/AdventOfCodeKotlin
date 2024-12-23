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
data class Position(val x: Int, val y: Int) {
    fun up() = n()
    fun down() = s()
    fun left() = w()
    fun right() = e()

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
        Direction.Up -> up()
        Direction.Down -> down()
        Direction.Left -> left()
        Direction.Right -> right()
    }

    /**
     * Returns the Manhattan distance between this position and the other one
     */
    fun distanceTo(other: Position): Long {
        return (this.x - other.x).absoluteValue + (this.y - other.y).absoluteValue.toLong()
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
    Left, Right, Up, Down;

    fun turnsTo(direction: Direction): Int {
        if (direction == this)
            return 0
        return when (direction) {
            Left -> if (this == Right) 2 else 1
            Right -> if (this == Left) 2 else 1
            Up -> if (this == Down) 2 else 1
            Down -> if (this == Up) 2 else 1
        }
    }

    companion object {
        fun from(c: Char) = when (c) {
            '>', 'E' -> Right
            '<', 'W' -> Left
            '^', 'N' -> Up
            'v', 'S' -> Down
            else -> throw RuntimeException("Unknown direction")
        }
    }
}