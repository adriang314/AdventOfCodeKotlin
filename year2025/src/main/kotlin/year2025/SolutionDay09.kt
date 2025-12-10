package year2025

import common.BaseSolution
import common.Position
import common.Rectangle
import java.awt.Polygon
import kotlin.math.max
import kotlin.math.min

fun main() = println(SolutionDay09().result())

class SolutionDay09 : BaseSolution() {

    override val day = 9

    private val redPositions = input().split("\r\n").map { line ->
        val parts = line.split(",")
        Position(parts[0].toInt(), parts[1].toInt())
    }

    private val redAndGreenPositions = mutableSetOf<Position>()

    init {
        for (i in redPositions.indices) {
            val p1 = redPositions[i]
            val p2 = redPositions[(i + 1) % redPositions.size]
            (min(p1.x, p2.x)..max(p1.x, p2.x)).forEach { x ->
                (min(p1.y, p2.y)..max(p1.y, p2.y)).forEach { y ->
                    redAndGreenPositions.add(Position(x, y))
                }
            }
        }
    }

    override fun task1(): String {
        var maxArea = 0L
        for (i in redPositions.indices) {
            for (j in i + 1 until redPositions.size) {
                val rectangle = Rectangle(redPositions[i], redPositions[j])
                if (rectangle.area > maxArea)
                    maxArea = rectangle.area
            }
        }

        return maxArea.toString()
    }

    override fun task2(): String {
        val polygon = Polygon()
        redPositions.forEach { polygon.addPoint(it.x, it.y) }

        fun isInsidePolygon(position: Position) = redAndGreenPositions.contains(position) || polygon.contains(position.x, position.y)

        var maxArea = 0L
        for (i in redPositions.indices) {
            for (j in i + 1 until redPositions.size) {
                val rectangle = Rectangle(redPositions[i], redPositions[j])
                if (rectangle.area < maxArea)
                    continue

                if (rectangle.corners.any { !isInsidePolygon(it) })
                    continue

                val allEdgePositionsInside = rectangle.edgePositions().all { isInsidePolygon(it) }
                if (allEdgePositionsInside)
                    maxArea = rectangle.area
            }
        }

        return maxArea.toString()
    }
}