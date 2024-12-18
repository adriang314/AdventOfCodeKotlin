package year2022

import common.BaseSolution

fun main() = println(SolutionDay06().result())

class SolutionDay06 : BaseSolution() {
    override val day = 6
    
    override fun task1(): String {
        val text = input()
        for (i in 0..text.length - 4) {
            if (areDifferent(text.substring(i, i + 4)))
                return (i + 4).toString()
        }
        return ""
    }

    override fun task2(): String {
        val text = input()
        for (i in 0..text.length - 14) {
            if (areDifferent(text.substring(i, i + 14)))
                return (i + 14).toString()
        }
        return ""
    }

    private fun areDifferent(s: String) = s.toList().groupBy { it }.maxBy { it.value.size }.value.size == 1
}