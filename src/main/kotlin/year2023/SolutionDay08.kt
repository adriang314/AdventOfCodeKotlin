package year2023

import common.BaseSolution
import common.LeastCommonMultiple

fun main() = println(SolutionDay08().result())

class SolutionDay08 : BaseSolution() {

    override val day = 8

    override fun task1(): String {
        return Task(input(), { node -> node.name == "AAA" }, { node -> node.name == "ZZZ" }).result.toString()
    }

    override fun task2(): String {
        return Task(input(), { node -> node.name[2] == 'A' }, { node -> node.name[2] == 'Z' }).result.toString()
    }

    private class Task(input: String, isStartNode: (Node) -> Boolean, isDone: (Node) -> Boolean) {
        val result: Long

        init {
            val rawLines = input.split("\r\n", "\n")
            val nodes = Nodes(rawLines.filterIndexed { i, _ -> i > 1 }.map { Node(it) }.toList())
            val instructions = rawLines[0].toList()
            val startNodes = nodes.find { isStartNode(it) }
            val calculator = Calculator(instructions, startNodes) { isDone(it) }
            result = calculator.findNumberOfSteps()
        }
    }

    private class Calculator(
        private val instructions: List<Char>,
        private val startNodes: List<Node>,
        private val isDone: (Node) -> Boolean,
    ) {
        fun findNumberOfSteps(): Long {
            val steps = startNodes.parallelStream().map { findNumberOfSteps(it, isDone) }.toList()
            return LeastCommonMultiple.find(steps)
        }

        fun findNumberOfSteps(startNode: Node, isDone: (Node) -> Boolean): Long {
            var steps = 0L
            var currentNode = startNode
            while (true) {
                for (instruction in instructions) {
                    currentNode = (if (instruction == 'L') currentNode.leftNode else currentNode.rightNode)
                    steps++
                }

                if (isDone(currentNode))
                    break
            }
            return steps
        }
    }

    private class Nodes(private val nodes: List<Node>) {
        init {
            nodes.forEach { node ->
                node.leftNode = nodes.first { it.name == node.leftName }
                node.rightNode = nodes.first { it.name == node.rightName }
            }
        }

        fun find(filter: (Node) -> Boolean) = nodes.filter(filter)
    }

    private class Node(line: String) {

        private val mapRegex = Regex("^(\\w{3}) = \\((\\w{3}), (\\w{3})\\)$")
        val name: String
        val leftName: String
        val rightName: String
        lateinit var rightNode: Node
        lateinit var leftNode: Node

        override fun toString() = name

        init {
            val match = mapRegex.find(line)!!
            name = match.groupValues[1]
            leftName = match.groupValues[2]
            rightName = match.groupValues[3]
        }
    }
}
