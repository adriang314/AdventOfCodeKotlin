package year2023

import common.BaseSolution

fun main() {
    println("${SolutionDay02()}")
}

class SolutionDay02 : BaseSolution() {

    override val day = 2

    override fun task1(): String {
        val lines = input().split("\n")
        return lines.map { Line(it) }.filter { it.isPossible }.sumOf { it.game }.toString()
    }

    override fun task2(): String {
        val lines = input().split("\n")
        return lines.map { Line(it) }.sumOf { it.power }.toString()
    }

    class Line(line: String) {
        val game: Int
        val isPossible: Boolean
        val power: Int

        private val gameRegex = Regex("Game (\\d+):")

        init {
            game = gameRegex.find(line)!!.groupValues[1].toInt()

            val sets = line.split(";").map { Set(it) }.toList()
            isPossible = sets.all { it.isPossible }

            val maxRed = sets.maxOfOrNull { it.red } ?: 0
            val maxBlue = sets.maxOfOrNull { it.blue } ?: 0
            val maxGreen = sets.maxOfOrNull { it.green } ?: 0

            power = maxRed * maxBlue * maxGreen
        }

        class Set(line: String) {
            private val redRegex = Regex("(\\d+) red")
            private val blueRegex = Regex("(\\d+) blue")
            private val greenRegex = Regex("(\\d+) green")

            val blue: Int
            val red: Int
            val green: Int
            val isPossible: Boolean

            init {
                red = sum(redRegex, line)
                blue = sum(blueRegex, line)
                green = sum(greenRegex, line)
                isPossible = blue <= 14 && green <= 13 && red <= 12
            }

            private fun sum(regex: Regex, line: String): Int {
                val match = regex.findAll(line)
                val groups = match.toList().stream().map { it.groupValues[1].toInt() }.toList()
                return groups.sum()
            }
        }
    }
}