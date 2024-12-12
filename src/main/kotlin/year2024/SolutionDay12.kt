package year2024

import common.BaseSolution
import common.Point
import common.PointMap

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {

    override val day = 12
    override val year = 2024

    private val pointMap: PointMap<Connections, GardenPlot>
    private val regions: List<Region>

    init {
        pointMap = PointMap(input(), ::GardenPlot) { Connections() }
        pointMap.points.flatten().forEach {
            it.state.canGoUp = it.up?.value?.id == it.value.id
            it.state.canGoDown = it.down?.value?.id == it.value.id
            it.state.canGoLeft = it.left?.value?.id == it.value.id
            it.state.canGoRight = it.right?.value?.id == it.value.id
        }

        val tmpRegions = mutableListOf<Region>()
        val points = pointMap.points.flatten().toMutableSet()

        while (points.isNotEmpty()) {
            val point = points.first()

            val region = Region(point)
            tmpRegions.add(region)

            region.points().forEach { points.remove(it) }
        }

        regions = tmpRegions.toList()
    }

    override fun task1(): String {
        val result = regions.sumOf { it.size() * it.fences() }
        return result.toString()
    }

    override fun task2(): String {
        val result = regions.sumOf { it.size() * it.sides() }
        return result.toString()
    }

    private class Region(point: Point<Connections, GardenPlot>) {
        // key == point id, value == point
        private val points = mutableMapOf<String, Point<Connections, GardenPlot>>()
        private val fences = mutableSetOf<Fence>()
        private var requiredFence = 0
        private var totalSides = 0

        fun size() = points.size

        fun fences() = requiredFence

        fun sides() = totalSides

        fun points() = points.values

        init {
            createRegion(point)
            calculateSides()
        }

        private fun calculateSides() {
            var sides = 0
            while (fences.isNotEmpty()) {
                val first = fences.first()
                sides += 1
                removeSameSideFences(first)
            }

            totalSides = sides
        }

        private fun removeSameSideFences(fence: Fence) {
            if (!fences.contains(fence))
                return

            fences.remove(fence)
            when (fence.side) {
                Side.Up -> {
                    removeSameSideFences(Fence(fence.rowIdx, fence.colIdx - 1, fence.side))
                    removeSameSideFences(Fence(fence.rowIdx, fence.colIdx + 1, fence.side))
                }

                Side.Down -> {
                    removeSameSideFences(Fence(fence.rowIdx, fence.colIdx - 1, fence.side))
                    removeSameSideFences(Fence(fence.rowIdx, fence.colIdx + 1, fence.side))
                }

                Side.Left -> {
                    removeSameSideFences(Fence(fence.rowIdx - 1, fence.colIdx, fence.side))
                    removeSameSideFences(Fence(fence.rowIdx + 1, fence.colIdx, fence.side))
                }

                Side.Right -> {
                    removeSameSideFences(Fence(fence.rowIdx - 1, fence.colIdx, fence.side))
                    removeSameSideFences(Fence(fence.rowIdx + 1, fence.colIdx, fence.side))
                }
            }
        }

        private fun createRegion(point: Point<Connections, GardenPlot>) {
            if (points.containsKey(point.id))
                return

            val canGoUp = point.state.canGoUp
            val canGoDown = point.state.canGoDown
            val canGoLeft = point.state.canGoLeft
            val canGoRight = point.state.canGoRight

            var directions = 4
            if (canGoUp) directions--
            if (canGoDown) directions--
            if (canGoLeft) directions--
            if (canGoRight) directions--

            if (!canGoUp) fences.add(Fence(point.rowIdx, point.colIdx, Side.Up))
            if (!canGoDown) fences.add(Fence(point.rowIdx, point.colIdx, Side.Down))
            if (!canGoLeft) fences.add(Fence(point.rowIdx, point.colIdx, Side.Left))
            if (!canGoRight) fences.add(Fence(point.rowIdx, point.colIdx, Side.Right))

            requiredFence += directions

            points[point.id] = point

            if (canGoUp && !points.containsKey(point.up!!.id))
                createRegion(point.up!!)

            if (canGoDown && !points.containsKey(point.down!!.id))
                createRegion(point.down!!)

            if (canGoLeft && !points.containsKey(point.left!!.id))
                createRegion(point.left!!)

            if (canGoRight && !points.containsKey(point.right!!.id))
                createRegion(point.right!!)
        }
    }

    private data class Fence(val rowIdx: Int, val colIdx: Int, val side: Side)

    private enum class Side { Up, Down, Left, Right }

    private data class GardenPlot(val id: Char)

    private class Connections {
        var canGoUp = false
        var canGoDown = false
        var canGoLeft = false
        var canGoRight = false

        override fun toString() = "U:$canGoUp D:$canGoDown L:$canGoLeft R:$canGoRight"
    }
}