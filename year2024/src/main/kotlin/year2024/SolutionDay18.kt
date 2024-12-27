package year2024

import common.*
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {

    override val day = 18

    private val bytePositions: List<Position> = input().split("\r\n")
        .map { line -> line.split(",").let { Position(it[0].toInt(), it[1].toInt()) } }

    private val mapSize = 70

    override fun task1(): String {
        val shortestPathLength = findShortestPathLength(1024)!!
        return shortestPathLength.toString()
    }

    override fun task2(): String {
        (1025..bytePositions.size).forEach { bytesToTake ->
            findShortestPathLength(bytesToTake) ?: return bytePositions[bytesToTake - 1].toString()
        }
        throw RuntimeException("Not found")
    }

    private fun findShortestPathLength(bytesToUse: Int): Int? {
        val bytes = bytePositions.take(bytesToUse)
        lateinit var startPoint: Point
        lateinit var endPoint: Point
        val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)

        val gridBuilder = Grid.Builder(0..mapSize, 0..mapSize) { position ->
            if (bytes.contains(position)) '#' else '.'
        }

        Grid(gridBuilder) { c, position -> Point(position, c) }.also { grid ->
            grid.cells.forEach {
                if (it.position == Position(0, 0)) startPoint = it
                if (it.position == Position(mapSize, mapSize)) endPoint = it

                it.canGoN = it.n?.value == '.'
                it.canGoS = it.s?.value == '.'
                it.canGoW = it.w?.value == '.'
                it.canGoE = it.e?.value == '.'

                graph.addVertex(it.position.toString())
            }

            grid.cells.forEach {
                if (it.canGoN) graph.addEdge(it.position, it.position.n())
                if (it.canGoS) graph.addEdge(it.position, it.position.s())
                if (it.canGoW) graph.addEdge(it.position, it.position.w())
                if (it.canGoE) graph.addEdge(it.position, it.position.e())
            }
        }

        val algorithm = BidirectionalDijkstraShortestPath(graph)
        return algorithm.getPath(startPoint.position, endPoint.position)?.length
    }

    private class Point(position: Position, c: Char) : Cell<Point>(position, c)
}