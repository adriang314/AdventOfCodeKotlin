package year2024

import common.BaseSolution
import kotlin.math.absoluteValue

fun main() = println(SolutionDay01().result())

class SolutionDay01 : BaseSolution() {

    override val day = 1
    
    private val regex = Regex("^(\\d+) +(\\d+)$")
    private val values = input().split("\r\n").map {
        val (x, y) = regex.find(it)!!.destructured
        Pair(x.toInt(), y.toInt())
    }
    private val xList = values.map { it.first }.sorted()
    private val yList = values.map { it.second }.sorted()

    override fun task1(): String {
        val sum = xList.mapIndexed { index, x -> (x - yList[index]).absoluteValue }.sum()
        return sum.toString()
    }

    override fun task2(): String {
        val sum = xList.sumOf { x -> x * yList.count { it == x } }
        return sum.toString()
    }
}