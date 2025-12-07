package year2025

import common.BaseSolution
import common.Cell
import common.Grid
import common.Position
import java.util.*

fun main() = println(SolutionDay07().result())

class SolutionDay07 : BaseSolution() {

    override val day = 7

    private val map = Grid(input()) { value, position -> Point(position, value) }
    private val start = map.cells.single { it.isStart() }

    override fun task1(): String {
        return BeanSplitCounter().count(start).toString()
    }

    override fun task2(): String {
        return BeanPathsCounter().count(start).toString()
    }

    private class BeanSplitCounter() {

        fun count(start: Point): Int {
            val paths = LinkedList(listOf(start))
            val visitedPoints = mutableSetOf<Point>()
            var splitters = 0

            fun tryNextMove(point: Point?) {
                point?.let {
                    if (!visitedPoints.contains(it)) {
                        paths.add(it)
                        visitedPoints.add(it)
                    }
                }
            }

            while (paths.isNotEmpty()) {
                val nextPoint = paths.removeFirst()
                if (nextPoint.isSplitter()) {
                    splitters++
                    tryNextMove(nextPoint.w)
                    tryNextMove(nextPoint.e)
                } else {
                    tryNextMove(nextPoint.s)
                }
            }

            return splitters
        }
    }

    private class BeanPathsCounter() {
        private val cache = mutableMapOf<Point, Long>()

        fun count(point: Point): Long {
            cache[point]?.let { return it }

            if (point.isSplitter()) {
                val westCount = point.w?.let { count(it) } ?: 0L
                val eastCount = point.e?.let { count(it) } ?: 0L
                cache[point] = westCount + eastCount
                return westCount + eastCount
            }

            val southCount = point.s?.let { count(it) } ?: 1L
            cache[point] = southCount
            return southCount
        }
    }

    private class Point(position: Position, c: Char) : Cell<Point>(position, c) {

        fun isStart(): Boolean = value == 'S'

        fun isSplitter(): Boolean = value == '^'
    }
}