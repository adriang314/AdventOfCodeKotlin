package common

data class Position(val x: Int, val y: Int) {
    fun up() = Position(x, y - 1)
    fun down() = Position(x, y + 1)
    fun left() = Position(x - 1, y)
    fun right() = Position(x + 1, y)

    fun next(direction: Direction) = when (direction) {
        Direction.Up -> up()
        Direction.Down -> down()
        Direction.Left -> left()
        Direction.Right -> right()
    }

    override fun toString() = "$x,$y"
}

enum class Direction {
    Left, Right, Up, Down;

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