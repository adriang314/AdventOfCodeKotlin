package year2024

import common.BaseSolution
import kotlin.math.absoluteValue

fun main() = println(SolutionDay02().result())

class SolutionDay02 : BaseSolution() {

    override val day = 2
    
    private val values = input().split("\r\n").map { line ->
        line.split(" ").map { it.toInt() }
    }

    override fun task1(): String {
        val result = values.map { numbers -> isSafe(numbers) }.count { it }
        return result.toString()
    }

    override fun task2(): String {
        val result = values.map { numbers ->
            val numbersMinusOneNumber = numbers.indices.map { i ->
                val newList = numbers.toMutableList()
                newList.removeAt(i)
                newList.toList()
            }
            numbersMinusOneNumber.any { isSafe(it) }
        }.count { it }
        return result.toString()
    }

    private fun isSafe(numbers: List<Int>): Boolean {
        var isIncrease: Boolean? = null
        for (i in 0..numbers.size - 2) {
            val prev = numbers[i]
            val next = numbers[i + 1]

            val tmpIncrease = when {
                prev > next -> false
                prev < next -> true
                else -> return false
            }

            if (isIncrease == null)
                isIncrease = tmpIncrease

            if ((isIncrease != tmpIncrease) || ((next - prev).absoluteValue > 3))
                return false
        }
        return true
    }
}