package year2024

import common.BaseSolution
import common.Point
import common.PointMap
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import java.util.*
import kotlin.math.absoluteValue

fun main() = println(SolutionDay20().result())

class SolutionDay20 : BaseSolution() {

    override val day = 20

    private val pointMap: PointMap<Paths, Square>
    private lateinit var startPoint: Point<Paths, Square>
    private lateinit var endPoint: Point<Paths, Square>
    private val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    private val algorithm: BidirectionalDijkstraShortestPath<String, DefaultEdge>
    private val pathPointsMap: Map<Point<Paths, Square>, Int> // value = length from start point
    private val pathLength: Int

    init {
        pointMap = PointMap(input(), ::Square) { Paths() }
        pointMap.points.flatten().forEach {
            if (it.value.name == 'S') startPoint = it
            if (it.value.name == 'E') endPoint = it

            it.state.canGoUp = it.value.isOpenSpace() && it.up?.value?.isOpenSpace() ?: false
            it.state.canGoDown = it.value.isOpenSpace() && it.down?.value?.isOpenSpace() ?: false
            it.state.canGoLeft = it.value.isOpenSpace() && it.left?.value?.isOpenSpace() ?: false
            it.state.canGoRight = it.value.isOpenSpace() && it.right?.value?.isOpenSpace() ?: false

            graph.addVertex(it.id)
        }

        pointMap.points.flatten().forEach {
            if (it.state.canGoLeft) graph.addEdge(it.id, it.left!!.id)
            if (it.state.canGoRight) graph.addEdge(it.id, it.right!!.id)
            if (it.state.canGoUp) graph.addEdge(it.id, it.up!!.id)
            if (it.state.canGoDown) graph.addEdge(it.id, it.down!!.id)
        }

        algorithm = BidirectionalDijkstraShortestPath(graph)
        val shortestPath = algorithm.getPath(startPoint.id, endPoint.id)
        pathPointsMap = shortestPath.vertexList.mapIndexed { idx, id -> pointMap.pointMap[id]!! to idx }.toMap()
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
        val startCheatPoints = pathPointsMap.keys
        val bestCheats = mutableMapOf<Int, Long>() // key = saved time, count
        startCheatPoints.forEach { cheatStartPoint ->
            val endCheatPoints = getEndCheatPoints(cheatStartPoint, length)
            val lengthFromStartPointToStartCheatPoint = pathPointsMap[cheatStartPoint]!!

            endCheatPoints.forEach { endCheatPoint ->
                val lengthFromEndCheatPointToEndPoint = pathLength - pathPointsMap[endCheatPoint]!!
                var savedTime = pathLength
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

    private fun getEndCheatPoints(startCheatPoint: Point<Paths, Square>, length: Int): List<Point<Paths, Square>> {
        val result = LinkedList<Point<Paths, Square>>()
        for (rowShift in -length..length) {
            for (colShift in -length..length) {
                if (rowShift == 0 && colShift == 0)
                    continue
                if (rowShift.absoluteValue + colShift.absoluteValue > length)
                    continue

                val newRowIdx = startCheatPoint.rowIdx + rowShift
                val newColIdx = startCheatPoint.colIdx + colShift
                pointMap.pointMap["[$newRowIdx,$newColIdx]"]?.let { newPoint ->
                    if (newPoint.value.isOpenSpace())
                        result.add(newPoint)
                }
            }
        }

        return result
    }

    private data class Square(val name: Char) {
        fun isOpenSpace() = name != '#'
    }

    private class Paths {
        var canGoUp = false
        var canGoDown = false
        var canGoLeft = false
        var canGoRight = false
    }
}