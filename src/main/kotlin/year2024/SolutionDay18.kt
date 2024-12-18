package year2024

import common.BaseSolution
import common.Point
import common.PointMap
import common.Position
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {

    override val day = 18
    override val year = 2024

    private val bytePositions: List<Position> = input().split("\r\n")
        .map { line -> line.split(",").let { Position(it[0].toInt(), it[1].toInt()) } }

    private lateinit var pointMap: PointMap<Paths, Square>
    private lateinit var startPoint: Point<Paths, Square>
    private lateinit var endPoint: Point<Paths, Square>
    private lateinit var graph: SimpleDirectedGraph<String, DefaultEdge>
    private val mapSize = 70

    override fun task1(): String {
        initialize(1024)
        val algorithm = BidirectionalDijkstraShortestPath(graph)
        val shortestPath = algorithm.getPath(startPoint.id, endPoint.id)
        return shortestPath.length.toString()
    }

    override fun task2(): String {
        (1025..bytePositions.size).forEach { bytesToTake ->
            initialize(bytesToTake)
            val algorithm = BidirectionalDijkstraShortestPath(graph)
            algorithm.getPath(startPoint.id, endPoint.id) ?: return bytePositions[bytesToTake - 1].toString()
        }

        throw RuntimeException("Not found")
    }

    private fun initialize(bytesToUse: Int) {
        val bytes = bytePositions.take(bytesToUse)

        val map = (0..mapSize).joinToString("\r\n") { y ->
            (0..mapSize).map { x ->
                if (bytes.contains(Position(x, y))) '#' else '.'
            }.joinToString("")
        }

        graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)

        pointMap = PointMap(map, ::Square) { Paths() }
        pointMap.points.flatten().forEach {
            if (it.rowIdx == 0 && it.colIdx == 0) startPoint = it
            if (it.rowIdx == mapSize && it.colIdx == mapSize) endPoint = it

            it.state.canGoUp = it.up?.value?.name == '.'
            it.state.canGoDown = it.down?.value?.name == '.'
            it.state.canGoLeft = it.left?.value?.name == '.'
            it.state.canGoRight = it.right?.value?.name == '.'

            graph.addVertex(it.id)
        }

        pointMap.points.flatten().forEach {
            if (it.state.canGoLeft) graph.addEdge(it.id, it.left!!.id)
            if (it.state.canGoRight) graph.addEdge(it.id, it.right!!.id)
            if (it.state.canGoUp) graph.addEdge(it.id, it.up!!.id)
            if (it.state.canGoDown) graph.addEdge(it.id, it.down!!.id)
        }
    }

    data class Square(val name: Char)

    class Paths {
        var canGoUp = false
        var canGoDown = false
        var canGoLeft = false
        var canGoRight = false
    }
}