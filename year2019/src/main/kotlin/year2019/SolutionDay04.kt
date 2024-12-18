package year2019

import common.BaseSolution

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4
    
    private val from = 137683
    private val to = 596253
    private val numbers = (from..to).map { it.toString() }

    private val doubleDigitRegex = Regex(".*(00|11|22|33|44|55|66|77|88|99).*")
    private val notDecreaseRegex = Regex("1*2*3*4*5*6*7*8*9*")

    override fun task1(): String {
        val numbers = numbers
            .filter { notDecreaseRegex.matches(it) }
            .filter { doubleDigitRegex.matches(it) }
        return numbers.size.toString()
    }

    override fun task2(): String {
        val numbers = numbers
            .filter { notDecreaseRegex.matches(it) }
            .filter { doubleDigitRegex.matches(it) }
            .filter { it.groupBy { c -> c }.values.map { e -> e.size }.contains(2) }
        return numbers.size.toString()
    }
}