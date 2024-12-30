package year2018

import common.BaseSolution
import common.Position
import kotlin.math.max
import kotlin.math.min

fun main() = println(SolutionDay17().result())

class SolutionDay17 : BaseSolution() {

    override val day = 17

    private val springOfWater: SpringOfWater

    init {
        val xRegex = Regex("x=(\\d+|\\d+..\\d+)(?:$|,)")
        val yRegex = Regex("y=(\\d+|\\d+..\\d+)(?:$|,)")
        val tmpClayPositions = mutableListOf<ClayPosition>()
        input().split("\r\n").forEach { line ->
            val (x) = xRegex.find(line)!!.destructured
            val (y) = yRegex.find(line)!!.destructured
            val xRange = x.split("..").map { it.toInt() }.let { IntRange(it.first(), it.last()) }
            val yRange = y.split("..").map { it.toInt() }.let { IntRange(it.first(), it.last()) }
            tmpClayPositions.add(ClayPosition(xRange, yRange))
        }
        springOfWater = SpringOfWater(ClayPositions(tmpClayPositions))

        springOfWater.spread()
    }

    override fun task1(): String {
        return springOfWater.floodedPositionsCount().toString()
    }

    override fun task2(): String {
        return springOfWater.retainedWaterCount().toString()
    }

    private class SpringOfWater(private val clayPositions: ClayPositions) {
        private val source = Position(500, 0)
        private val floodedPositions = mutableSetOf<Position>()
        private val acceptableYRange = clayPositions.minY..clayPositions.maxY
        private val retainedWater = mutableSetOf<Position>()
        private val spreadVerticallyCache = mutableSetOf<Position>()


        fun spread() = spreadVertically(source)

        fun retainedWaterCount() = retainedWater.size

        fun floodedPositionsCount() = floodedPositions.size

        fun print() {
            val minX = min(floodedPositions.minOf { it.x }, clayPositions.minX)
            val minY = min(floodedPositions.minOf { it.y }, clayPositions.minY)
            val maxX = max(floodedPositions.maxOf { it.x }, clayPositions.maxX)
            val maxY = max(floodedPositions.maxOf { it.y }, clayPositions.maxY)
            for (y in minY..maxY) {
                for (x in minX..maxX) {
                    val pos = Position(x, y)
                    print(if (clayPositions.contains(pos)) '#' else if (floodedPositions.contains(pos)) '.' else ' ')
                }
            }
            println()
        }

        private fun spreadVertically(position: Position) {
            // no point to execute for the same position
            if (spreadVerticallyCache.contains(position))
                return

            if (position.y > acceptableYRange.last)
                return

            spreadVerticallyCache.add(position)

            val belowPositionIsClay = clayPositions.contains(position.s())
            if (belowPositionIsClay) {
                spreadHorizontally(position)
            } else {
                markAsFlooded(position.s())
                spreadVertically(position.s())
            }
        }

        private fun spreadHorizontally(position: Position) {
            var leftBorder: Boolean? = null
            var rightBorder: Boolean? = null

            // spreading left
            var leftPosition = position.w()
            while (leftBorder == null) {
                val leftPositionIsClay = clayPositions.contains(leftPosition)
                val belowLeftPositionIsClay = clayPositions.contains(leftPosition.s())
                val belowLeftPositionIsWater = floodedPositions.contains(leftPosition.s())
                val belowNextLeftPositionIsWaterOrClay =
                    clayPositions.contains(leftPosition.w().s()) || floodedPositions.contains(leftPosition.w().s())
                if (leftPositionIsClay) {
                    // clay on the left
                    leftBorder = true
                } else if (belowLeftPositionIsClay || (belowLeftPositionIsWater && belowNextLeftPositionIsWaterOrClay)) {
                    // continue spread left
                    markAsFlooded(leftPosition)
                    leftPosition = leftPosition.w()
                } else {
                    // no clay on bottom-left, water goes down
                    leftBorder = false
                    markAsFlooded(leftPosition)
                    spreadVertically(leftPosition)
                }
            }

            // spreading right
            var rightPosition = position.e()
            while (rightBorder == null) {
                val rightPositionIsClay = clayPositions.contains(rightPosition)
                val belowRightPositionIsClay = clayPositions.contains(rightPosition.s())
                val belowRightPositionIsWater = floodedPositions.contains(rightPosition.s())
                val belowNextRightPositionIsWaterOrClay =
                    clayPositions.contains(rightPosition.e().s()) || floodedPositions.contains(rightPosition.e().s())
                if (rightPositionIsClay) {
                    // clay on the right
                    rightBorder = true
                } else if (belowRightPositionIsClay || (belowRightPositionIsWater && belowNextRightPositionIsWaterOrClay)) {
                    // continue spread right
                    markAsFlooded(rightPosition)
                    rightPosition = rightPosition.e()
                } else {
                    // no clay on bottom-right, water goes down
                    rightBorder = false
                    markAsFlooded(rightPosition)
                    spreadVertically(rightPosition)
                }
            }

            // spreading up
            if (leftBorder == true && rightBorder == true) {
                // calculating retained water
                var retainedWaterPosition = leftPosition.e()
                while (retainedWaterPosition != rightPosition) {
                    retainedWater.add(retainedWaterPosition)
                    retainedWaterPosition = retainedWaterPosition.e()
                }

                // filling next upper level horizontally
                markAsFlooded(position.n())
                spreadHorizontally(position.n())
            }
        }

        private fun markAsFlooded(position: Position) {
            if (position.y in acceptableYRange)
                floodedPositions.add(position)
        }
    }

    private class ClayPositions(positions: List<ClayPosition>) {
        private val positionsByY: Map<Int, List<IntRange>> // key: Y, value: X range
        val minY: Int = positions.minOf { it.yRange.first }
        val maxY: Int = positions.maxOf { it.yRange.last }
        val minX: Int = positions.minOf { it.xRange.first }
        val maxX: Int = positions.maxOf { it.xRange.last }

        init {
            val tmpPositionsByY = mutableMapOf<Int, MutableList<IntRange>>()
            positions.forEach { position ->
                position.yRange.forEach { y ->
                    tmpPositionsByY.compute(y) { _, curr ->
                        curr?.also { it.add(position.xRange) } ?: mutableListOf(position.xRange)
                    }
                }
            }
            positionsByY = tmpPositionsByY
        }

        fun contains(position: Position) = positionsByY[position.y]?.any { it.contains(position.x) } ?: false
    }

    private data class ClayPosition(val xRange: IntRange, val yRange: IntRange)
}
