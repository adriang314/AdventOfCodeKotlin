package year2018

import common.BaseSolution

fun main() = println(SolutionDay01().result())

class SolutionDay01 : BaseSolution() {

    override val day = 1
    override val year = 2018

    private val regex = Regex("(-?\\d+)")

    private val values = input().split("\r\n").map {
        regex.find(it)!!.destructured.component1().toInt()
    }

    override fun task1(): String {
        val result = values.sum()
        return result.toString()
    }

    override fun task2(): String {
        val sumSet = mutableSetOf<Int>()
        var index = 0
        var sum = 0
        while (true) {
            sum += values[index++ % values.size]
            if (sumSet.contains(sum)) break else sumSet.add(sum)
        }
        return sum.toString()
    }
}