package year2021

import common.BaseSolution
import common.Point
import common.PointMap
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleDirectedWeightedGraph

private typealias DirectedGraph = SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>
private typealias Vertex = Point<SolutionDay15.State, Int>

fun main() = println(SolutionDay15().result())

class SolutionDay15 : BaseSolution() {

    override val day = 15
    
    override fun task1(): String {
        val algorithm = DijkstraShortestPath(graph)
        val shortestPath = algorithm.getPath(startPoint!!.id, endPoint!!.id)
        return shortestPath.weight.toInt().toString()
    }

    override fun task2(): String {
        val algorithm = DijkstraShortestPath(extendedGraph)
        val shortestPath = algorithm.getPath(extendedStartPoint!!.id, extendedEndPoint!!.id)
        return shortestPath.weight.toInt().toString()
    }

    private val points: List<Vertex>
    private val extendedPoints: List<Vertex>
    private var startPoint: Vertex? = null
    private var extendedStartPoint: Vertex? = null
    private var endPoint: Vertex? = null
    private var extendedEndPoint: Vertex? = null
    private val graph = DirectedGraph(DefaultWeightedEdge::class.java)
    private val extendedGraph = DirectedGraph(DefaultWeightedEdge::class.java)

    init {
        val input = input()
        val extendedInput = extendInput(input)
        val pointMap = PointMap(input, Char::digitToInt) { State() }
        val extendedPointMap = PointMap(extendedInput, Char::digitToInt) { State() }

        points = pointMap.points.flatten()
        extendedPoints = extendedPointMap.points.flatten()

        addVertices(graph, points, pointMap,
            { point -> this.startPoint = point },
            { point -> this.endPoint = point })
        addVertices(extendedGraph, extendedPoints, extendedPointMap,
            { point -> this.extendedStartPoint = point },
            { point -> this.extendedEndPoint = point })
        addEdges(graph, points)
        addEdges(extendedGraph, extendedPoints)
    }

    private fun extendInput(input: String): String {
        // extend columns
        val extendedColumns = input.split("\r\n").map { line ->
            val builder = StringBuilder(line.length * 5).append(line)
            repeat(4) { iteration -> builder.append(line.map { newDigit(it, iteration) }.joinToString("")) }
            builder.toString()
        }

        // extend rows
        val extendedRows = mutableListOf<String>()
        repeat(4) { iteration ->
            extendedColumns.map { line -> extendedRows.add(line.map { newDigit(it, iteration) }.joinToString("")) }
        }

        return extendedColumns.plus(extendedRows).joinToString("\r\n")
    }

    private fun newDigit(digit: Char, iteration: Int): String {
        var newDigit = digit.digitToInt() + iteration + 1
        if (newDigit > 9) newDigit -= 9
        return newDigit.toString()
    }

    private fun addVertices(
        graph: DirectedGraph,
        points: List<Vertex>,
        pointMap: PointMap<State, Int>,
        setStartPoint: (Vertex) -> Unit,
        setEndPoint: (Vertex) -> Unit,
    ) {
        points.forEach {
            if (it.rowIdx == 0 && it.colIdx == 0)
                setStartPoint(it)
            if (it.rowIdx == pointMap.length - 1 && it.colIdx == pointMap.height - 1)
                setEndPoint(it)
            graph.addVertex(it.id)
        }
    }

    private fun addEdges(graph: DirectedGraph, points: List<Vertex>) {
        points.forEach {
            if (it.left != null) {
                graph.addEdge(it.id, it.left!!.id)
                graph.setEdgeWeight(it.id, it.left!!.id, it.left!!.value.toDouble())
            }
            if (it.right != null) {
                graph.addEdge(it.id, it.right!!.id)
                graph.setEdgeWeight(it.id, it.right!!.id, it.right!!.value.toDouble())
            }
            if (it.up != null) {
                graph.addEdge(it.id, it.up!!.id)
                graph.setEdgeWeight(it.id, it.up!!.id, it.up!!.value.toDouble())
            }
            if (it.down != null) {
                graph.addEdge(it.id, it.down!!.id)
                graph.setEdgeWeight(it.id, it.down!!.id, it.down!!.value.toDouble())
            }
        }
    }

    data class State(val empty: String = "")
}