package year2021

import common.BaseSolution

fun main() = println(SolutionDay02().result())

class SolutionDay02 : BaseSolution() {

    override val day = 2
    override val year = 2021

    override fun task1(): String {
        val result = executeMoves(Position(0, 0))
        return result.toString()
    }

    override fun task2(): String {
        val result = executeMoves(Position(0, 0, 0))
        return result.toString()
    }

    private var moves: List<Move> = input().split("\r\n").map {
        val lineSplit = it.split(" ")
        when (lineSplit[0]) {
            "forward" -> Forward(lineSplit[1].toInt())
            "up" -> Up(lineSplit[1].toInt())
            "down" -> Down(lineSplit[1].toInt())
            else -> throw Exception()
        }
    }

    private fun executeMoves(position: Position): Int {
        moves.forEach { it.go(position) }
        return position.depth * position.horizontal
    }

    interface Move {
        fun go(position: Position)
    }

    data class Forward(val value: Int) : Move {
        override fun go(position: Position) {
            position.horizontal += value
            if (position.aim != null)
                position.depth += position.aim!! * value
        }
    }

    data class Down(val value: Int) : Move {
        override fun go(position: Position) {
            if (position.aim == null)
                position.depth += value
            else
                position.aim = position.aim!! + value
        }
    }

    data class Up(val value: Int) : Move {
        override fun go(position: Position) {
            if (position.aim == null)
                position.depth -= value
            else
                position.aim = position.aim!! - value
        }
    }

    data class Position(var horizontal: Int, var depth: Int, var aim: Int? = null)
}