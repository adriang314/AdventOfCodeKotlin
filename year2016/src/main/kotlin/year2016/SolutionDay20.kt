package year2016

import common.BaseSolution
import common.difference

fun main() = println(SolutionDay20().result())

class SolutionDay20 : BaseSolution() {

    override val day = 20

    private val blacklistedRanges = input().split("\r\n").map {
        val (start, end) = it.split("-").map { num -> num.toLong() }
        start..end
    }

    private val allowedRanges = blacklistedRanges.fold(listOf(0..4294967295)) { allowedRanges, blacklistedRange -> allowedRanges.flatMap { allowedRange -> allowedRange.difference(blacklistedRange) } }

    override fun task1(): String {
        return allowedRanges.minByOrNull { it.first }!!.first.toString()
    }

    override fun task2(): String {
        return allowedRanges.sumOf { it.last - it.first + 1 }.toString()
    }
}