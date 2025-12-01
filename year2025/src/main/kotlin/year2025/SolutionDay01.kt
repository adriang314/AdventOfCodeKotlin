package year2025

import common.BaseSolution

fun main() = println(SolutionDay01().result())

class SolutionDay01 : BaseSolution() {

    override val day = 1

    private val moves = input().split("\r\n").map { line ->
        val rotate = when (line[0]) {
            'L' -> Rotate.Left
            'R' -> Rotate.Right
            else -> throw IllegalArgumentException("Invalid rotate direction: ${line[0]}")
        }
        val steps = line.substring(1).toInt()
        Move(rotate, steps)
    }

    override fun task1(): String {
        var position = 50
        var zeroCount = 0

        moves.forEach { move ->
            position = when (move.rotate) {
                Rotate.Left -> position - move.steps
                Rotate.Right -> position + move.steps
            }

            while (position < 0) position += 100
            while (position >= 100) position -= 100
            if (position == 0) zeroCount++
        }

        return zeroCount.toString()
    }

    override fun task2(): String {
        var position = 50
        var zeroCount = 0

        moves.forEach { move ->
            when (move.rotate) {
                Rotate.Left -> {
                    repeat(move.steps) {
                        position--
                        if (position < 0) position += 100
                        if (position == 0) zeroCount++
                    }
                }

                Rotate.Right -> {
                    repeat(move.steps) {
                        position++
                        if (position >= 100) position -= 100
                        if (position == 0) zeroCount++
                    }
                }
            }
        }

        return zeroCount.toString()
    }

    private data class Move(val rotate: Rotate, val steps: Int)

    private enum class Rotate { Left, Right }
}