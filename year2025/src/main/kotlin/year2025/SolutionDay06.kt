package year2025

import common.BaseSolution

fun main() = println(SolutionDay06().result())

class SolutionDay06 : BaseSolution() {

    override val day = 6

    private val digits = input().split("\r\n").dropLast(1).map { row -> row.mapIndexed { index, c -> c.digitToIntOrNull()?.let { Digit(it, index) } }.filterNotNull() }

    private val operations = input().split("\r\n").last().mapIndexed { index, c ->
        when (c) {
            '+' -> Operation('+', index)
            '*' -> Operation('*', index)
            else -> null
        }
    }.filterNotNull()

    override fun task1(): String {
        return doMath { indices ->
            digits.map { row -> row.filter { it.index in indices }.map { it.number }.joinToString("").toLong() }
        }.toString()
    }

    override fun task2(): String {
        return doMath { indices ->
            indices.map { index -> digits.mapNotNull { row -> row.firstOrNull { it.index == index }?.number }.joinToString("").toLong() }
        }.toString()
    }

    private fun doMath(buildNumbers: (IntRange) -> List<Long>): Long {
        var resultTotal = 0L
        for (operationIdx in 0 until operations.size) {
            val operation = operations[operationIdx]
            val nextOperation = operations.getOrNull(operationIdx + 1)

            val startIdx = operation.index
            val endIdx = nextOperation?.index?.let { it - 2 } ?: digits.maxOf { it.maxOf { j -> j.index } }
            val numbers = buildNumbers(startIdx..endIdx)

            resultTotal += when (operation.id) {
                '+' -> numbers.fold(0L) { acc, i -> acc + i }
                '*' -> numbers.fold(1L) { acc, i -> acc * i }
                else -> 0L
            }
        }

        return resultTotal
    }

    private data class Digit(val number: Int, val index: Int)

    private data class Operation(val id: Char, val index: Int)
}