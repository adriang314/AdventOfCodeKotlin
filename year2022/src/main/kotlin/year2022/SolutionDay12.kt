package year2022

import common.*

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {
    override val day = 12

    private val map: Grid<Point> = Grid(input()) { value, position -> Point(position, value) }
    private val startPoint: Point = map.cells.single { it.value == 'S' }
    private val endPoint: Point = map.cells.single { it.value == 'E' }

    override fun task1(): String {
        val shortestPath = startPoint.findShortestPath(endPoint)!!
        return shortestPath.connections.toString()
    }

    override fun task2(): String {
        val startPoints = map.cells.filter { it.height == 'a'.code }
        val shortestPath = startPoints.mapNotNull { it.findShortestPath(endPoint) }.minBy { it.connections }
        return shortestPath.connections.toString()
    }

    private class Point(position: Position, value: Char) : Cell<Point>(position, value) {
        val height: Int = when (value) {
            'S' -> 'a'.code
            'E' -> 'z'.code
            else -> value.code
        }

        override fun canGoN() = (n?.height ?: Int.MAX_VALUE) <= height + 1
        override fun canGoS() = (s?.height ?: Int.MAX_VALUE) <= height + 1
        override fun canGoW() = (w?.height ?: Int.MAX_VALUE) <= height + 1
        override fun canGoE() = (e?.height ?: Int.MAX_VALUE) <= height + 1
    }
}