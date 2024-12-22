package year2024

import common.*

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {

    override val day = 12

    private val garden: Grid<GardenCell> = Grid(input()) { c, position -> GardenCell(position, c) }
        .also { garden ->
            garden.cells.values.forEach {
                it.canGoN = it.n?.c == it.c
                it.canGoS = it.s?.c == it.c
                it.canGoW = it.w?.c == it.c
                it.canGoE = it.e?.c == it.c
            }
        }
    private val regions: List<Region>

    init {
        val tmpRegions = mutableListOf<Region>()
        val gardenCells = garden.cells.values.toMutableList()

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

    private class Region(innerGardenCell: GardenCell) {
        private val gardenCells = mutableMapOf<Position, GardenCell>()
        private val fences = mutableSetOf<Fence>()
        private var requiredFence = 0
        private var totalSides = 0

        fun size() = gardenCells.size

        fun fences() = requiredFence

        fun sides() = totalSides

        fun gardenCells() = gardenCells.values

        init {
            createRegion(innerGardenCell)
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

        private fun createRegion(gardenCell: GardenCell) {
            if (gardenCells.containsKey(gardenCell.position))
                return

            var directions = 4
            if (gardenCell.canGoN) directions--
            if (gardenCell.canGoS) directions--
            if (gardenCell.canGoE) directions--
            if (gardenCell.canGoW) directions--

            if (!gardenCell.canGoN) fences.add(Fence(gardenCell.position, Side.Up))
            if (!gardenCell.canGoS) fences.add(Fence(gardenCell.position, Side.Down))
            if (!gardenCell.canGoE) fences.add(Fence(gardenCell.position, Side.Left))
            if (!gardenCell.canGoW) fences.add(Fence(gardenCell.position, Side.Right))

            requiredFence += directions

            gardenCells[gardenCell.position] = gardenCell

            if (gardenCell.canGoN && !gardenCells.containsKey(gardenCell.n!!.position))
                createRegion(gardenCell.n as GardenCell)

            if (gardenCell.canGoS && !gardenCells.containsKey(gardenCell.s!!.position))
                createRegion(gardenCell.s as GardenCell)

            if (gardenCell.canGoW && !gardenCells.containsKey(gardenCell.w!!.position))
                createRegion(gardenCell.w as GardenCell)

            if (gardenCell.canGoE && !gardenCells.containsKey(gardenCell.e!!.position))
                createRegion(gardenCell.e as GardenCell)
        }
    }

    private data class Fence(val position: Position, val side: Side)

    private enum class Side { Up, Down, Left, Right }

    private class GardenCell(position: Position, c: Char) : Cell(position, c)
}