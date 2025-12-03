package year2025

import common.BaseSolution

fun main() = println(SolutionDay03().result())

class SolutionDay03 : BaseSolution() {

    override val day = 3

    private val banks = input().split("\r\n").map { Bank(it.map { n -> n.digitToInt() }) }

    override fun task1(): String {
        return banks.sumOf { it.maxJoltage(2) }.toString()
    }

    override fun task2(): String {
        return banks.sumOf { it.maxJoltage(12) }.toString()
    }

    private data class Bank(val batteries: List<Int>) {

        fun maxJoltage(useBatteries: Int): Long {
            val digits = mutableListOf<Int>()
            var minIndex = 0
            var maxIndex = batteries.size - useBatteries

            repeat(useBatteries) {
                val nextMaxDigit = findMaxDigit(minIndex..maxIndex, batteries)
                digits.add(nextMaxDigit.value)
                minIndex = nextMaxDigit.index + 1
                maxIndex++
            }

            return digits.joinToString("").toLong()
        }

        private fun findMaxDigit(indexRange: IntRange, digits: List<Int>): Digit {
            var maxDigit = -1
            var maxDigitIndex = -1
            for (index in indexRange) {
                val digit = digits[index]
                if (digit > maxDigit) {
                    maxDigit = digit
                    maxDigitIndex = index
                }
            }
            return Digit(maxDigit, maxDigitIndex)
        }
    }

    private data class Digit(val value: Int, val index: Int)
}