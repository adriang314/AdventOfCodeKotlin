package year2024

import common.BaseSolution
import common.Grid
import common.Position

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4

    private val grid = Grid(input()) { grid, c, position -> CellWithLetter(grid, position, c) }
    private val xLetters = grid.cells.filterValues { it.value.c == 'X' }.map { it.value.value }
    private val aLetters = grid.cells.filterValues { it.value.c == 'A' }.map { it.value.value }

    override fun task1(): String {
        val result = xLetters.sumOf { it.xmas1Count() }
        return result.toString()
    }

    override fun task2(): String {
        val result = aLetters.sumOf { it.xmas2Count() }
        return result.toString()
    }

    private class CellWithLetter(val grid: Grid<CellWithLetter>, val pos: Position, val c: Char) {

        fun n(): CellWithLetter? = grid.getCell(pos.n())?.value
        fun s(): CellWithLetter? = grid.getCell(pos.s())?.value
        fun e(): CellWithLetter? = grid.getCell(pos.e())?.value
        fun w(): CellWithLetter? = grid.getCell(pos.w())?.value
        fun nw(): CellWithLetter? = grid.getCell(pos.nw())?.value
        fun ne(): CellWithLetter? = grid.getCell(pos.ne())?.value
        fun sw(): CellWithLetter? = grid.getCell(pos.sw())?.value
        fun se(): CellWithLetter? = grid.getCell(pos.se())?.value

        fun xmas1Count(): Int {
            if (c != 'X')
                return 0

            var count = 0
            if (e()?.c == 'M' && e()?.e()?.c == 'A' && e()?.e()?.e()?.c == 'S')
                count++
            if (w()?.c == 'M' && w()?.w()?.c == 'A' && w()?.w()?.w()?.c == 'S')
                count++
            if (n()?.c == 'M' && n()?.n()?.c == 'A' && n()?.n()?.n()?.c == 'S')
                count++
            if (s()?.c == 'M' && s()?.s()?.c == 'A' && s()?.s()?.s()?.c == 'S')
                count++
            if (ne()?.c == 'M' && ne()?.ne()?.c == 'A' && ne()?.ne()?.ne()?.c == 'S')
                count++
            if (nw()?.c == 'M' && nw()?.nw()?.c == 'A' && nw()?.nw()?.nw()?.c == 'S')
                count++
            if (se()?.c == 'M' && se()?.se()?.c == 'A' && se()?.se()?.se()?.c == 'S')
                count++
            if (sw()?.c == 'M' && sw()?.sw()?.c == 'A' && sw()?.sw()?.sw()?.c == 'S')
                count++
            return count
        }

        fun xmas2Count() =
            if (c == 'A' &&
                ((nw()?.c == 'M' && se()?.c == 'S') || (nw()?.c == 'S' && se()?.c == 'M')) &&
                ((ne()?.c == 'M' && sw()?.c == 'S') || (ne()?.c == 'S' && sw()?.c == 'M'))
            ) 1 else 0
    }
}