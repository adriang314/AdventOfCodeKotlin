package year2016

import common.BaseSolution
import common.Direction
import common.Position

fun main() = println(SolutionDay01().result())

class SolutionDay01 : BaseSolution() {

    override val day = 1

    private val start = Position(0, 0)
    private val steps = input().split(", ").map {
        val turn = when (it[0]) {
            'L' -> Turn.LEFT
            'R' -> Turn.RIGHT
            else -> throw IllegalArgumentException("Unknown turn ${it[0]}")
        }
        val steps = it.substring(1).toInt()
        Step(turn, steps)
    }

    override fun task1(): String {
        var currentPosition = start
        var currentDirection = Direction.N
        steps.forEach { step ->
            currentDirection = when (step.turn) {
                Turn.LEFT -> currentDirection.turnLeft()
                Turn.RIGHT -> currentDirection.turnRight()
            }
            currentPosition = currentPosition.next(currentDirection, step.length)
        }

        return start.distanceTo(currentPosition).toString()
    }

    override fun task2(): String {
        var currentPosition = start
        var currentDirection = Direction.N
        val visitedPositions = mutableSetOf<Position>()

        var i = 0
        while (true) {
            val step = steps[i++ % steps.size]
            currentDirection = when (step.turn) {
                Turn.LEFT -> currentDirection.turnLeft()
                Turn.RIGHT -> currentDirection.turnRight()
            }

            repeat(step.length) {
                currentPosition = currentPosition.next(currentDirection)
                if (!visitedPositions.add(currentPosition)) {
                    return start.distanceTo(currentPosition).toString()
                }
            }
        }
    }

    private data class Step(val turn: Turn, val length: Int)

    private enum class Turn {
        LEFT, RIGHT
    }
}