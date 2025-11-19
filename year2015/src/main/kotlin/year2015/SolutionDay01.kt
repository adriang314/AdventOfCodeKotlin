package year2015

import common.BaseSolution

fun main() = println(SolutionDay01().result())

class SolutionDay01 : BaseSolution() {

    override val day = 1

    override fun task1(): String {
        return input().map { if (it == '(') 1 else -1 }.sum().toString()
    }

    override fun task2(): String {
        return input().scanIndexed(Pair(0, 0)) { idx, pair, ch -> Pair(idx + 1, if (ch == '(') pair.second + 1 else pair.second - 1) }.first { it.second < 0 }.first.toString()
    }
}