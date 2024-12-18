package year2021

import common.BaseSolution

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4
    
    override fun task1(): String {
        val (winningBoard, winningNumber) = playGameFirstWins()
        return score(winningBoard, winningNumber).toString()
    }

    override fun task2(): String {
        val (winningBoard, winningNumber) = playGameLastWins()
        return score(winningBoard, winningNumber).toString()
    }

    private fun score(winningBoard: Board, winningNumber: Int): Int {
        val sumOfUnmarked = winningBoard.rows.flatten().filter { !it.marked }.sumOf { it.value }
        return sumOfUnmarked * winningNumber
    }

    private fun playGameFirstWins(): Pair<Board, Int> {
        clearBoards()
        val numbersStack = ArrayDeque(numbers)
        var winningNumber: Int
        var winningBoard: Board?
        do {
            winningNumber = numbersStack.removeFirst()
            boards.forEach { it.mark(winningNumber) }
            winningBoard = boards.firstOrNull { it.isWinning() }
        } while (winningBoard == null)

        return Pair(winningBoard, winningNumber)
    }

    private fun playGameLastWins(): Pair<Board, Int> {
        clearBoards()
        val numbersStack = ArrayDeque(numbers)
        val winningBoards = mutableListOf<Board>()
        var winningNumber: Int
        do {
            winningNumber = numbersStack.removeFirst()
            boards.forEach { it.mark(winningNumber) }

            boards.filter { it.isWinning() }.forEach { board ->
                if (!winningBoards.contains(board))
                    winningBoards.add(board)
            }
        } while (winningBoards.size < boards.size)

        return Pair(winningBoards.last(), winningNumber)
    }

    private var numbers: List<Int>
    private var boards: List<Board>

    init {
        val parts = input().split("\r\n\r\n")
        numbers = parts[0].split(",").map { it.toInt() }

        val regex = Regex("(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)")

        boards = parts.filterIndexed { index, _ -> index >= 1 }
            .map { lines ->
                Board(lines.split("\r\n").map { line ->
                    val (v0, v1, v2, v3, v4) = regex.find(line)!!.destructured
                    listOf(v0, v1, v2, v3, v4).map { Number(it.toInt(), false) }
                })
            }
    }

    private fun clearBoards() = boards.forEach { board -> board.rows.flatten().forEach { it.marked = false } }

    data class Board(val rows: List<List<Number>>) {
        private val length = rows.first().size
        private val columns = (0 until length).map { index -> rows.map { it[index] } }

        fun mark(number: Int) = rows.flatten().filter { it.value == number }.forEach { it.marked = true }

        fun isWinning() = hasWinningRow() || hasWinningColumn()

        private fun hasWinningRow() = rows.map { row -> row.all { it.marked } }.any { it }

        private fun hasWinningColumn() = columns.map { column -> column.all { it.marked } }.any { it }
    }

    data class Number(val value: Int, var marked: Boolean)
}