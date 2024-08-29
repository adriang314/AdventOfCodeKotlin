package year2018

import common.BaseSolution

fun main() = println(SolutionDay02().result())

class SolutionDay02 : BaseSolution() {

    override val day = 2
    override val year = 2018

    private val lines = input().split("\r\n")
    private val linesChars = lines.map { line -> line.toList().groupBy { it } }

    override fun task1(): String {
        val twos = linesChars.map { line -> if (line.any { it.value.size == 2 }) 1 else 0 }.sum()
        val threes = linesChars.map { line -> if (line.any { it.value.size == 3 }) 1 else 0 }.sum()
        return (twos * threes).toString()
    }

    override fun task2(): String {
        for (l1 in lines) {
            for (l2 in lines) {
                var diff = 0
                var common = ""
                for (idx in l1.indices) {
                    if (l1[idx] != l2[idx])
                        diff++
                    else
                        common += l1[idx]
                }

                if (diff == 1) {
                    return common
                }
            }
        }

        throw RuntimeException("Not found")
    }
}