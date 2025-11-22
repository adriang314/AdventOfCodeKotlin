package year2016

import common.BaseSolution

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {

    override val day = 18

    private val room = Room(input())

    override fun task1(): String {
        return room.totalSafeTiles(40).toString()
    }

    override fun task2(): String {
        return room.totalSafeTiles(400000).toString()
    }

    private data class Row(val pattern: String) {
        val safeTileCount: Int = pattern.count { it == '.' }
    }

    private class Room(firstRow: String) {
        private val rows = mutableListOf(Row(firstRow))

        init {
            var currentRow = firstRow
            var index = 0
            while (index++ < 400000) {
                currentRow = nextRow(currentRow)
                rows.add(Row(currentRow))
            }
        }

        fun totalSafeTiles(count: Int): Int =
            if (count == rows.size)
                rows.sumOf { it.safeTileCount }
            else
                rows.take(count).sumOf { it.safeTileCount }

        private fun nextRow(currentRow: String): String {
            val nextRow = buildString {
                for (i in currentRow.indices) {
                    val left = if (i == 0) '.' else currentRow[i - 1]
                    val center = currentRow[i]
                    val right = if (i == currentRow.length - 1) '.' else currentRow[i + 1]
                    val isTrap = when {
                        left == '^' && center == '^' && right == '.' -> true
                        left == '.' && center == '^' && right == '^' -> true
                        left == '^' && center == '.' && right == '.' -> true
                        left == '.' && center == '.' && right == '^' -> true
                        else -> false
                    }
                    append(if (isTrap) '^' else '.')
                }
            }
            return nextRow
        }
    }
}