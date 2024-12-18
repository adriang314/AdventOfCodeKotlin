package year2022

import common.BaseSolution
import common.Point
import common.PointMap
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {
    override val day = 12
    
    override fun task1(): String {
        val algorithm = BidirectionalDijkstraShortestPath(graph)
        val shortestPath = algorithm.getPath(startPoint!!.id(), endPoint!!.id())
        return shortestPath.length.toString()
    }

    override fun task2(): String {
        val algorithm = BidirectionalDijkstraShortestPath(graph)
        val startPoints = pointMap.points.flatten().filter { it.value.height == 'a'.code }
        val shortestPath = startPoints.mapNotNull { algorithm.getPath(it.id(), endPoint!!.id()) }.minBy { it.length }
        return shortestPath.length.toString()
    }

    private val pointMap: PointMap<Paths, Square>
    private var startPoint: Point<Paths, Square>? = null
    private var endPoint: Point<Paths, Square>? = null
    private val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)

    init {
        pointMap = PointMap(input(), ::Square) { Paths() }
        pointMap.points.flatten().forEach {
            if (it.value.name == 'S') startPoint = it
            if (it.value.name == 'E') endPoint = it

            it.state.canGoUp = (it.up?.value?.height ?: Int.MAX_VALUE) <= it.value.height + 1
            it.state.canGoDown = (it.down?.value?.height ?: Int.MAX_VALUE) <= it.value.height + 1
            it.state.canGoLeft = (it.left?.value?.height ?: Int.MAX_VALUE) <= it.value.height + 1
            it.state.canGoRight = (it.right?.value?.height ?: Int.MAX_VALUE) <= it.value.height + 1

            graph.addVertex(it.id())
        }

        pointMap.points.flatten().forEach {
            if (it.state.canGoLeft) graph.addEdge(it.id(), it.left!!.id())
            if (it.state.canGoRight) graph.addEdge(it.id(), it.right!!.id())
            if (it.state.canGoUp) graph.addEdge(it.id(), it.up!!.id())
            if (it.state.canGoDown) graph.addEdge(it.id(), it.down!!.id())
        }
    }

    private fun Point<Paths, Square>.id(): String = "[${this.rowIdx},${this.colIdx}]"

    data class Square(val name: Char) {
        val height: Int = when (name) {
            'S' -> 'a'.code
            'E' -> 'z'.code
            else -> name.code
        }
    }

    class Paths {
        var canGoUp = false
        var canGoDown = false
        var canGoLeft = false
        var canGoRight = false
    }
}