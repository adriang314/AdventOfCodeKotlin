package year2017

import common.BaseSolution

fun main() = println(SolutionDay09().result())

class SolutionDay09 : BaseSolution() {

    override val day = 9

    override fun task1(): String {
        var score = 0
        var depth = 0
        var inGarbage = false
        var ignoreNext = false

        input().forEach { char ->
            when {
                ignoreNext -> ignoreNext = false
                char == '!' -> ignoreNext = true
                inGarbage -> if (char == '>') inGarbage = false
                char == '<' -> inGarbage = true
                char == '{' -> score += ++depth
                char == '}' -> depth--
            }
        }

        return score.toString()
    }

    override fun task2(): String {
        var garbageCount = 0
        var inGarbage = false
        var ignoreNext = false

        input().forEach { char ->
            when {
                ignoreNext -> ignoreNext = false
                char == '!' -> ignoreNext = true
                inGarbage -> if (char == '>') inGarbage = false else garbageCount++
                char == '<' -> inGarbage = true
            }
        }

        return garbageCount.toString()
    }
}