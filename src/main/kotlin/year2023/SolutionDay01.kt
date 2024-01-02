package year2023

import common.BaseSolution

private val mappingDigits = mapOf(
    "1" to 1, "2" to 2, "3" to 3, "4" to 4, "5" to 5,
    "6" to 6, "7" to 7, "8" to 8, "9" to 9
)

private val mappingNames = mapOf(
    "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
    "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9
)

fun main() = println(SolutionDay01().result())

class SolutionDay01 : BaseSolution() {

    override val day = 1

    override fun task1() = task(mappingDigits).toString()

    override fun task2() = task(mappingDigits.plus(mappingNames)).toString()

    private fun task(mapping: Map<String, Int>): Int {
        val lines = input().split("\n")
        var count = 0
        lines.filter { it.isNotBlank() }.forEach {
            val first = firstDigit(mapping, it)
            val last = lastDigit(mapping, it)
            val value = "$first$last".toInt()
            count += value
        }
        return count
    }

    private fun firstDigit(mapping: Map<String, Int>, line: String): Int {
        var minIdx: Int? = null
        var value: Int? = null
        mapping.forEach {
            val idx = line.indexOf(it.key)
            if (idx >= 0 && (minIdx == null || idx < minIdx!!)) {
                minIdx = idx
                value = it.value
            }
        }
        return value!!

    }

    private fun lastDigit(mapping: Map<String, Int>, line: String): Int {
        var maxIdx: Int? = null
        var value: Int? = null
        mapping.forEach {
            val idx = line.lastIndexOf(it.key)
            if (idx >= 0 && (maxIdx == null || idx > maxIdx!!)) {
                maxIdx = idx
                value = it.value
            }
        }
        return value!!

    }
}