package year2024

import common.BaseSolution
import common.Cell
import common.Grid
import common.Position

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4

    private val grid = Grid(input()) { c, position -> Letter(position, c) }
    private val xLetters = grid.cells.filterValues { it.c == 'X' }.map { it.value }
    private val aLetters = grid.cells.filterValues { it.c == 'A' }.map { it.value }

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
            if (c != 'X')
                return 0

            var count = 0
            if (e?.c == 'M' && e?.e?.c == 'A' && e?.e?.e?.c == 'S')
                count++
            if (w?.c == 'M' && w?.w?.c == 'A' && w?.w?.w?.c == 'S')
                count++
            if (n?.c == 'M' && n?.n?.c == 'A' && n?.n?.n?.c == 'S')
                count++
            if (s?.c == 'M' && s?.s?.c == 'A' && s?.s?.s?.c == 'S')
                count++
            if (ne?.c == 'M' && ne?.ne?.c == 'A' && ne?.ne?.ne?.c == 'S')
                count++
            if (nw?.c == 'M' && nw?.nw?.c == 'A' && nw?.nw?.nw?.c == 'S')
                count++
            if (se?.c == 'M' && se?.se?.c == 'A' && se?.se?.se?.c == 'S')
                count++
            if (sw?.c == 'M' && sw?.sw?.c == 'A' && sw?.sw?.sw?.c == 'S')
                count++
            return count
        }

        fun xmas2Count() =
            if (c == 'A' &&
                ((nw?.c == 'M' && se?.c == 'S') || (nw?.c == 'S' && se?.c == 'M')) &&
                ((ne?.c == 'M' && sw?.c == 'S') || (ne?.c == 'S' && sw?.c == 'M'))
            ) 1 else 0
    }
}