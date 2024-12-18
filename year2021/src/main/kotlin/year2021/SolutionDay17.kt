package year2021

import common.BaseSolution
import kotlin.math.max

fun main() = println(SolutionDay17().result())

class SolutionDay17 : BaseSolution() {

    override val day = 17
    
    override fun task1(): String {
        val result = (0..250).map { x ->
            (-150..150).map { y ->
                Probe(Velocity(x, y)).shoot(startPosition, target)
            }
        }.flatten().filter { it.hitTarget }.maxBy { it.maxHeight }
        return result.maxHeight.toString()
    }

    override fun task2(): String {
        val countResult = (0..250).map { x ->
            (-150..150).map { y ->
                Probe(Velocity(x, y)).shoot(startPosition, target)
            }
        }.flatten().count { it.hitTarget }
        return countResult.toString()
    }

    private val startPosition = Position(0, 0)
    private val target: Target

    init {
        val regex = Regex("target area: x=(-?\\d+)\\.\\.(-?\\d+), y=(-?\\d+)\\.\\.(-?\\d+)")
        val (x1, x2, y1, y2) = regex.find(input())!!.destructured

        target = Target(x1.toInt()..x2.toInt(), y1.toInt()..y2.toInt())
    }

    data class Probe(val velocity: Velocity) {

        fun shoot(position: Position, target: Target): Result {
            var currPosition = position
            var currVelocity = velocity
            var maxHeight = position.y

            do {
                val nextPosition = Position(currPosition.x + currVelocity.x, currPosition.y + currVelocity.y)
                if (nextPosition.y < target.y.first)
                    break
                currVelocity = currVelocity.decrease()
                maxHeight = max(nextPosition.y, maxHeight)
                currPosition = nextPosition
                val hitTarget = currPosition.x in target.x && currPosition.y in target.y
                if (hitTarget)
                    return Result(true, maxHeight)
            } while (true)

            return Result(false, maxHeight)
        }
    }

    data class Result(val hitTarget: Boolean, val maxHeight: Int)

    data class Target(val x: IntRange, val y: IntRange)

    data class Position(val x: Int, val y: Int)

    data class Velocity(var x: Int, val y: Int) {
        fun decrease() = copy(x = if (x > 0) x - 1 else 0, y = y - 1)
    }
}