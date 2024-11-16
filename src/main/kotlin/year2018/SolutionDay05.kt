package year2018

import common.BaseSolution
import java.util.LinkedList
import kotlin.math.abs

fun main() = println(SolutionDay05().result())

class SolutionDay05 : BaseSolution() {

    override val day = 5
    override val year = 2018

    private val polymer = input()

    override fun task1(): String {
        val units = LinkedList(polymer.toList())
        executeReactions(units)
        return units.size.toString()
    }

    override fun task2(): String {
        return ('A'.code..'Z'.code).minOf { codeToRemove ->
            val units = LinkedList(polymer.toList().filter { it.code != codeToRemove && it.code != codeToRemove + 32 })
            executeReactions(units)
            units.size
        }.toString()
    }

    private fun executeReactions(units: LinkedList<Char>) {
        var i = 0
        val diff = abs('a'.code - 'A'.code)
        while (i < units.size - 1) {
            val unit1 = units[i].code
            val unit2 = units[i + 1].code

            if (abs(unit1 - unit2) == diff) {
                units.removeAt(i + 1)
                units.removeAt(i)
                if (i > 0)
                    i--
            } else {
                i++
            }
        }
    }
}