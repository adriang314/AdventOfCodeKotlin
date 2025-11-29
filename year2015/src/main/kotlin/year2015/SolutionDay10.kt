package year2015

import common.BaseSolution

fun main() = println(SolutionDay10().result())

class SolutionDay10 : BaseSolution() {

    override val day = 10

    override fun task1(): String {
        return LookAndSayGame().playRounds(input(), 40).length.toString()
    }

    override fun task2(): String {
        return LookAndSayGame().playRounds(input(), 50).length.toString()
    }

    private class LookAndSayGame() {
        fun playRounds(input: String, rounds: Int): String = (1..rounds).fold(input) { roundInput, _ -> playRound(roundInput) }

        private fun playRound(input: String): String {
            val result = StringBuilder()
            var count = 1
            for (i in 1 until input.length) {
                if (input[i] == input[i - 1]) {
                    count++
                } else {
                    result.append(count).append(input[i - 1])
                    count = 1
                }
            }
            result.append(count).append(input.last())
            return result.toString()
        }
    }
}