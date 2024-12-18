package year2021

import common.BaseSolution

fun main() = println(SolutionDay01().result())

class SolutionDay01 : BaseSolution() {

    override val day = 1
    
    override fun task1() = depths.zipWithNext { a, b -> a < b }.count { it }.toString()

    override fun task2() = depths.indices.mapNotNull {
        val depth1 = depths.getOrNull(it)
        val depth2 = depths.getOrNull(it + 1)
        val depth3 = depths.getOrNull(it + 2)
        if (depth1 != null && depth2 != null && depth3 != null)
            depth1 + depth2 + depth3
        else null
    }.zipWithNext { a, b -> a < b }.count { it }.toString()

    private var depths: List<Int> = input().split("\r\n").map { it.toInt() }
}