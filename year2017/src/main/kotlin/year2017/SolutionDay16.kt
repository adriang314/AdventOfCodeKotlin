package year2017

import common.BaseSolution
import common.shiftRight
import common.swapAtIndex

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16

    private val moves = input().split(",")

    override fun task1(): String {
        val dancingPrograms = DancingPrograms(('a'.code..'p'.code).map { it.toChar() }.joinToString(""))
        moves.forEach { dancingPrograms.move(it) }
        return dancingPrograms.toString()
    }

    override fun task2(): String {
        val dancingPrograms = DancingPrograms(('a'.code..'p'.code).map { it.toChar() }.joinToString(""))
        val cache = mutableMapOf<String, Int>()
        var counter = 0
        while (true) {
            moves.forEach { dancingPrograms.move(it) }
            val dancingProgramStatus = dancingPrograms.toString()
            if (cache.contains(dancingProgramStatus)) {
                return cache.filterValues { it == (1_000_000_000 % counter) - 1 }.keys.single()
            } else {
                cache[dancingProgramStatus] = counter++
            }
        }
    }

    private data class DancingPrograms(var sequence: String) {

        fun move(move: String) {
            when {
                move.startsWith("s") -> {
                    val offset = move.substring(1).toInt()
                    sequence = sequence.shiftRight(offset)
                }

                move.startsWith("x") -> {
                    val parts = move.substring(1).split("/")
                    val swap1 = parts[0].toInt()
                    val swap2 = parts[1].toInt()
                    sequence = sequence.swapAtIndex(swap1, swap2)
                }

                move.startsWith("p") -> {
                    val parts = move.substring(1).split("/")
                    val swap1 = sequence.indexOf(parts[0][0])
                    val swap2 = sequence.indexOf(parts[1][0])
                    sequence = sequence.swapAtIndex(swap1, swap2)
                }

                else -> throw IllegalArgumentException("Unknown move")
            }
        }

        override fun toString(): String = sequence
    }
}