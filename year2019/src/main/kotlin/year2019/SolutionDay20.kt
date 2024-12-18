package year2019

import common.BaseSolution
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph
import java.util.LinkedList

fun main() = println(SolutionDay20().result())

class SolutionDay20 : BaseSolution() {

    override val day = 20
    
    private val lines = input().split("\r\n").mapIndexed { row, s ->
        s.mapIndexed { col, c -> Tile(Position(row, col), c) }
    }.flatten()

    override fun task1(): String {
        val tiles = Tiles(lines)
        val result = tiles.findPath()
        return result.toString()
    }

    override fun task2(): String {
        val tiles = Tiles(lines)
        val result = tiles.findPath2()
        return result.toString()
    }

    private data class Tiles(val map: List<Tile>) {
        private val aaTile: Tile
        private val zzTile: Tile

        init {
            for (tile in map) {
                tile.northTile = map.firstOrNull { it.pos.row == tile.pos.row - 1 && it.pos.col == tile.pos.col }
                tile.southTile = map.firstOrNull { it.pos.row == tile.pos.row + 1 && it.pos.col == tile.pos.col }
                tile.westTile = map.firstOrNull { it.pos.row == tile.pos.row && it.pos.col == tile.pos.col - 1 }
                tile.eastTile = map.firstOrNull { it.pos.row == tile.pos.row && it.pos.col == tile.pos.col + 1 }
            }

            map.forEach { it.setName() }

            val walls = map.filter { it.char == '#' }
            val outerXMin = walls.minOf { it.pos.row }
            val outerXMax = walls.maxOf { it.pos.row }
            val outerYMin = walls.minOf { it.pos.col }
            val outerYMax = walls.maxOf { it.pos.col }

            map.forEach { it.setType(outerXMin, outerXMax, outerYMin, outerYMax) }

            aaTile = map.single { it.name == "AA" }
            zzTile = map.single { it.name == "ZZ" }
        }

        fun findPath(): Int {
            val graph = SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

            buildGraph(graph)

            val path = DijkstraShortestPath(graph).getPath(aaTile.uniqueName(), zzTile.uniqueName())
            return path.weight.toInt()
        }

        fun findPath2(): Int {
            val graph = SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

            buildGraph2(graph)

            val path = DijkstraShortestPath(graph).getPath(aaTile.uniqueName(), zzTile.uniqueName())

            return path.weight.toInt()
        }

        private fun isNotEntryOrExitTile(tile: Tile) = tile !== aaTile && tile !== zzTile

        private fun isEntryOrExitTile(tile: Tile) = tile === aaTile || tile === zzTile

        private fun namedTiles() = map.filter { it.name != null }

        private fun portals() = map.filter { it.name != null && isNotEntryOrExitTile(it) }

        private fun buildGraph(graph: SimpleWeightedGraph<String, DefaultWeightedEdge>) {
            // add vertices
            namedTiles().forEach {
                graph.addVertex(it.uniqueName())
                // println("Adding vertex ${it.uniqueName()}")
            }

            // add portal connections
            portals().forEach { entry ->
                val exit = namedTiles().firstOrNull { it.name == entry.name && it !== entry }
                if (exit != null) {
                    graph.addEdge(entry.uniqueName(), exit.uniqueName())
                    graph.setEdgeWeight(entry.uniqueName(), exit.uniqueName(), 1.0)
                    // println("Setting edge ${entry.uniqueName()} <-> ${exit.uniqueName()} with 1")
                }
            }

            // connect regular paths
            val paths = LinkedList(namedTiles().map { Path(it, it, 0, setOf(it)) })
            while (paths.isNotEmpty()) {
                val path = paths.pop()
                path.to.availableMoves().filter { !path.visited.contains(it) }.forEach { nextTile ->
                    if (nextTile.name != null) {
                        graph.addEdge(path.from.uniqueName(), nextTile.uniqueName())
                        graph.setEdgeWeight(path.from.uniqueName(), nextTile.uniqueName(), path.steps + 1.0)
                        // println("Setting edge ${path.from.uniqueName()} <-> ${nextTile.uniqueName()} with ${path.steps + 1}")
                    } else {
                        paths.add(0, Path(path.from, nextTile, path.steps + 1, path.visited.plus(nextTile)))
                    }
                }
            }
        }

        private fun buildGraph2(graph: SimpleWeightedGraph<String, DefaultWeightedEdge>) {
            val maxDepthLevel = 25

            // add vertices
            graph.addVertex(aaTile.uniqueName())
            graph.addVertex(zzTile.uniqueName())

            portals().forEach {
                for (i in 0..maxDepthLevel) {
                    graph.addVertex(it.uniqueName(i))
                    // println("Adding vertex ${it.uniqueName(i)}")
                }
            }

            // add portal connections
            portals().forEach { entry ->
                val exit = portals().firstOrNull { it.name == entry.name && it !== entry }
                if (exit != null) {
                    for (i in 0..maxDepthLevel) {
                        if (entry.type == TileType.INNER_PORTAL && i < 10) {
                            graph.addEdge(entry.uniqueName(i), exit.uniqueName(i + 1))
                            graph.setEdgeWeight(entry.uniqueName(i), exit.uniqueName(i + 1), 1.0)
                            //println("Setting edge ${entry.uniqueName(i)} <-> ${exit.uniqueName(i + 1)} with 1")
                        } else if (entry.type == TileType.OUTER_PORTAL && i > 0) {
                            graph.addEdge(entry.uniqueName(i), exit.uniqueName(i - 1))
                            graph.setEdgeWeight(entry.uniqueName(i), exit.uniqueName(i - 1), 1.0)
                            // println("Setting edge ${entry.uniqueName(i)} <-> ${exit.uniqueName(i - 1)} with 1")
                        }
                    }
                }
            }

            // connect regular paths
            val paths = LinkedList(namedTiles().map { Path(it, it, 0, setOf(it)) })
            while (paths.isNotEmpty()) {
                val path = paths.pop()
                val from = path.from
                path.to.availableMoves().filter { !path.visited.contains(it) }.forEach { to ->
                    if (to.name != null) {
                        if (isNotEntryOrExitTile(from) && isNotEntryOrExitTile(to)) {
                            for (i in 0..maxDepthLevel) {
                                if (from.type == TileType.INNER_PORTAL) {
                                    graph.addEdge(from.uniqueName(i), to.uniqueName(i))
                                    graph.setEdgeWeight(from.uniqueName(i), to.uniqueName(i), path.steps + 1.0)
                                    // println("Setting edge ${from.uniqueName(i)} <-> ${to.uniqueName(i)} with ${path.steps + 1}")
                                } else if (from.type == TileType.OUTER_PORTAL) {
                                    graph.addEdge(from.uniqueName(i), to.uniqueName(i))
                                    graph.setEdgeWeight(from.uniqueName(i), to.uniqueName(i), path.steps + 1.0)
                                    // println("Setting edge ${from.uniqueName(i)} <-> ${to.uniqueName(i)} with ${path.steps + 1}")
                                }
                            }
                        } else {
                            if (isEntryOrExitTile(from) && to.type == TileType.INNER_PORTAL) {
                                graph.addEdge(from.uniqueName(), to.uniqueName(0))
                                graph.setEdgeWeight(from.uniqueName(), to.uniqueName(0), path.steps + 1.0)
                                //println("Setting edge ${from.uniqueName()} <-> ${to.uniqueName(0)} with ${path.steps + 1}")
                            } else if (isEntryOrExitTile(to) && from.type == TileType.INNER_PORTAL) {
                                graph.addEdge(from.uniqueName(0), to.uniqueName())
                                graph.setEdgeWeight(from.uniqueName(0), to.uniqueName(), path.steps + 1.0)
                                //println("Setting edge ${from.uniqueName(0)} <-> ${to.uniqueName()} with ${path.steps + 1}")
                            }
                        }
                    } else {
                        paths.add(0, Path(path.from, to, path.steps + 1, path.visited.plus(to)))
                    }
                }
            }
        }
    }

    private data class Path(val from: Tile, val to: Tile, val steps: Int, val visited: Set<Tile>)

    private data class Tile(val pos: Position, val char: Char) {
        var name: String? = null
        var type = TileType.NONE

        var northTile: Tile? = null
        var southTile: Tile? = null
        var eastTile: Tile? = null
        var westTile: Tile? = null

        override fun toString(): String = "$pos $char $name"

        fun availableMoves() =
            sequenceOf(northTile, southTile, westTile, eastTile).filterNotNull().filter { it.char == '.' }

        fun uniqueName() = "$pos $name"

        fun uniqueName(level: Int) = "$pos $name L$level"

        fun setName() {
            val neighbour1 = neighbourWithLetter()
            if (char != '.' || neighbour1 == null)
                return

            val neighbour2 = neighbour1.neighbourWithLetter()!!

            name = if (neighbour1.pos.row == neighbour2.pos.row) {
                if (neighbour1.pos.col < neighbour2.pos.col) {
                    "${neighbour1.char}${neighbour2.char}"
                } else {
                    "${neighbour2.char}${neighbour1.char}"
                }
            } else if (neighbour1.pos.col == neighbour2.pos.col) {
                if (neighbour1.pos.row < neighbour2.pos.row) {
                    "${neighbour1.char}${neighbour2.char}"
                } else {
                    "${neighbour2.char}${neighbour1.char}"
                }
            } else {
                throw RuntimeException("Cannot execute")
            }
        }

        private fun neighbourWithLetter() = sequenceOf(northTile, southTile, westTile, eastTile)
            .filterNotNull().filter { it.char in 'A'..'Z' }.singleOrNull()

        fun setType(outerXMin: Int, outerXMax: Int, outerYMin: Int, outerYMax: Int) {
            if (name != null) {
                type = if (pos.row == outerXMin || pos.row == outerXMax || pos.col == outerYMin || pos.col == outerYMax)
                    TileType.OUTER_PORTAL
                else
                    TileType.INNER_PORTAL
            }
        }
    }

    private enum class TileType {
        INNER_PORTAL, OUTER_PORTAL, NONE
    }

    private data class Position(val row: Int, val col: Int) {
        override fun toString() = "[$row,$col]"
    }
}