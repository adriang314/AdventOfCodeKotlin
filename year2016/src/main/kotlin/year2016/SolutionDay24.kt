package year2016

import common.*

fun main() = println(SolutionDay24().result())

class SolutionDay24 : BaseSolution() {

    override val day = 24

    private val grid = Grid(input()) { c, position -> Point(position, c) }
    private val allLocations = grid.cells.filter { it.isLocation() }.toSet()
    private val startLocation = allLocations.single { it.value == '0' }

    init {
        // close dead ends

        do {
            var walSet = false
            grid.cells.forEach {
                if (it.isDeadEnd()) {
                    it.value = '#'
                    walSet = true
                }
            }
        } while (walSet)
    }

    override fun task1(): String {
        val result = allLocations.allPermutations().filter { it.first() == startLocation }.minOf { path -> findPathLength(path) }
        return result.toString()
    }

    override fun task2(): String {
        val result = allLocations.allPermutations().filter { it.first() == startLocation }.map { it.plus(startLocation) }.minOf { path -> findPathLength(path) }
        return result.toString()
    }

    private fun findPathLength(path: List<Point>): Int {
        var steps = 0
        for (i in 0 until path.size - 1) {
            val currPoint = path[i]
            val nextPoint = path[i + 1]
            steps += currPoint.findShortestPath(nextPoint)!!.connections
        }

        return steps
    }

    private class Point(position: Position, c: Char) : Cell<Point>(position, c) {
        fun isLocation() = value.isDigit()
        fun isWall() = value == '#'
        fun isSpace() = !isWall()
        fun isDeadEnd() = isSpace() && !isLocation() && neighbours().count() <= 1

        override fun canGoN() = n?.isSpace() == true
        override fun canGoS() = s?.isSpace() == true
        override fun canGoW() = w?.isSpace() == true
        override fun canGoE() = e?.isSpace() == true
    }
}