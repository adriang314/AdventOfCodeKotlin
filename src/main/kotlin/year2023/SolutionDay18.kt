package year2023

import common.BaseSolution
import java.awt.Point
import java.awt.Polygon
import java.util.*

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {

    override val day = 18

    override fun task1(): String {
        val lines = Lines(input(), Task.One)
        val polygon = CustomPolygon(lines.edges)
        val result = polygon.calculateCoveredArea()
        return result.toString()
    }

    override fun task2(): String {
        val lines = Lines(input(), Task.Two)
        val polygon = CustomPolygon(lines.edges)
        val result = polygon.calculateCoveredArea()
        return result.toString()
    }

    class CustomPolygon(edges: List<Edge>) {
        private val edgePoints = LinkedList<Pair<IntRange, IntRange>>()
        private val polygon = Polygon()
        private var minRowIdx = 0
        private var maxRowIdx = 0
        private val edgeTypes = EdgeTypes(edges)

        init {
            var prev = Point(0, 0)
            polygon.addPoint(0, 0)
            edges.forEach {
                val next = when (it.direction) {
                    Direction.Right -> {
                        edgePoints.add(Pair(prev.x..prev.x, prev.y..prev.y + it.length))
                        Point(prev.x, prev.y + it.length)
                    }

                    Direction.Left -> {
                        edgePoints.add(Pair(prev.x..prev.x, prev.y - it.length..prev.y))
                        Point(prev.x, prev.y - it.length)
                    }

                    Direction.Down -> {
                        edgePoints.add(Pair(prev.x - it.length..prev.x, prev.y..prev.y))
                        Point(prev.x - it.length, prev.y)
                    }

                    Direction.Up -> {
                        edgePoints.add(Pair(prev.x..prev.x + it.length, prev.y..prev.y))
                        Point(prev.x + it.length, prev.y)
                    }
                }

                prev = next

                if (next.x != 0 || next.y != 0)
                    polygon.addPoint(next.x, next.y)

                if (next.x < minRowIdx)
                    minRowIdx = next.x
                if (next.x > maxRowIdx)
                    maxRowIdx = next.x
            }
        }

        fun calculateCoveredArea(): Long {
            var inside = 0L
            for (rowIdx in minRowIdx..maxRowIdx) {
                val verticalCuts = edgeTypes.verticalCuts(rowIdx)
                var lastContains = false
                var prevColIdx: Int? = null
                for (colIdx in verticalCuts) {
                    if (prevColIdx == null) {
                        prevColIdx = colIdx
                        continue
                    }

                    val currMinus1OnEdge = edgePoints.any { rowIdx in it.first && colIdx - 1 in it.second }
                    val currMinus1Contains = currMinus1OnEdge || polygon.contains(rowIdx, colIdx - 1)
                    if (currMinus1Contains)
                        inside += colIdx - prevColIdx + if (!lastContains) 1 else 0
                    lastContains = currMinus1Contains
                    prevColIdx = colIdx
                }
            }

            return inside
        }
    }

    class Lines(lines: String, task: Task) {
        var edges: List<Edge>

        private val regex = Regex("^(\\w+) (\\d+) \\(#(\\w\\w\\w\\w\\w)(\\d)\\)")

        init {
            val rawLines = lines.split("\r\n", "\n")
            edges = rawLines.mapIndexed { _, line ->
                val match = regex.find(line)!!
                val direction = Direction.from(match.groupValues[1])
                val length = match.groupValues[2].toInt()
                val directionNew = Direction.from(match.groupValues[4])
                val newLength = Integer.parseInt(match.groupValues[3], 16)
                when (task) {
                    Task.One -> Edge(direction, length)
                    Task.Two -> Edge(directionNew, newLength)
                }
            }
        }
    }

    enum class Task { One, Two }

    data class Node(val rowIdx: Int, val colIdx: Int)

    class EdgeTypes(edges: List<Edge>) {
        private val horizontals = mutableListOf<Pair<Int, IntRange>>()  // rowIdx + col idx range
        private val verticals = mutableListOf<Pair<Int, IntRange>>()  // colIdx + row idx range

        init {
            var from = Node(0, 0)
            edges.forEach {
                addEdge(it, from)
                from = when (it.direction) {
                    Direction.Right -> Node(from.rowIdx, from.colIdx + it.length)
                    Direction.Left -> Node(from.rowIdx, from.colIdx - it.length)
                    Direction.Up -> Node(from.rowIdx + it.length, from.colIdx)
                    Direction.Down -> Node(from.rowIdx - it.length, from.colIdx)
                }
            }
        }

        fun verticalCuts(rowIdx: Int): List<Int> {
            val horizontalCuts =
                horizontals.filter { it.first == rowIdx }.map { listOf(it.second.first, it.second.last) }.flatten()
            val verticalCuts =
                verticals.filter { rowIdx in it.second }.map { it.first }.toList()
            return (horizontalCuts + verticalCuts).distinct().sorted()
        }

        private fun addEdge(edge: Edge, from: Node) {
            if (edge.isHorizontal)
                horizontals.add(Pair(from.rowIdx, edge.horizontalRange(from.colIdx)))
            else if (edge.isVertical)
                verticals.add(Pair(from.colIdx, edge.verticalRange(from.rowIdx)))
        }
    }

    data class Edge(val direction: Direction, val length: Int) {
        val isHorizontal = direction == Direction.Left || direction == Direction.Right
        val isVertical = direction == Direction.Up || direction == Direction.Down

        fun horizontalRange(colIdx: Int) =
            if (direction == Direction.Right)
                colIdx..colIdx + length
            else
                colIdx - length..colIdx

        fun verticalRange(rowIdx: Int) =
            if (direction == Direction.Up)
                rowIdx..rowIdx + length
            else
                rowIdx - length..rowIdx
    }

    enum class Direction {
        Right, Left, Up, Down;

        companion object {
            fun from(c: String) = when (c) {
                "R", "0" -> Right
                "D", "1" -> Down
                "L", "2" -> Left
                "U", "3" -> Up
                else -> throw Exception("Unable to parse")
            }
        }
    }
}