package year2023

import java.awt.Point
import java.awt.Polygon
import java.util.*

fun main() {
    println("${SolutionDay10()}")
}

/**
 * Increase stack trace with -Xss40m
 */
class SolutionDay10 : BaseSolution() {
    override val day = 10

    override fun task1(): String {
        val result = maxPath.size / 2
        return result.toString()
    }

    override fun task2(): String {
        val polygon = Polygon()
        maxPath.forEach { polygon.addPoint(it.rowIdx, it.colIdx) }

        var count = 0
        for (i in nodesMap.nodes.indices) {
            for (j in nodesMap.nodes[i].indices) {
                val currentNode = nodesMap.nodes[i][j]
                val isInside = polygon.contains(Point(i, j))
                if (isInside && !currentNode.inLoop)
                    count++
            }
        }

        return count.toString()
    }

    private val nodesMap: NodesMap
    private val maxPath: LinkedList<Node>

    init {
        val rawLines = input().split("\r\n", "\n")
        val lineNodes = rawLines.mapIndexed { index, s -> Line(s, index).nodes }
        val startNode = lineNodes.flatten().first { it.type == NodeType.START }
        nodesMap = NodesMap(startNode, lineNodes)
        maxPath = nodesMap.findMaxPath()
    }

    private class Line(line: String, val rowIdx: Int) {
        val nodes: List<Node>

        init {
            nodes = line.toList().mapIndexed { colIdx, c -> Node(NodeType.from(c), rowIdx, colIdx) }
        }
    }

    data class NodesMap(val startNode: Node, var nodes: List<List<Node>>) {
        init {
            for (i in nodes.indices) {
                for (j in nodes[i].indices) {
                    val top = nodes.getOrNull(i - 1)?.getOrNull(j)
                    val bottom = nodes.getOrNull(i + 1)?.getOrNull(j)
                    val left = nodes.getOrNull(i)?.getOrNull(j - 1)
                    val right = nodes.getOrNull(i)?.getOrNull(j + 1)
                    val current = nodes[i][j]

                    if (current.top == null && top != null && current.type.connectWithTop && top.type.connectWithBottom) {
                        current.top = top
                    }
                    if (current.bottom == null && bottom != null && current.type.connectWithBottom && bottom.type.connectWithTop) {
                        current.bottom = bottom
                    }
                    if (current.left == null && left != null && current.type.connectWithLeft && left.type.connectWithRight) {
                        current.left = left
                    }
                    if (current.right == null && right != null && current.type.connectWithRight && right.type.connectWithLeft) {
                        current.right = right
                    }
                }
            }
        }

        fun findMaxPath(): LinkedList<Node> {
            val path = findMaxPath(null, startNode, LinkedList())
            path.forEach { it.inLoop = true }
            return path
        }

        private val emptyList = LinkedList<Node>()

        private fun findMaxPath(prev: Node?, curr: Node, currPath: LinkedList<Node>): LinkedList<Node> {
            if (curr === startNode && prev != null)
                return currPath

            val rightPath = if (canMoveTo(curr.right, prev))
                findMaxPath(curr, curr.right!!, LinkedList(currPath).apply { add(curr) }) else emptyList
            val leftPath = if (canMoveTo(curr.left, prev))
                findMaxPath(curr, curr.left!!, LinkedList(currPath).apply { add(curr) }) else emptyList
            val topPath = if (canMoveTo(curr.top, prev))
                findMaxPath(curr, curr.top!!, LinkedList(currPath).apply { add(curr) }) else emptyList
            val bottomPath = if (canMoveTo(curr.bottom, prev))
                findMaxPath(curr, curr.bottom!!, LinkedList(currPath).apply { add(curr) }) else emptyList

            val leftRightPathsMax = if (rightPath.size > leftPath.size) rightPath else leftPath
            val topBottomPathsMax = if (topPath.size > bottomPath.size) topPath else bottomPath
            return if (leftRightPathsMax.size > topBottomPathsMax.size) leftRightPathsMax else topBottomPathsMax
        }

        private fun canMoveTo(next: Node?, prev: Node?) = next != null && (prev == null || next != prev)
    }

    data class Node(val type: NodeType, val rowIdx: Int, val colIdx: Int) {
        var left: Node? = null
        var right: Node? = null
        var top: Node? = null
        var bottom: Node? = null
        var inLoop = false
    }

    enum class NodeType(
        private var char: Char,
        var connectWithTop: Boolean,
        var connectWithBottom: Boolean,
        var connectWithLeft: Boolean,
        var connectWithRight: Boolean,
    ) {
        LT_CORNER('F', false, true, false, true),
        LD_CORNER('L', true, false, false, true),
        RT_CORNER('7', false, true, true, false),
        RD_CORNER('J', true, false, true, false),
        HOR_PIPE('-', false, false, true, true),
        VERT_PIPE('|', true, true, false, false),
        START('S', true, true, true, true),
        NONE('.', false, false, false, false);

        override fun toString() = char.toString()

        companion object {
            fun from(c: Char) = when (c) {
                'F' -> LT_CORNER
                'L' -> LD_CORNER
                '7' -> RT_CORNER
                'J' -> RD_CORNER
                '-' -> HOR_PIPE
                '|' -> VERT_PIPE
                'S' -> START
                else -> NONE
            }
        }
    }
}