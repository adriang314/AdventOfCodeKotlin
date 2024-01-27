package year2020

import common.BaseSolution

fun main() = println(SolutionDay01().result())

class SolutionDay01 : BaseSolution() {

    override val day = 1
    override val year = 2020

    override fun task1(): String {
        val numbers = input().split("\r\n").map { it.toInt() }
        val cross = numbers.indices
            .map { i ->
                numbers.map { Pair(numbers[i], it) }
            }.flatten()

        val rightPair = cross.first { it.first + it.second == 2020 }
        return (rightPair.first * rightPair.second).toString()
    }

    override fun task2(): String {
        val numbers = input().split("\r\n").map { it.toInt() }
        val cross = numbers.indices
            .map { i ->
                numbers.indices.map { j ->
                    numbers.map { Triple(numbers[i], numbers[j], it) }
                }.flatten()
            }.flatten()

        val rightTriple = cross.first { it.first + it.second + it.third == 2020 }
        return (rightTriple.first * rightTriple.second * rightTriple.third).toString()
    }
}