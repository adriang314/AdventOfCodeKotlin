package year2017

import common.BaseSolution

fun main() = println(SolutionDay01().result())

class SolutionDay01 : BaseSolution() {

    override val day = 1

    private val digits = input().map { it.toString().toInt() }

    override fun task1(): String {
        return solveCaptcha1().toString()
    }

    override fun task2(): String {
        return solveCaptcha2().toString()
    }

    private fun solveCaptcha1() = digits.foldRightIndexed(0) { index, digit, sum ->
        if (digit == digits[(index + 1) % digits.size]) sum + digits[index] else sum
    }

    private fun solveCaptcha2() = digits.foldRightIndexed(0) { index, digit, sum ->
        if (digit == digits[(index + digits.size / 2) % digits.size]) sum + digits[index] else sum
    }
}