package year2021

import common.BaseSolution
import common.Point
import common.PointMap

fun main() = println(SolutionDay09().result())

class SolutionDay09 : BaseSolution() {

    override val day = 9
    override val year = 2021

    override fun task1(): String {
        val riskLevel = points.filter { it.state.isLowPoint }.sumOf { it.value + 1 }
        return riskLevel.toString()
    }

    override fun task2(): String {
        val result = points
            .groupBy { it.state.basinNumber }
            .asSequence()
            .filter { it.key != null }
            .map { it.value.size }
            .sortedDescending()
            .take(3)
            .reduce { acc: Int, i: Int -> acc * i }
        return result.toString()
    }

    private var pointMap = PointMap(input(), Char::digitToInt) { Attribute() }
    private val points = pointMap.points.flatten()

    init {
        points.forEach { point ->
            point.state.isLowPoint = point.neighbours().all { it.value > point.value }
            point.state.isTopPoint = point.value == 9
        }

        markBasins()
    }

    private fun markBasins() {
        var basinNumber = 1
        do {
            val basinPoint = points.firstOrNull { it.state.basinNumber == null && !it.state.isTopPoint }
            if (basinPoint != null)
                markBasin(basinPoint, basinNumber++)
        } while (basinPoint != null)
    }

    private fun markBasin(point: Point<Attribute, Int>, basinNumber: Int) {
        if (point.state.isTopPoint || point.state.basinNumber != null)
            return

        point.state.basinNumber = basinNumber
        point.neighbours().forEach { markBasin(it, basinNumber) }
    }

    data class Attribute(
        var isLowPoint: Boolean = false,
        var basinNumber: Int? = null,
        var isTopPoint: Boolean = false,
    )
}