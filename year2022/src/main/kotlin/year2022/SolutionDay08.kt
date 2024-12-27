package year2022

import common.*

fun main() = println(SolutionDay08().result())

class SolutionDay08 : BaseSolution() {

    override val day = 8

    private val treeMap = TreeMap(input())

    override fun task1(): String {
        val visibleCount = treeMap.points().count { it.isVisible() }
        return visibleCount.toString()
    }

    override fun task2(): String {
        val maxScenicScore = treeMap.points().map { it.visibleDistance.scenicScore }.maxBy { it }
        return maxScenicScore.toString()
    }

    private class TreeMap(treeMap: String) {
        private val map = Grid(treeMap) { c, position -> TreePoint(position, c) }
        private val width = map.width
        private val height = map.height

        init {
            setVisibility((0..<height), (0..<width), { y, x -> point(x, y) }) { it.visibleFromW = true }
            setVisibility((0..<height), (width - 1 downTo 0), { y, x -> point(x, y) }) { it.visibleFromE = true }
            setVisibility((0..<width), (0..<height), { x, y -> point(x, y) }) { it.visibleFromN = true }
            setVisibility((0..<width), (height - 1 downTo 0), { x, y -> point(x, y) }) { it.visibleFromS = true }

            for (y in 0..<height) {
                for (x in 0..<width) {
                    val distW = visibleDistance(point(x, y)) { it.w }
                    val distE = visibleDistance(point(x, y)) { it.e }
                    val distN = visibleDistance(point(x, y)) { it.n }
                    val distS = visibleDistance(point(x, y)) { it.s }
                    point(x, y).visibleDistance = VisibleDistance(distW, distE, distN, distS)
                }
            }
        }

        fun points() = map.cells

        private fun point(x: Int, y: Int) = map.getCell(x, y)!!

        private fun setVisibility(
            range1: Iterable<Int>,
            range2: Iterable<Int>,
            currPointProvider: (Int, Int) -> TreePoint,
            setVisible: (TreePoint) -> Unit
        ) {
            var max: Int?
            for (i in range1) {
                max = null
                for (j in range2) {
                    val currPoint = currPointProvider(i, j)
                    if (max == null || currPoint.height > max) {
                        max = currPoint.height
                        setVisible(currPoint)
                    }
                }
            }
        }

        private fun visibleDistance(
            currPoint: TreePoint,
            nextPointProvider: (TreePoint) -> TreePoint?
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
    }


    private class TreePoint(position: Position, value: Char) : Cell<TreePoint>(position, value) {
        val height = value.digitToInt()

        var visibleFromW = false
        var visibleFromE = false
        var visibleFromN = false
        var visibleFromS = false
        var visibleDistance = VisibleDistance.empty

        fun isVisible() = visibleFromW || visibleFromE || visibleFromN || visibleFromS

        override fun toString() = "${isVisible()} score: ${visibleDistance.scenicScore}"
    }

    private data class VisibleDistance(val left: Int, val right: Int, val up: Int, val down: Int) {
        val scenicScore = left * right * up * down

        companion object {
            val empty = VisibleDistance(0, 0, 0, 0)
        }
    }
}