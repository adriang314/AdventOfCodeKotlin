package year2023

import kotlin.math.abs

fun main() {
    println("${SolutionDay11()}")
}

class SolutionDay11 : BaseSolution() {

    override val day = 11

    override fun task1(): String {
        val universeMap = input()
        val universe = Universe(universeMap, 1L)
        val galaxyPairs = universe.galaxyPairs()
        val sumOfDistance = galaxyPairs.sumOf { it.distance }
        return sumOfDistance.toString()
    }

    override fun task2(): String {
        val universeMap = input()
        val universe = Universe(universeMap, 999_999L)
        val galaxyPairs = universe.galaxyPairs()
        val sumOfDistance = galaxyPairs.sumOf { it.distance }
        return sumOfDistance.toString()
    }

    private data class GalaxyPair(val g1: Galaxy, val g2: Galaxy) {
        val distance: Long = abs(g2.rowIdx - g1.rowIdx) + abs(g2.colIdx - g1.colIdx)
    }

    private data class Galaxy(val rowIdx: Long, val colIdx: Long)

    private class EmptyMilkyWay : MilkyWay()

    private open class MilkyWay(private var objects: String = "", private val expansionSize: Long = 0) {
        val galaxyLocations = mutableListOf<Long>()
        val size: Int = objects.length
        var expansionLocations = emptyList<Long>()

        init {
            findGalaxies()
        }

        fun isEmpty() = galaxyLocations.isEmpty()

        private fun Char.isGalaxy() = this == '#'

        fun findGalaxies() {
            galaxyLocations.clear()
            objects.forEachIndexed { location, obj ->
                if (obj.isGalaxy()) {
                    val expansionCount = expansionLocations.count { it < location }
                    galaxyLocations.add(location + (expansionCount * this.expansionSize))
                }
            }
        }
    }

    private class Universe(universeMap: String, expansionSize: Long) {

        private val galaxies: List<Galaxy>

        init {
            val milkyWays = universeMap.split("\r\n", "\n").map { MilkyWay(it, expansionSize) }.toMutableList()

            // find empty milky ways
            val emptyMilkyWayLocations = mutableListOf<Int>()
            milkyWays.forEachIndexed { location, milkyWay ->
                if (milkyWay.isEmpty()) {
                    emptyMilkyWayLocations.add(location)
                }
            }

            // find locations in milky ways for horizontal expansion
            val galaxyLocations = milkyWays.map { it.galaxyLocations }.flatten().distinct().sorted()
            val milkyWayExpansionLocations = mutableListOf<Long>()
            (0L..<milkyWays.first.size).forEach { location ->
                if (!galaxyLocations.contains(location))
                    milkyWayExpansionLocations.add(location)
            }

            // extend universe
            emptyMilkyWayLocations.reversed().forEach { location ->
                milkyWays.add(location, EmptyMilkyWay())
            }
            milkyWays.forEach { milkyWay ->
                milkyWay.expansionLocations = milkyWayExpansionLocations
            }

            // find galaxies at new universe
            milkyWays.forEach { it.findGalaxies() }

            // build galaxies
            var emptySpaces = 0L
            galaxies = milkyWays.mapIndexed { index, space ->
                if (space is EmptyMilkyWay) {
                    emptySpaces++
                }

                val rowIdx = (expansionSize * emptySpaces) + (index - emptySpaces)
                space.galaxyLocations.map { Galaxy(rowIdx, it) }
            }.flatten()
        }

        fun galaxyPairs(): List<GalaxyPair> {
            val pairs = mutableListOf<GalaxyPair>()
            galaxies.forEachIndexed { location1, galaxy1 ->
                galaxies.forEachIndexed { location2, galaxy2 ->
                    if (location1 < location2)
                        pairs.add(GalaxyPair(galaxy1, galaxy2))
                }
            }
            return pairs
        }
    }
}