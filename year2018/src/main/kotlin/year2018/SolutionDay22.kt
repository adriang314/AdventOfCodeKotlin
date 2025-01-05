package year2018

import common.BaseSolution
import common.Position
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleDirectedWeightedGraph
import java.util.*

fun main() = println(SolutionDay22().result())

class SolutionDay22 : BaseSolution() {

    override val day = 22

    private val start = Position(0, 0)
    private val end = Position.fromString(input().lines().last().split(" ").last())
    private val depth = input().lines().first().split(" ").last().toInt()
    private val shift = 40
    private val map = createMap()
    private val graph = createGraph()

    override fun task1(): String {
        val result = map.filterKeys { it.x <= end.x && it.y <= end.y }.values.sumOf { it.riskLevel }
        return result.toString()
    }

    override fun task2(): String {
        val startPosition = PositionWithTool(start, map[start]!!.type, Tool.Torch)
        val endPosition = PositionWithTool(end, map[end]!!.type, Tool.Torch)
        val path = BidirectionalDijkstraShortestPath(graph).getPath(startPosition.toString(), endPosition.toString())
        val result = path.weight.toInt()
        return result.toString()
    }

    private fun createMap(): Map<Position, PositionInfo> {
        val map = mutableMapOf<Position, PositionInfo>()
        val queue = LinkedList(listOf(start))
        while (queue.isNotEmpty()) {
            val position = queue.poll()
            if (map.containsKey(position))
                continue

            if (position == start || position == end) {
                map[position] = PositionInfo(0L, depth)
            } else if (position.y == 0) {
                map[position] = PositionInfo(position.x * 16807L, depth)
            } else if (position.x == 0) {
                map[position] = PositionInfo(position.y * 48271L, depth)
            } else {
                val nPositionInfo = map[position.n()]!!
                val wPositionInfo = map[position.w()]!!
                map[position] = PositionInfo(nPositionInfo.erosionLevel * wPositionInfo.erosionLevel, depth)
            }

            if (position.x < end.x + shift) {
                queue.add(position.e())
            }

            if (position.y < end.y + shift) {
                queue.add(position.s())
            }
        }

        return map
    }

    private fun createGraph(): SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> {
        val graph = SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

        map.forEach {
            val position = it.key
            val info = it.value
            graph.addVertex(position.with(info.type, Tool.Torch).toString())
            graph.addVertex(position.with(info.type, Tool.ClimbingGear).toString())
            graph.addVertex(position.with(info.type, Tool.None).toString())
        }

        map.forEach {
            val pos = it.key
            val posInfo = it.value

            for (nextPos in sequenceOf(pos.s(), pos.e(), pos.n(), pos.w())) {
                if (!map.containsKey(nextPos))
                    continue

                val nextPosInfo = map[nextPos]!!
                for (tool in allowedTool(posInfo.type)) {
                    for (nextTool in allowedTool(nextPosInfo.type)) {
                        val posWithTool = PositionWithTool(pos, posInfo.type, tool).toString()
                        val nextPosWithTool = PositionWithTool(nextPos, nextPosInfo.type, nextTool).toString()
                        graph.addEdge(posWithTool, nextPosWithTool)
                        graph.addEdge(nextPosWithTool, posWithTool)
                        graph.setEdgeWeight(posWithTool, nextPosWithTool, if (tool == nextTool) 1.0 else 8.0)
                        graph.setEdgeWeight(nextPosWithTool, posWithTool, if (tool == nextTool) 1.0 else 8.0)
                    }
                }
            }
        }

        return graph
    }

    private fun allowedTool(positionType: PositionType): Sequence<Tool> {
        return when (positionType) {
            PositionType.Rocky -> sequenceOf(Tool.Torch, Tool.ClimbingGear)
            PositionType.Wet -> sequenceOf(Tool.None, Tool.ClimbingGear)
            PositionType.Narrow -> sequenceOf(Tool.Torch, Tool.None)
        }
    }

    private class PositionInfo(geologyIndex: Long, depth: Int) {
        val erosionLevel = (geologyIndex + depth) % 20183
        var type = PositionType.from((erosionLevel % 3).toInt())
        val riskLevel = when (type) {
            PositionType.Rocky -> 0
            PositionType.Wet -> 1
            PositionType.Narrow -> 2
        }

        override fun toString() = "${type.displayValue}"
    }

    private data class PositionWithTool(val position: Position, val type: PositionType, val tool: Tool)

    private fun Position.with(type: PositionType, tool: Tool) = PositionWithTool(this, type, tool)

    private enum class PositionType(val id: Int, val displayValue: Char) {
        Rocky(0, '.'), Wet(1, '='), Narrow(2, '|');

        companion object {
            fun from(id: Int) = PositionType.entries.single { it.id == id }
        }
    }

    private enum class Tool {
        Torch, ClimbingGear, None
    }
}
