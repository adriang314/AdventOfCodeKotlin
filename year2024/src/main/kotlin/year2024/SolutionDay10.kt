package year2024

import common.*

fun main() = println(SolutionDay10().result())

class SolutionDay10 : BaseSolution() {

    override val day = 10

    private val hills = Grid(input()) { c, position -> Hill(position, c) }.also { grid ->
        grid.cells.forEach {
            it.canGoN = it.n?.height == it.height + 1
            it.canGoS = it.s?.height == it.height + 1
            it.canGoW = it.w?.height == it.height + 1
            it.canGoE = it.e?.height == it.height + 1
        }
    }
    private val zeroHills: List<Hill> = hills.cells.filter { it.height == 0 }

    override fun task1(): String {
        val result = zeroHills.sumOf { go1(it).size }
        return result.toString()
    }

    override fun task2(): String {
        val result = zeroHills.sumOf { go2(it) }
        return result.toString()
    }

    private fun go1(hill: Hill): Set<Hill> {
        if (hill.isHighest)
            return setOf(hill)

        val result = mutableSetOf<Hill>()
        if (hill.canGoN) result.addAll(go1(hill.n!!))
        if (hill.canGoS) result.addAll(go1(hill.s!!))
        if (hill.canGoW) result.addAll(go1(hill.w!!))
        if (hill.canGoE) result.addAll(go1(hill.e!!))

        return result
    }

    private fun go2(hill: Hill): Int {
        if (hill.isHighest)
            return 1

        var result = 0
        if (hill.canGoN) result += go2(hill.n!!)
        if (hill.canGoS) result += go2(hill.s!!)
        if (hill.canGoW) result += go2(hill.w!!)
        if (hill.canGoE) result += go2(hill.e!!)

        return result
    }

    private class Hill(position: Position, c: Char) : Cell<Hill>(position, c) {
        val height = c.digitToInt()
        val isHighest = height == 9
    }
}