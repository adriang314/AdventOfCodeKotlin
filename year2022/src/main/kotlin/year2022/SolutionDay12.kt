package year2022

import common.*
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {
    override val day = 12

    override fun task1(): String {
        val algorithm = BidirectionalDijkstraShortestPath(graph)
        val shortestPath = algorithm.getPath(startPoint.position, endPoint.position)!!
        return shortestPath.length.toString()
    }

    override fun task2(): String {
        val algorithm = BidirectionalDijkstraShortestPath(graph)
        val startPoints = map.cells.filter { it.height == 'a'.code }
        val shortestPath =
            startPoints.mapNotNull { algorithm.getPath(it.position, endPoint.position) }.minBy { it.length }
        return shortestPath.length.toString()
    }

    private val map: Grid<Point>
    private lateinit var startPoint: Point
    private lateinit var endPoint: Point
    private val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)

    init {
        map = Grid(input()) { value, position -> Point(position, value) }.also { grid ->
            grid.cells.forEach {
                if (it.value == 'S') startPoint = it
                if (it.value == 'E') endPoint = it

                it.canGoN = (grid.getCell(it.position.n())?.height ?: Int.MAX_VALUE) <= it.height + 1
                it.canGoS = (grid.getCell(it.position.s())?.height ?: Int.MAX_VALUE) <= it.height + 1
                it.canGoW = (grid.getCell(it.position.w())?.height ?: Int.MAX_VALUE) <= it.height + 1
                it.canGoE = (grid.getCell(it.position.e())?.height ?: Int.MAX_VALUE) <= it.height + 1

                graph.addVertex(it.position)
            }

            grid.cells.forEach {
                if (it.canGoN) graph.addEdge(it.position, it.position.n())
                if (it.canGoS) graph.addEdge(it.position, it.position.s())
                if (it.canGoW) graph.addEdge(it.position, it.position.w())
                if (it.canGoE) graph.addEdge(it.position, it.position.e())
            }
        }
    }

    private class Point(position: Position, value: Char) : Cell<Point>(position, value) {
        val height: Int = when (value) {
            'S' -> 'a'.code
            'E' -> 'z'.code
            else -> value.code
        }
    }
}