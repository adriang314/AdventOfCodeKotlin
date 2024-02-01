package year2020

import common.BaseSolution

fun main() = println(SolutionDay08().result())

class SolutionDay08 : BaseSolution() {

    override val day = 8
    override val year = 2020

    override fun task1(): String {
        val result = executeTillRepeat(operations)
        return result.first.toString()
    }

    override fun task2(): String {
        val fixableOperations = operations.values.filter { it.name != "acc" }
        for (operation in fixableOperations) {
            val fixedOperations = operations.minus(operation.id).plus(operation.id to operation.opposite())
            val result = executeTillRepeat(fixedOperations)
            if (result.second)
                return result.first.toString()
        }
        throw Exception()
    }

    private val operationRegex = Regex("(\\w+) \\+?(-?\\d+)")
    private val operations = input().split("\r\n").mapIndexed { id, line ->
        val (name, arg) = operationRegex.find(line)!!.destructured
        Operation(name, id, arg.toInt())
    }.associateBy { it.id }

    private fun executeTillRepeat(operations: Map<Int, Operation>): Pair<Long, Boolean> {
        var accumulator = 0L
        val executedOperations = mutableSetOf<Int>()
        var currentOperation = operations[0]
        while (currentOperation != null && !executedOperations.contains(currentOperation.id)) {
            executedOperations.add(currentOperation.id)
            if (currentOperation.name == "acc") {
                accumulator += currentOperation.arg
                currentOperation = operations[currentOperation.id + 1]
            } else if (currentOperation.name == "jmp") {
                currentOperation = operations[currentOperation.id + currentOperation.arg]
            } else {
                currentOperation = operations[currentOperation.id + 1]
            }
        }

        return Pair(accumulator, currentOperation == null)
    }

    data class Operation(val name: String, val id: Int, val arg: Int) {
        fun opposite() = when (name) {
            "jmp" -> this.copy(name = "nop")
            "nop" -> this.copy(name = "jmp")
            else -> throw Exception()
        }
    }
}