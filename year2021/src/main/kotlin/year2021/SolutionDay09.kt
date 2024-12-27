package year2021

import common.*

fun main() = println(SolutionDay09().result())

class SolutionDay09 : BaseSolution() {

    override val day = 9

    override fun task1(): String {
        val riskLevel = map.cells.filter { it.isLowPoint }.sumOf { it.height + 1 }
        return riskLevel.toString()
    }

    override fun task2(): String {
        val result = map.cells
            .groupBy { it.basinNumber }
            .asSequence()
            .filter { it.key != null }
            .map { it.value.size }
            .sortedDescending()
            .take(3)
            .reduce { acc: Int, i: Int -> acc * i }
        return result.toString()
    }

    private var map = Grid(input()) { value, position -> Point(position, value) }

    init {
        map.cells.forEach { point ->
            point.isLowPoint = point.neighbours().all { it.value > point.value }
            point.isTopPoint = point.height == 9
        }

        // mark basins
        var basinNumber = 1
        do {
            val basinPoint = map.cells.firstOrNull { it.basinNumber == null && !it.isTopPoint }
            basinPoint?.markBasin(basinNumber++)
        } while (basinPoint != null)
    }

    private class Point(position: Position, value: Char) : Cell<Point>(position, value) {
        val height = value.digitToInt()
        var isLowPoint: Boolean = false
        var basinNumber: Int? = null
        var isTopPoint: Boolean = false

        fun markBasin(basinNumber: Int) {
            if (isTopPoint || this.basinNumber != null)
                return

            this.basinNumber = basinNumber
            neighbours().forEach { it.markBasin(basinNumber) }
        }
    }
}