package year2024

import common.BaseSolution
import common.Combinatorics
import java.util.*

fun main() = println(SolutionDay07().result())

class SolutionDay07 : BaseSolution() {

    override val day = 7
    override val year = 2024

    private val regex = Regex("^(\\d+): (.*)$")
    private val equations = input().split("\r\n").map { line ->
        val (result, parts) = regex.find(line)!!.destructured
        Equation(result.toLong(), parts.split(" ").map { it.toLong() })
    }

    override fun task1(): String {
        val result = equations.filter { it.isPossible(charArrayOf('*', '+')) }.sumOf { it.result }
        return result.toString()
    }

    override fun task2(): String {
        val result = equations.filter { it.isPossible(charArrayOf('*', '+', '|')) }.sumOf { it.result }
        return result.toString()
    }

    private data class Equation(val result: Long, val parts: List<Long>) {

        fun isPossible(operatorTypes: CharArray): Boolean {
            val allVariations = LinkedList<CharArray>()
            val item = CharArray(parts.size - 1)
            Combinatorics.variationsWithRepetition(allVariations, operatorTypes, item)

            for (operators in allVariations) {
                var i = 0
                var res = parts[i]

                for (operator in operators) {
                    val next = parts[++i]
                    when (operator) {
                        '*' -> res *= next
                        '+' -> res += next
                        '|' -> res = (res * if (next < 10L) 10L else if (next < 100L) 100L else 1000L) + next
                    }
                }

                if (res == result)
                    return true
            }

            return false
        }
    }
}