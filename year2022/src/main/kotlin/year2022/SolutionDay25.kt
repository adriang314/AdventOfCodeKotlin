package year2022

import common.BaseSolution
import common.pow

fun main() = println(SolutionDay25().result())

class SolutionDay25 : BaseSolution() {

    override val day = 25
    
    override fun task1(): String {
        val sum = numbers.sumOf { it.decimalNumber }
        val result = SnafuNumber.from(sum).text
        return result
    }

    override fun task2() = ""

    private var numbers: List<SnafuNumber> = input().split("\r\n")
        .map { line -> SnafuNumber(line) }

    data class SnafuNumber(val text: String) {

        val decimalNumber: Long
        val numbers: List<SnafuDigit>

        init {
            var decimal = 0L
            numbers = text.toList().map { SnafuDigit.from(it) }
            numbers.reversed().mapIndexed { idx, digit -> decimal += 5.pow(idx) * digit.decimal }
            decimalNumber = decimal
        }

        fun lowerNumber(changeAtIndex: Int) = SnafuNumber(numbers.mapIndexed { index, snafuDigit ->
            if (index == changeAtIndex && snafuDigit.hasLower())
                snafuDigit.lower().coded
            else
                snafuDigit.coded
        }.joinToString(""))

        fun upperNumber(changeAtIndex: Int) = SnafuNumber(numbers.mapIndexed { index, snafuDigit ->
            if (index == changeAtIndex && snafuDigit.hasUpper())
                snafuDigit.upper().coded
            else
                snafuDigit.coded
        }.joinToString(""))

        override fun toString(): String = "$text $decimalNumber"

        companion object {

            fun from(int: Long): SnafuNumber {

                var lower = SnafuNumber("1")
                var upper = SnafuNumber("2")

                do {
                    val inRange = isInRange(int, lower, upper)
                    if (inRange)
                        break
                    lower = SnafuNumber(lower.text + "=")
                    upper = SnafuNumber(upper.text + "2")
                } while (true)

                var index = 0
                while (true) {

                    if (lower == upper)
                        return lower

                    do {
                        val nextLower = lower.upperNumber(index)
                        if (nextLower == lower)
                            break
                        val nextLowerInRange = isInRange(int, nextLower, upper)
                        if (nextLowerInRange)
                            lower = nextLower
                    } while (nextLowerInRange)


                    do {
                        val nextUpper = upper.lowerNumber(index)
                        if (nextUpper == upper)
                            break
                        val nextUpperInRange = isInRange(int, lower, nextUpper)
                        if (nextUpperInRange)
                            upper = nextUpper
                    } while (nextUpperInRange)

                    index++
                }
            }

            private fun isInRange(int: Long, lower: SnafuNumber, upper: SnafuNumber) =
                int in lower.decimalNumber..upper.decimalNumber
        }
    }

    enum class SnafuDigit(val decimal: Int, val coded: Char) {
        Two(2, '2'),
        One(1, '1'),
        Zero(0, '0'),
        MinusOne(-1, '-'),
        MinusTwo(-2, '=');

        fun lower() = when (this) {
            Two -> One
            One -> Zero
            Zero -> MinusOne
            MinusOne -> MinusTwo
            else -> throw Exception()
        }

        fun upper() = when (this) {
            One -> Two
            Zero -> One
            MinusOne -> Zero
            MinusTwo -> MinusOne
            else -> throw Exception()
        }

        fun hasLower() = this != MinusTwo
        fun hasUpper() = this != Two

        companion object {
            fun from(c: Char) = entries.first { it.coded == c }
        }
    }
}