package year2021

import common.BaseSolution
import kotlin.math.abs
import kotlin.math.max

fun main() = println(SolutionDay05().result())

class SolutionDay05 : BaseSolution() {

    override val day = 5
    override val year = 2021

    override fun task1(): String {
        val validLines = lines.filter { it.isVertical() or it.isHorizontal() }
        val overlappingPoints = overlappingPoints(validLines)
        return overlappingPoints.toString()
    }

    override fun task2(): String {
        val validLines = lines
        val overlappingPoints = overlappingPoints(validLines)
        return overlappingPoints.toString()
    }

    private fun overlappingPoints(lines: List<Line>): Int {
        val pointCountMap = mutableMapOf<Point, Int>()
        lines.forEach { line ->
            line.points.forEach { point ->
                pointCountMap.compute(point) { _, value -> if (value == null) 1 else value + 1 }
            }
        }
        return pointCountMap.filterValues { it > 1 }.count()
    }

    private var lines: List<Line> = input().split("\r\n").map { line ->
        val parts = line.split(" -> ")
        Line(Point.from(parts[0]), Point.from(parts[1]))
    }

    data class Line(val start: Point, val end: Point) {
        private val xDirection = if (start.x > end.x) Direction.Down else if (start.x < end.x) Direction.Up else null
        private val yDirection = if (start.y > end.y) Direction.Down else if (start.y < end.y) Direction.Up else null
        private val pointCount = max(abs(start.x - end.x), abs(start.y - end.y))

        val points = (0..pointCount).map {
            val xChange = xDirection?.change ?: 0
            val yChange = yDirection?.change ?: 0
            Point(start.x + it * xChange, start.y + it * yChange)
        }

        fun isHorizontal() = yDirection == null
        fun isVertical() = xDirection == null
    }

    enum class Direction(val change: Int) { Up(1), Down(-1) }

    data class Point(val x: Int, val y: Int) {
        companion object {
            fun from(s: String): Point {
                val xy = s.split(",")
                return Point(xy[0].toInt(), xy[1].toInt())
            }
        }
    }
}