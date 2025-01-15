package year2017

import common.BaseSolution

fun main() = println(SolutionDay02().result())

class SolutionDay02 : BaseSolution() {

    override val day = 2

    private val spreadsheet = input().lines().map { line ->
        line.split("\t").map { it.toInt() }
    }

    override fun task1(): String {
        return calculateChecksum(spreadsheet).toString()
    }

    override fun task2(): String {
        return calculateDivisibleSum(spreadsheet).toString()
    }

    private fun calculateDivisibleSum(spreadsheet: List<List<Int>>) = spreadsheet.sumOf { row ->
        row.flatMap { a -> row.map { b -> a to b } }
            .first { (a, b) -> a != b && a % b == 0 }
            .let { (a, b) -> a / b }
    }

    private fun calculateChecksum(spreadsheet: List<List<Int>>) = spreadsheet.sumOf { row ->
        val min = row.minOrNull() ?: 0
        val max = row.maxOrNull() ?: 0
        max - min
    }
}