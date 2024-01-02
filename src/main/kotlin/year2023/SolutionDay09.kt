package year2023

import common.BaseSolution

fun main() = println(SolutionDay09().result())

class SolutionDay09 : BaseSolution() {
    override val day = 9

    override fun task1(): String {
        val rawLines = input().split("\r\n", "\n")
        val result = rawLines.map { History(it, true) }.sumOf { it.result }
        return result.toString()
    }

    override fun task2(): String {
        val rawLines = input().split("\r\n", "\n")
        val result = rawLines.map { History(it, false) }.sumOf { it.result }
        return result.toString()
    }

    private class History(line: String, appendHistory: Boolean) {
        private val numberRegex = Regex("(-?\\d+)")

        // the first is the child history, the second is the parent history
        private val allHistories: MutableList<Pair<MutableList<Long>, MutableList<Long>>>

        val result: Long

        init {
            val firstHistory = numbers(numberRegex, line).toMutableList()

            allHistories = ArrayList(firstHistory.size)
            allHistories.add(Pair(firstHistory, mutableListOf()))
            buildAllHistories(firstHistory)
            updateHistories(appendHistory)

            result = if (appendHistory) firstHistory.last() else firstHistory.first()
        }

        private fun updateHistories(appendHistory: Boolean) {
            allHistories.reversed().forEach { pair ->
                if (pair.first == allHistories.last().first)
                    pair.first.add(0L)

                if (appendHistory) {
                    val toAdd = pair.first.last() + (pair.second.lastOrNull() ?: 0L)
                    pair.second.add(toAdd)
                } else {
                    val toAdd = (pair.second.firstOrNull() ?: 0L) - pair.first.first()
                    pair.second.add(0, toAdd)
                }
            }
        }

        private fun buildAllHistories(history: MutableList<Long>): MutableList<Long> {
            if (history.all { it == 0L }) // true if empty collection
                return history

            val childHistory = ArrayList<Long>(history.size)
            for (i in 0..<history.size - 1) {
                childHistory.add(history[i + 1] - history[i])
            }

            allHistories.add(Pair(childHistory, history))
            return buildAllHistories(childHistory)
        }

        private fun numbers(regex: Regex, line: String): List<Long> {
            val match = regex.findAll(line)
            return match.toList().stream().map { it.groupValues[1].toLong() }.toList()
        }
    }
}