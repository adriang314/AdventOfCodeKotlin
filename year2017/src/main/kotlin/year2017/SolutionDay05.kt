package year2017

import common.BaseSolution

fun main() = println(SolutionDay05().result())

class SolutionDay05 : BaseSolution() {

    override val day = 5

    override fun task1(): String {
        return executeJumps { 1 }.toString()
    }

    override fun task2(): String {
        return executeJumps { if (it >= 3) -1 else 1 }.toString()
    }

    private fun executeJumps(offsetChange: (Int) -> Int): Long {
        val numbers = input().lines().map { it.toInt() }.toMutableList()
        var currentPosition = 0
        var steps = 0L
        while (true) {
            numbers.getOrNull(currentPosition)?.let { jump ->
                numbers[currentPosition] += offsetChange(jump)
                currentPosition += jump
            } ?: break

            steps++
        }

        return steps
    }
}