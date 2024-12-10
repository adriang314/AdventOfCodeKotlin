package year2024

import common.BaseSolution
import common.Point
import common.PointMap

fun main() = println(SolutionDay10().result())

class SolutionDay10 : BaseSolution() {

    override val day = 10
    override val year = 2024

    private val pointMap: PointMap<Paths, Hill>
    private val zeroPoints: List<Point<Paths, Hill>>

    init {
        pointMap = PointMap(input(), ::Hill) { Paths() }
        pointMap.points.flatten().forEach {
            it.state.canGoUp = (it.up?.value?.height ?: Int.MAX_VALUE) == it.value.height + 1
            it.state.canGoDown = (it.down?.value?.height ?: Int.MAX_VALUE) == it.value.height + 1
            it.state.canGoLeft = (it.left?.value?.height ?: Int.MAX_VALUE) == it.value.height + 1
            it.state.canGoRight = (it.right?.value?.height ?: Int.MAX_VALUE) == it.value.height + 1
        }

        zeroPoints = pointMap.points.flatten().filter { it.value.height == 0 }
    }

    override fun task1(): String {
        val result = zeroPoints.sumOf { go1(it).size }
        return result.toString()
    }

    override fun task2(): String {
        val result = zeroPoints.sumOf { go2(it) }
        return result.toString()
    }

    private fun go1(point: Point<Paths, Hill>): Set<Point<Paths, Hill>> {
        if (point.value.highest)
            return setOf(point)

        val result = mutableSetOf<Point<Paths, Hill>>()
        if (point.state.canGoUp) result.addAll(go1(point.up!!))
        if (point.state.canGoDown) result.addAll(go1(point.down!!))
        if (point.state.canGoLeft) result.addAll(go1(point.left!!))
        if (point.state.canGoRight) result.addAll(go1(point.right!!))

        return result
    }

    private fun go2(point: Point<Paths, Hill>): Int {
        if (point.value.highest)
            return 1

        var result = 0
        if (point.state.canGoUp) result += go2(point.up!!)
        if (point.state.canGoDown) result += go2(point.down!!)
        if (point.state.canGoLeft) result += go2(point.left!!)
        if (point.state.canGoRight) result += go2(point.right!!)

        return result
    }

    private data class Hill(val id: Char) {
        val height = id.digitToInt()
        val highest = height == 9
    }

    private class Paths {
        var canGoUp = false
        var canGoDown = false
        var canGoLeft = false
        var canGoRight = false
    }
}