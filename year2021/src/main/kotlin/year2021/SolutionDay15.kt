package year2021

import common.*
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleDirectedWeightedGraph

fun main() = println(SolutionDay15().result())

class SolutionDay15 : BaseSolution() {

    override val day = 15

    override fun task1(): String {
        val algorithm = DijkstraShortestPath(graph)
        val shortestPath = algorithm.getPath(startPoint.position, endPoint.position)!!
        return shortestPath.weight.toInt().toString()
    }

    override fun task2(): String {
        val algorithm = DijkstraShortestPath(extGraph)
        val shortestPath = algorithm.getPath(extStartPoint.position, extEndPoint.position)!!
        return shortestPath.weight.toInt().toString()
    }

    private val points: List<Point>
    private val extPoints: List<Point>
    private lateinit var startPoint: Point
    private lateinit var extStartPoint: Point
    private lateinit var endPoint: Point
    private lateinit var extEndPoint: Point
    private val graph = SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
    private val extGraph = SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

    init {
        val pointMap = Grid(input()) { c, position -> Point(position, c) }
        val extendedPointMap = Grid(extendInput(input())) { c, position -> Point(position, c) }

        points = pointMap.values.toList()
        extPoints = extendedPointMap.values.toList()

        addVertices(
            graph, points, pointMap,
            { point -> this.startPoint = point },
            { point -> this.endPoint = point })
        addVertices(
            extGraph, extPoints, extendedPointMap,
            { point -> this.extStartPoint = point },
            { point -> this.extEndPoint = point })
        addEdges(graph, points)
        addEdges(extGraph, extPoints)
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
        graph: SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>,
        points: List<Point>,
        pointMap: Grid<Point>,
        setStartPoint: (Point) -> Unit,
        setEndPoint: (Point) -> Unit,
    ) {
        points.forEach { point ->
            val position = point.position
            if (position.x == 0 && position.y == 0)
                setStartPoint(point)
            if (position.x == pointMap.width - 1 && position.y == pointMap.height - 1)
                setEndPoint(point)
            graph.addVertex(position)
        }
    }

    private fun addEdges(graph: SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>, points: List<Point>) {
        points.forEach { point ->
            val position = point.position
            point.w?.let {
                graph.addEdge(position, position.w())
                graph.setEdgeWeight(position, position.w(), (it as Point).riskLvl)
            }
            point.e?.let {
                graph.addEdge(position, position.e())
                graph.setEdgeWeight(position, position.e(), (it as Point).riskLvl)
            }
            point.n?.let {
                graph.addEdge(position, position.n())
                graph.setEdgeWeight(position, position.n(), (it as Point).riskLvl)
            }
            point.s?.let {
                graph.addEdge(position, position.s())
                graph.setEdgeWeight(position, position.s(), (it as Point).riskLvl)
            }
        }
    }

    private class Point(position: Position, value: Char) : Cell(position, value) {
        val riskLvl = value.digitToInt().toDouble()
    }
}