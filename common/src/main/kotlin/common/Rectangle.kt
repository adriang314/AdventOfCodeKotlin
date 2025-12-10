package common

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Rectangle(corner1: Position, corner2: Position) {
    val topLeft: Position
    val topRight: Position
    val bottomLeft: Position
    val bottomRight: Position
    val corners: List<Position>
    val area: Long

    init {
        val minX = min(corner1.x, corner2.x)
        val minY = min(corner1.y, corner2.y)
        val maxX = max(corner1.x, corner2.x)
        val maxY = max(corner1.y, corner2.y)

        topLeft = Position(minX, minY)
        topRight = Position(maxX, minY)
        bottomLeft = Position(minX, maxY)
        bottomRight = Position(maxX, maxY)
        corners = listOf(topLeft, topRight, bottomRight, bottomLeft)
        area = (abs(topLeft.x - bottomRight.x) + 1L) * (abs(topLeft.y - bottomRight.y) + 1L)
    }

    fun edgePositions(): Set<Position> {
        val xRng = topLeft.x..topRight.x
        val yRng = topLeft.y..bottomLeft.y
        val positions = mutableSetOf<Position>()

        xRng.forEach {
            positions.add(Position(it, topLeft.y))
            positions.add(Position(it, bottomLeft.y))
        }
        yRng.forEach {
            positions.add(Position(topLeft.x, it))
            positions.add(Position(bottomLeft.x, it))
        }

        return positions
    }
}