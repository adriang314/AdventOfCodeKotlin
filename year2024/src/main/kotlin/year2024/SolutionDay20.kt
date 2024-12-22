package year2024

import common.*
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import java.util.*
import kotlin.math.absoluteValue

fun main() = println(SolutionDay20().result())

class SolutionDay20 : BaseSolution() {

    override val day = 20

    private val map: Grid<Point>
    private lateinit var startPoint: Point
    private lateinit var endPoint: Point
    private val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    private val algorithm: BidirectionalDijkstraShortestPath<String, DefaultEdge>
    private val pathPlaces: Map<Point, Int> // value = length from start point
    private val pathLength: Int

    init {
        map = Grid(input()) { c, position -> Point(position, c) }.also { grid ->
            grid.cells.values.forEach {
                if (it.value == 'S') startPoint = it
                if (it.value == 'E') endPoint = it

                it.canGoN = it.isOpenSpace() && grid.getCell(it.position.n())?.isOpenSpace() ?: false
                it.canGoS = it.isOpenSpace() && grid.getCell(it.position.s())?.isOpenSpace() ?: false
                it.canGoW = it.isOpenSpace() && grid.getCell(it.position.w())?.isOpenSpace() ?: false
                it.canGoE = it.isOpenSpace() && grid.getCell(it.position.e())?.isOpenSpace() ?: false

                graph.addVertex(it.position.toString())
            }

            grid.cells.values.forEach {
                if (it.canGoN) graph.addEdge(it.position, it.position.n())
                if (it.canGoS) graph.addEdge(it.position, it.position.s())
                if (it.canGoW) graph.addEdge(it.position, it.position.w())
                if (it.canGoE) graph.addEdge(it.position, it.position.e())
            }
        }

        algorithm = BidirectionalDijkstraShortestPath(graph)
        val shortestPath = algorithm.getPath(startPoint.position, endPoint.position)!!
        pathPlaces = shortestPath.vertexList.mapIndexed { idx, position -> map.getCell(position)!! to idx }.toMap()
        pathLength = shortestPath.length
    }

    override fun task1(): String {
        val res = getBestCheatsSum(2)
        return res.toString()
    }

    override fun task2(): String {
        val res = getBestCheatsSum(20)
        return res.toString()
    }

    private fun getBestCheatsSum(length: Int): Long {
        val startCheatPoints = pathPlaces.keys
        val bestCheats = mutableMapOf<Long, Long>() // key = saved time, count
        startCheatPoints.forEach { cheatStartPoint ->
            val endCheatPoints = getEndCheatPoints(cheatStartPoint, length)
            val lengthFromStartPointToStartCheatPoint = pathPlaces[cheatStartPoint]!!

            endCheatPoints.forEach { endCheatPoint ->
                val lengthFromEndCheatPointToEndPoint = pathLength - pathPlaces[endCheatPoint]!!
                var savedTime = pathLength.toLong()
                savedTime -= lengthFromStartPointToStartCheatPoint
                savedTime -= lengthFromEndCheatPointToEndPoint
                savedTime -= cheatStartPoint.distanceTo(endCheatPoint)

                if (savedTime >= 100) {
                    bestCheats.compute(savedTime) { _, v -> if (v == null) 1L else v + 1L }
                }
            }
        }

        return bestCheats.values.sum()
    }

    private fun getEndCheatPoints(startCheatPoint: Point, length: Int): List<Point> {
        val result = LinkedList<Point>()
        for (yShift in -length..length) {
            for (xShift in -length..length) {
                if (yShift == 0 && xShift == 0)
                    continue
                if (yShift.absoluteValue + xShift.absoluteValue > length)
                    continue

                map.getCell(startCheatPoint.position.shift(xShift, yShift))?.let { newPlace ->
                    if (newPlace.isOpenSpace()) {
                        result.add(newPlace)
                    }
                }
            }
        }

        return result
    }

    private class Point(position: Position, c: Char) : Cell(position, c) {
        fun isOpenSpace() = value != '#'
    }
}