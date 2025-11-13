package year2016

import common.BaseSolution
import common.Cell
import common.Grid
import common.Position

fun main() = println(SolutionDay13().result())

class SolutionDay13 : BaseSolution() {

    override val day = 13

    private val favoriteNumber = input().toInt()
    private val areaBuilder = Grid.Builder(0..<100, 0..<100) { position -> PointType.from(position, favoriteNumber).char }
    private val area = Grid(areaBuilder) { c, position -> Point(position, c) }

    override fun task1(): String {
        val startPoint = area.getCell(Position(1, 1))!!
        val endPoint = area.getCell(Position(31, 39))!!
        val path = startPoint.findShortestPath(endPoint)!!
        return path.connections.toString()
    }

    override fun task2(): String {
        val startPoint = area.getCell(Position(1, 1))!!
        val distanceMap = startPoint.distanceMap { point -> point.value == '.' }
        val reachablePoints = distanceMap.filter { it.value <= 50 }
        return reachablePoints.size.toString()
    }

    private data class PointType(val char: Char) {
        companion object {
            fun from(position: Position, favoriteNumber: Int): PointType {
                val part1 = position.x * position.x + 3 * position.x + 2 * position.x * position.y + position.y + position.y * position.y
                val part2 = part1 + favoriteNumber
                val bitCount = Integer.bitCount(part2)
                return if (bitCount % 2 == 0) PointType('.') else PointType('#')
            }
        }
    }

    private class Point(position: Position, c: Char) : Cell<Point>(position, c) {
        override fun canGoN() = n?.value == '.'
        override fun canGoS() = s?.value == '.'
        override fun canGoW() = w?.value == '.'
        override fun canGoE() = e?.value == '.'
    }
}