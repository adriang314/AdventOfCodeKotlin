package year2024

import common.*

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {

    override val day = 12

    private val garden: Grid<GardenPoint> = Grid(input()) { value, position -> GardenPoint(position, value) }
    private val regions: List<Region>

    init {
        val tmpRegions = mutableListOf<Region>()
        val gardenCells = garden.cells.toMutableList()

        while (gardenCells.isNotEmpty()) {
            val gardenCell = gardenCells.first()

            val region = Region(gardenCell)
            tmpRegions.add(region)

            region.gardenCells().forEach { gardenCells.remove(it) }
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

    private class Region(innerGardenPoint: GardenPoint) {
        private val gardenCells = mutableMapOf<Position, GardenPoint>()
        private val fences = mutableSetOf<Fence>()
        private var requiredFence = 0
        private var totalSides = 0

        fun size() = gardenCells.size

        fun fences() = requiredFence

        fun sides() = totalSides

        fun gardenCells() = gardenCells.values

        init {
            createRegion(innerGardenPoint)
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
                Side.Up, Side.Down -> {
                    removeSameSideFences(Fence(fence.position.w(), fence.side))
                    removeSameSideFences(Fence(fence.position.e(), fence.side))
                }

                Side.Left, Side.Right -> {
                    removeSameSideFences(Fence(fence.position.n(), fence.side))
                    removeSameSideFences(Fence(fence.position.s(), fence.side))
                }
            }
        }

        private fun createRegion(gardenPoint: GardenPoint) {
            if (gardenCells.containsKey(gardenPoint.position))
                return

            var directions = 4
            if (gardenPoint.canGoN()) directions--
            if (gardenPoint.canGoS()) directions--
            if (gardenPoint.canGoE()) directions--
            if (gardenPoint.canGoW()) directions--

            if (!gardenPoint.canGoN()) fences.add(Fence(gardenPoint.position, Side.Up))
            if (!gardenPoint.canGoS()) fences.add(Fence(gardenPoint.position, Side.Down))
            if (!gardenPoint.canGoE()) fences.add(Fence(gardenPoint.position, Side.Left))
            if (!gardenPoint.canGoW()) fences.add(Fence(gardenPoint.position, Side.Right))

            requiredFence += directions

            gardenCells[gardenPoint.position] = gardenPoint

            if (gardenPoint.canGoN() && !gardenCells.containsKey(gardenPoint.n!!.position))
                createRegion(gardenPoint.n!!)

            if (gardenPoint.canGoS() && !gardenCells.containsKey(gardenPoint.s!!.position))
                createRegion(gardenPoint.s!!)

            if (gardenPoint.canGoW() && !gardenCells.containsKey(gardenPoint.w!!.position))
                createRegion(gardenPoint.w!!)

            if (gardenPoint.canGoE() && !gardenCells.containsKey(gardenPoint.e!!.position))
                createRegion(gardenPoint.e!!)
        }
    }

    private data class Fence(val position: Position, val side: Side)

    private enum class Side { Up, Down, Left, Right }

    private class GardenPoint(position: Position, value: Char) : Cell<GardenPoint>(position, value) {

        override fun canGoN() = n?.value == value
        override fun canGoS() = s?.value == value
        override fun canGoW() = w?.value == value
        override fun canGoE() = e?.value == value

    }
}