package year2024

import common.BaseSolution
import common.Cell
import common.Grid
import common.Position

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4

    private val grid = Grid(input()) { c, position -> Letter(position, c) }
    private val xLetters = grid.cells.filterValues { it.value == 'X' }.map { it.value }
    private val aLetters = grid.cells.filterValues { it.value == 'A' }.map { it.value }

    override fun task1(): String {
        val result = xLetters.sumOf { it.xmas1Count() }
        return result.toString()
    }

    override fun task2(): String {
        val result = aLetters.sumOf { it.xmas2Count() }
        return result.toString()
    }

    private class Letter(pos: Position, c: Char) : Cell(pos, c) {

        fun xmas1Count(): Int {
            if (value != 'X')
                return 0

            var count = 0
            if (e?.value == 'M' && e?.e?.value == 'A' && e?.e?.e?.value == 'S')
                count++
            if (w?.value == 'M' && w?.w?.value == 'A' && w?.w?.w?.value == 'S')
                count++
            if (n?.value == 'M' && n?.n?.value == 'A' && n?.n?.n?.value == 'S')
                count++
            if (s?.value == 'M' && s?.s?.value == 'A' && s?.s?.s?.value == 'S')
                count++
            if (ne?.value == 'M' && ne?.ne?.value == 'A' && ne?.ne?.ne?.value == 'S')
                count++
            if (nw?.value == 'M' && nw?.nw?.value == 'A' && nw?.nw?.nw?.value == 'S')
                count++
            if (se?.value == 'M' && se?.se?.value == 'A' && se?.se?.se?.value == 'S')
                count++
            if (sw?.value == 'M' && sw?.sw?.value == 'A' && sw?.sw?.sw?.value == 'S')
                count++
            return count
        }

        fun xmas2Count() =
            if (value == 'A' &&
                ((nw?.value == 'M' && se?.value == 'S') || (nw?.value == 'S' && se?.value == 'M')) &&
                ((ne?.value == 'M' && sw?.value == 'S') || (ne?.value == 'S' && sw?.value == 'M'))
            ) 1 else 0
    }
}