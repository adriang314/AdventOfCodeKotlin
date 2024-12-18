package year2019

import common.BaseSolution
import kotlin.math.max

fun main() = println(SolutionDay01().result())

class SolutionDay01 : BaseSolution() {

    override val day = 1
    
    private val initValues = input().split("\r\n").map { it.toInt() }

    override fun task1(): String {
        val result = countFuel(initValues).sum()
        return result.toString()
    }

    override fun task2(): String {
        var values = initValues
        var totalFuel = 0
        do {
            values = countFuel(values)
            val fuelIncrement = values.sum()
            totalFuel += fuelIncrement
        } while (fuelIncrement > 0)
        return totalFuel.toString()
    }

    private fun countFuel(values: List<Int>): List<Int> = values.map { max(0, (it / 3) - 2) }
}