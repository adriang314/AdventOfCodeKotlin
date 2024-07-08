package year2020

import common.BaseSolution

fun main() = println(SolutionDay09().result())

class SolutionDay09 : BaseSolution() {

    override val day = 9
    override val year = 2020

    override fun task1(): String {
        return invalidNumber.value.toString()
    }

    override fun task2(): String {
        for (idx in 0..invalidNumber.idx) {
            val minMax = getMinMaxToSumForInvalid(idx)
            if (minMax != null)
                return (minMax.first + minMax.second).toString()
        }
        throw Exception()
    }

    private val preamble = 25

    private val numbers = input().split("\r\n").mapIndexed { idx, l -> Number(idx, l.toLong()) }.associateBy { it.idx }

    private val invalidNumber = numbers.values.drop(preamble).first { !isSumOfPreamble(it) }

    private fun getMinMaxToSumForInvalid(idx: Int): Pair<Long, Long>? {
        var sum = 0L
        var currIdx = idx
        var min = Long.MAX_VALUE
        var max = Long.MIN_VALUE
        do {
            val number = numbers[currIdx++]!!.value
            if (number < min) min = number
            if (number > max) max = number
            sum += number
        } while (sum < invalidNumber.value)

        return if (sum == invalidNumber.value) Pair(min, max) else null
    }

    private fun isSumOfPreamble(number: Number): Boolean {
        val preambleRange = (number.idx - preamble) until number.idx
        val preamble = preambleRange.map { numbers[it]!!.value }.toSet()
        return preambleRange.any { idx ->
            val firstNumber = numbers[idx]!!
            val secondNumber = number.value - firstNumber.value
            preamble.contains(secondNumber)
        }
    }

    data class Number(val idx: Int, val value: Long)
}