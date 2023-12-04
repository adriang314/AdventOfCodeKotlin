package solution

import kotlin.math.pow

class SolutionDay04 : BaseSolution() {

    override val day = 4

    override fun task1(): String {
        val rawLines = input().split("\r\n", "\n")
        val result = rawLines.map { Line(it) }.sumOf { it.result }
        return result.toString()
    }

    override fun task2(): String {
        val rawLines = input().split("\r\n", "\n")
        val lines = rawLines.map { Line(it) }
        val originalCards = lines.size
        val copies = HashMap<Int, Int>()
        val maxGame = lines.maxOf { it.game }

        lines.forEach {
            val cardCount = copies.getOrDefault(it.game, 0) + 1 // plus one - original
            for (count in 1..cardCount) {
                for (i in it.game + 1..it.game + it.winSize) {
                    copies[i] = copies.getOrDefault(i, 0) + 1
                }
            }
        }

        val result = copies.filter { it.key <= maxGame }.map { it.value }.sum() + originalCards
        return result.toString()
    }

    class Line(line: String) {
        private val winning: List<Int>
        private val mine: List<Int>
        private val common: List<Int>
        val game: Int
        val result: Int
        val winSize: Int

        private val numberRegex = Regex("(\\d+)")

        init {
            val gameSplit = line.split(":")
            val winningSplit = gameSplit[1].split("|")

            game = numberRegex.find(gameSplit[0])!!.groupValues[1].toInt()
            winning = numbers(numberRegex, winningSplit[0])
            mine = numbers(numberRegex, winningSplit[1])
            common = mine.filter { winning.contains(it) }
            winSize = common.size
            result = if (common.isNotEmpty()) 2.0.pow(winSize - 1.0).toInt() else 0
        }

        private fun numbers(regex: Regex, line: String): List<Int> {
            val match = regex.findAll(line)
            return match.toList().stream().map { it.groupValues[1].toInt() }.toList()
        }
    }
}