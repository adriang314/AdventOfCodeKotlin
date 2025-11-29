package year2015

import common.BaseSolution

fun main() = println(SolutionDay09().result())

class SolutionDay09 : BaseSolution() {

    override val day = 9

    private val distances = input().split("\r\n").map {
        val parts = it.split(" = ")
        val locations = parts[0].split(" to ")
        Distance(locations[0], locations[1], parts[1].toInt())
    }

    override fun task1(): String {
        val map = Map(distances)
        val shortestRoute = map.findShortestPath()
        return shortestRoute.toString()
    }

    override fun task2(): String {
        val map = Map(distances)
        val shortestRoute = map.findLongestPath()
        return shortestRoute.toString()
    }

    private data class Distance(val location1: String, val location2: String, val distance: Int)

    private data class Path(val locations: List<String>, val totalDistance: Int)

    private class Map(val distances: List<Distance>) {

        private val locations = distances.flatMap { listOf(it.location1, it.location2) }.toSet()

        fun findShortestPath(): Int = locations.map { startLocation -> findAllPaths(startLocation).minBy { it.totalDistance } }.minBy { it.totalDistance }.totalDistance

        fun findLongestPath(): Int = locations.map { startLocation -> findAllPaths(startLocation).maxBy { it.totalDistance } }.maxBy { it.totalDistance }.totalDistance

        private fun findAllPaths(startLocation: String): List<Path> {
            val results = mutableListOf<Path>()
            fun visit(location: String, visited: Set<String>, currentDistance: Int) {
                if (visited.size == locations.size) {
                    results.add(Path(visited.toList(), currentDistance))
                    return
                }
                val nextDistances = distances.filter {
                    (it.location1 == location && !visited.contains(it.location2)) || (it.location2 == location && !visited.contains(it.location1))
                }
                nextDistances.forEach { next ->
                    val nextLocation = if (next.location1 == location) next.location2 else next.location1
                    visit(nextLocation, visited + nextLocation, currentDistance + next.distance)
                }
            }
            visit(startLocation, setOf(startLocation), 0)
            return results
        }
    }
}