package solution

import kotlin.math.abs

class SolutionDay11 : BaseSolution() {

    override val day = 11

    override fun task1(): String {
        val galaxies = getGalaxies(1L)
        val galaxyPairs = getGalaxyPairs(galaxies)
        val sumOfDistance = galaxyPairs.sumOf { it.distance }
        return sumOfDistance.toString()
    }

    override fun task2(): String {
        val galaxies = getGalaxies(999_999L)
        val galaxyPairs = getGalaxyPairs(galaxies)
        val sumOfDistance = galaxyPairs.sumOf { it.distance }
        return sumOfDistance.toString()
    }

    private fun getGalaxyPairs(galaxies: List<Galaxy>): List<GalaxyPair> {
        val pairs = mutableListOf<GalaxyPair>()
        galaxies.forEachIndexed { index1, galaxy1 ->
            galaxies.forEachIndexed { index2, galaxy2 ->
                if (index1 < index2)
                    pairs.add(GalaxyPair(galaxy1, galaxy2))
            }
        }
        return pairs
    }

    private fun getGalaxies(expansionSize: Long): List<Galaxy> {
        var emptySpaces = 0L
        return getUniverse(expansionSize).universe.mapIndexed { index, space ->
            if (space is EmptySpace) {
                emptySpaces++
            }

            val rowIdx = (expansionSize * emptySpaces) + (index - emptySpaces)
            space.galaxyAt.map { Galaxy(rowIdx, it) }
        }.flatten()
    }

    private fun getUniverse(expansionSize: Long): Universe {
        val rawLines = input().split("\r\n", "\n")
        return Universe(rawLines.map { Space(it, expansionSize) }.toMutableList())
    }

    private data class GalaxyPair(val g1: Galaxy, val g2: Galaxy) {
        val distance: Long = abs(g2.rowIdx - g1.rowIdx) + abs(g2.colIdx - g1.colIdx)
    }

    private data class Galaxy(val rowIdx: Long, val colIdx: Long)

    private class EmptySpace : Space(".", 0L)

    private open class Space(private var space: String, private val expansionSize: Long) {
        val galaxyAt = mutableListOf<Long>()
        val size: Int = space.length
        val horizontalExpansionAt = mutableListOf<Long>()

        init {
            findGalaxies()
        }

        fun isEmpty() = galaxyAt.isEmpty()

        private fun Char.isGalaxy() = this == '#'

        fun findGalaxies() {
            galaxyAt.clear()
            space.forEachIndexed { index, point ->
                if (point.isGalaxy()) {
                    val horizontalExpansions = horizontalExpansionAt.count { it < index }
                    galaxyAt.add(index + (horizontalExpansions * expansionSize))
                }
            }
        }
    }

    private class Universe(val universe: MutableList<Space>) {

        init {
            val verticalExpansions = mutableListOf<Int>()
            universe.forEachIndexed { index, space ->
                if (space.isEmpty()) {
                    verticalExpansions.add(index)
                }
            }

            val allGalaxies = universe.map { it.galaxyAt }.flatten().distinct().sorted()
            val horizontalExpansions = mutableListOf<Long>()
            (0L..<universe.first.size).forEach {
                if (!allGalaxies.contains(it))
                    horizontalExpansions.add(it)
            }

            verticalExpansions.reversed().forEach {
                universe.add(it, EmptySpace())
            }

            horizontalExpansions.reversed().forEach {
                universe.forEach { line ->
                    line.horizontalExpansionAt.add(it)
                }
            }

            universe.forEach { it.findGalaxies() }
        }
    }
}