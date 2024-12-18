package year2024

import common.BaseSolution

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4
    
    private val letters = input().split("\r\n").mapIndexed { x, line ->
        line.mapIndexed { y, c -> Letter(Position(x, y), c) }
    }.flatten().associateBy { it.position }

    private val xLetters = letters.filter { it.value.c == 'X' }
    private val aLetters = letters.filter { it.value.c == 'A' }

    init {
        letters.values.forEach { letter ->
            letter.n = letters[letter.position.n()]
            letter.s = letters[letter.position.s()]
            letter.e = letters[letter.position.e()]
            letter.w = letters[letter.position.w()]
            letter.ne = letters[letter.position.ne()]
            letter.nw = letters[letter.position.nw()]
            letter.se = letters[letter.position.se()]
            letter.sw = letters[letter.position.sw()]
        }
    }

    override fun task1(): String {
        val result = xLetters.values.sumOf { it.xmas1Count() }
        return result.toString()
    }

    override fun task2(): String {
        val result = aLetters.values.sumOf { it.xmas2Count() }
        return result.toString()
    }

    private data class Letter(val position: Position, val c: Char) {
        var n: Letter? = null
        var s: Letter? = null
        var e: Letter? = null
        var w: Letter? = null
        var nw: Letter? = null
        var ne: Letter? = null
        var sw: Letter? = null
        var se: Letter? = null

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

    private data class Position(val x: Int, val y: Int) {
        fun n() = Position(x - 1, y)
        fun s() = Position(x + 1, y)
        fun e() = Position(x, y + 1)
        fun w() = Position(x, y - 1)
        fun nw() = Position(x - 1, y - 1)
        fun ne() = Position(x - 1, y + 1)
        fun sw() = Position(x + 1, y - 1)
        fun se() = Position(x + 1, y + 1)
    }
}