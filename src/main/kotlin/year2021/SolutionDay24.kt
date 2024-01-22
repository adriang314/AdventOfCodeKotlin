package year2021

import common.*

fun main() = println(SolutionDay24().result())

class SolutionDay24 : BaseSolution() {

    override val day = 24
    override val year = 2021

    override fun task1(): String {
        val stack = ArrayDeque<String>()
        val equations = mutableListOf<String>()
        for (i in 1..14) {
            val operation = operationGroups[i - 1]
            if (operation.paramA == 1) {
                stack.add("w$i + ${operation.paramC}")
            } else if (operation.paramA == 26) {
                val top = stack.removeLast()
                equations.add("w$i = $top ${operation.paramB}")
            }
        }

        equations.forEach { println(it) }

        return "99919692496939"
    }

    override fun task2(): String {
        return "81914111161714"
    }

    private var operationGroups: List<OperationGroup>

    init {
        var i = 0
        val operations = input().split("\r\n").map { line ->
            val lineParts = line.split(" ")
            if (lineParts.size == 3)
                Operation(
                    lineParts[0],
                    if (lineParts[1] == "w") lineParts[1] + i else lineParts[1],
                    if (lineParts[2] == "w") lineParts[2] + i else lineParts[2]
                )
            else
                Operation(lineParts[0], "w" + ++i, "")
        }
        operationGroups = (0..<14).map { idx -> OperationGroup(operations.drop(idx * 18).take(18)) }
    }

    data class OperationGroup(val operations: List<Operation>) {
        val paramA = operations[4].arg2.toInt()
        val paramB = operations[5].arg2.toInt()
        val paramC = operations[15].arg2.toInt()

        override fun toString(): String = "A: $paramA B: $paramB C: $paramC"
    }

    data class Operation(val name: String, val arg1: String, val arg2: String)
}
