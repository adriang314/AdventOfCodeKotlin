package year2022

import common.BaseSolution
import common.Point
import common.PointMap

fun main() = println(SolutionDay08().result())

private typealias PointWithVisibility = Point<SolutionDay08.Visibility, Int>

class SolutionDay08 : BaseSolution() {
    override val day = 8
    override val year = 2022

    override fun task1(): String {
        val visibleCount = pointMap.points.flatten().count { it.state.isVisible() }
        return visibleCount.toString()
    }

    override fun task2(): String {
        val maxScenicScore = pointMap.points.flatten().map { it.state.visibleDistance.scenicScore }.maxBy { it }
        return maxScenicScore.toString()
    }

    private val pointMap: PointMap<Visibility, Int>

    init {
        pointMap = PointMap(input(), Char::digitToInt) { Visibility() }
        val length = pointMap.length
        val height = pointMap.height
        val points = pointMap.points

        setVisibility((0..<height), (0..<length), { i, j -> points[i][j] }) { it.state.fromLeft = true }
        setVisibility((0..<height), (length - 1 downTo 0), { i, j -> points[i][j] }) { it.state.fromRight = true }
        setVisibility((0..<length), (0..<height), { i, j -> points[j][i] }) { it.state.fromTop = true }
        setVisibility((0..<length), (height - 1 downTo 0), { i, j -> points[j][i] }) { it.state.fromBottom = true }

        for (i in 0..<height) {
            for (j in 0..<length) {
                val currPoint = points[i][j]
                val distLeft = visibleDistance(currPoint) { it.left }
                val distRight = visibleDistance(currPoint) { it.right }
                val distUp = visibleDistance(currPoint) { it.up }
                val distDown = visibleDistance(currPoint) { it.down }
                currPoint.state.visibleDistance = VisibleDistance(distLeft, distRight, distUp, distDown)
            }
        }
    }

    private fun setVisibility(
        range1: Iterable<Int>,
        range2: Iterable<Int>,
        currPointProvider: (Int, Int) -> PointWithVisibility,
        setVisible: (PointWithVisibility) -> Unit
    ) {
        var max: Int?
        for (i in range1) {
            max = null
            for (j in range2) {
                val currPoint = currPointProvider(i, j)
                if (max == null || currPoint.value > max) {
                    max = currPoint.value
                    setVisible(currPoint)
                }
            }
        }
    }

    private fun visibleDistance(
        currPoint: PointWithVisibility,
        nextPointProvider: (PointWithVisibility) -> PointWithVisibility?
    ): Int {
        var viewingDistance = 0
        var nextPoint = nextPointProvider(currPoint)
        while (nextPoint != null) {
            viewingDistance++
            if (nextPoint.value >= currPoint.value)
                return viewingDistance
            nextPoint = nextPointProvider(nextPoint)
        }
        return viewingDistance
    }

    class Visibility {
        var fromLeft = false
        var fromRight = false
        var fromTop = false
        var fromBottom = false
        var visibleDistance: VisibleDistance = VisibleDistance.empty

        fun isVisible() = fromLeft || fromRight || fromTop || fromBottom

        override fun toString() = "${isVisible()} score: ${visibleDistance.scenicScore}"
    }

    data class VisibleDistance(val left: Int, val right: Int, val up: Int, val down: Int) {
        val scenicScore = left * right * up * down

        companion object {
            val empty = VisibleDistance(0, 0, 0, 0)
        }
    }
}