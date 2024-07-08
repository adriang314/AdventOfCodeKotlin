package year2020

import common.BaseSolution
import kotlin.math.absoluteValue

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {

    override val day = 12
    override val year = 2020

    override fun task1(): String {
        val shipPosition = Position(0, 0, Facing.E)
        moves.forEach { move ->
            when (move.direction) {
                Direction.L -> shipPosition.facing = shipPosition.facing.turn(Direction.L, move.value)
                Direction.R -> shipPosition.facing = shipPosition.facing.turn(Direction.R, move.value)
                Direction.N -> shipPosition.y += move.value
                Direction.S -> shipPosition.y -= move.value
                Direction.E -> shipPosition.x += move.value
                Direction.W -> shipPosition.x -= move.value
                Direction.F -> when (shipPosition.facing) {
                    Facing.E -> shipPosition.x += move.value
                    Facing.W -> shipPosition.x -= move.value
                    Facing.N -> shipPosition.y += move.value
                    Facing.S -> shipPosition.y -= move.value
                }
            }
        }
        val result = shipPosition.x.absoluteValue + shipPosition.y.absoluteValue
        return result.toString()
    }

    override fun task2(): String {
        val shipPosition = Position(0, 0, Facing.E)
        val waypointPosition = Position(10, 1, Facing.E)
        moves.forEach { move ->
            when (move.direction) {
                Direction.L -> waypointPosition.rotate(Direction.L, move.value)
                Direction.R -> waypointPosition.rotate(Direction.R, move.value)
                Direction.N -> waypointPosition.y += move.value
                Direction.S -> waypointPosition.y -= move.value
                Direction.E -> waypointPosition.x += move.value
                Direction.W -> waypointPosition.x -= move.value
                Direction.F -> {
                    shipPosition.x += waypointPosition.x * move.value
                    shipPosition.y += waypointPosition.y * move.value
                }
            }
        }
        val result = shipPosition.x.absoluteValue + shipPosition.y.absoluteValue
        return result.toString()
    }

    private val regex = Regex("(\\w)(\\d+)")
    private val moves = input().split("\r\n")
        .map {
            val (direction, value) = regex.find(it)!!.destructured
            Move(Direction.from(direction.first()), value.toInt())
        }

    data class Position(var x: Long, var y: Long, var facing: Facing) {
        fun rotate(direction: Direction, value: Int) {
            if (value == 180) {
                x *= -1
                y *= -1
            } else if ((direction == Direction.L && value == 90) || (direction == Direction.R && value == 270)) {
                val newX = -1 * y
                val newY = x
                x = newX
                y = newY
            } else if ((direction == Direction.L && value == 270) || (direction == Direction.R && value == 90)) {
                val newX = y
                val newY = -1 * x
                x = newX
                y = newY
            } else {
                throw Exception()
            }
        }
    }

    data class Move(val direction: Direction, val value: Int)

    enum class Direction {
        L, R, N, S, E, W, F;

        companion object {
            fun from(c: Char) = when (c) {
                'N' -> N
                'S' -> S
                'W' -> W
                'E' -> E
                'R' -> R
                'L' -> L
                'F' -> F
                else -> throw Exception()
            }
        }
    }

    enum class Facing(val degree: Int) {
        E(90), W(270), N(0), S(180);

        fun turn(direction: Direction, degree: Int) = when (direction) {
            Direction.L -> from(this.degree - degree)
            Direction.R -> from(this.degree + degree)
            else -> this
        }

        companion object {
            fun from(degree: Int) = Facing.values().first { it.degree == (degree + 360) % 360 }
        }
    }
}