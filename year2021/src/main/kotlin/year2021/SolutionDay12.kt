package year2021

import common.BaseSolution

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {

    override val day = 12
    
    override fun task1(): String {
        val paths = mutableSetOf<Path>()
        findPaths(Path(startCave, listOf(startCave)), paths, false)
        return paths.size.toString()
    }

    override fun task2(): String {
        val paths = mutableSetOf<Path>()
        findPaths(Path(startCave, listOf(startCave)), paths, true)
        return paths.size.toString()
    }

    private var caves: Set<Cave>
    private val startCave: Cave
    private val endCave: Cave

    init {
        val lines = input().split("\r\n")
        caves = lines.map { line -> line.split("-").map { Cave(it) }.toSet() }.flatten().toSet()

        startCave = caves.firstOrNull { it.isStart() }!!
        endCave = caves.firstOrNull { it.isEnd() }!!

        val tunnels = lines.map { line ->
            val caveConnection = line.split("-").map { name -> caves.first { it.name == name } }
            Tunnel(caveConnection.first(), caveConnection.last())
        }

        tunnels.forEach { tunnel ->
            tunnel.from.neighbours = tunnel.from.neighbours.plus(tunnel.to)
            tunnel.to.neighbours = tunnel.to.neighbours.plus(tunnel.from)
        }
    }

    data class Path(val cave: Cave, val visitedCaves: List<Cave>, val smallCaveVisitedTwice: Boolean = false) {
        fun nextCave(nextCave: Cave, smallCaveVisitedTwice: Boolean? = null) =
            Path(nextCave, visitedCaves.plus(nextCave), smallCaveVisitedTwice ?: this.smallCaveVisitedTwice)
    }

    private fun findPaths(path: Path, paths: MutableSet<Path>, canVisitSmallCaveTwice: Boolean) {
        if (path.cave == endCave) {
            paths.add(path)
            return
        }

        path.cave.neighbours
            .forEach {
                if (it.isBig)
                    findPaths(path.nextCave(it), paths, canVisitSmallCaveTwice)
                else if (canVisitSmallCaveTwice) {
                    val alreadyVisited = path.visitedCaves.count { cave -> cave.name == it.name }
                    if (alreadyVisited == 0)
                        findPaths(path.nextCave(it), paths, canVisitSmallCaveTwice)
                    else if (alreadyVisited == 1 && !it.isStart() && !it.isEnd() && !path.smallCaveVisitedTwice)
                        findPaths(path.nextCave(it, true), paths, canVisitSmallCaveTwice)
                } else {
                    if (!path.visitedCaves.contains(it))
                        findPaths(path.nextCave(it), paths, canVisitSmallCaveTwice)
                }
            }
    }

    data class Tunnel(val from: Cave, val to: Cave)

    data class Cave(val name: String) {
        var neighbours: Set<Cave> = emptySet()
        val isBig = name.uppercase() == name
        fun isStart() = name == "start"
        fun isEnd() = name == "end"

        override fun toString() = "[$name]"
    }
}