package year2015

import common.BaseSolution
import common.Combinatorics

fun main() = println(SolutionDay24().result())

class SolutionDay24 : BaseSolution() {

    override val day = 24

    private val weights = input().split("\r\n").map { it.toInt() }.toSet()
    private val totalWeight = weights.sum()

    override fun task1(): String {
        val result = Combinatorics.combinations(weights, 6).filter { it.sum() == totalWeight / 3 }.minOf { it.fold(1L) { acc, i -> acc * i } }
        return result.toString()
    }

    override fun task2(): String {
        val result = Combinatorics.combinations(weights, 5).filter { it.sum() == totalWeight / 4 }.minOf { it.fold(1L) { acc, i -> acc * i } }
        return result.toString()
    }
}