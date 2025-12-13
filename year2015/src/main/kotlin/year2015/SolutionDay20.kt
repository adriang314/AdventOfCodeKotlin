package year2015

import common.BaseSolution
import common.divisors

fun main() = println(SolutionDay20().result())

class SolutionDay20 : BaseSolution() {

    override val day = 20

    private val presentThreshold = input().toInt()

    override fun task1(): String {
        val id = (750_000L..800_000L).first { houseId -> houseId.divisors().sum() * 10L >= presentThreshold }
        return id.toString()
    }

    override fun task2(): String {
        val id = (750_000L..800_000L).first { houseId -> houseId.divisors().filter { houseId <= it * 50L }.sum() * 11L >= presentThreshold }
        return id.toString()
    }
}