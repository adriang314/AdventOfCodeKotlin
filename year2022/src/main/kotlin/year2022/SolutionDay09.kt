package year2022

import common.BaseSolution

fun main() = println(SolutionDay09().result())

class SolutionDay09 : BaseSolution() {
    override val day = 9
    
    override fun task1(): String {
        val tailKnot = Knot("tail", Position(0, 0), null)
        val headKnot = Knot("head", Position(0, 0), tailKnot)
        executeSteps(headKnot)
        return tailKnot.allPositions.size.toString()
    }

    override fun task2(): String {
        val tailKnot = Knot("tail", Position(0, 0), null)
        val knot8 = Knot("knot8", Position(0, 0), tailKnot)
        val knot7 = Knot("knot7", Position(0, 0), knot8)
        val knot6 = Knot("knot6", Position(0, 0), knot7)
        val knot5 = Knot("knot5", Position(0, 0), knot6)
        val knot4 = Knot("knot4", Position(0, 0), knot5)
        val knot3 = Knot("knot3", Position(0, 0), knot4)
        val knot2 = Knot("knot2", Position(0, 0), knot3)
        val knot1 = Knot("knot1", Position(0, 0), knot2)
        val headKnot = Knot("head", Position(0, 0), knot1)
        executeSteps(headKnot)
        return tailKnot.allPositions.size.toString()
    }

    private var steps: List<Step>
    private val stepRegex = Regex("(\\w) (\\d+)")

    init {
        steps = input().split("\r\n").map { line ->
            val match = stepRegex.find(line)!!
            Step(Direction.from(match.groupValues[1]), match.groupValues[2].toInt())
        }
    }

    private fun executeSteps(headKnot: Knot) {
        steps.forEach {
            when (it.direction) {
                Direction.Left -> repeat(it.length) {
                    headKnot.position.x--
                    headKnot.adjustNextKnot()
                }

                Direction.Right -> repeat(it.length) {
                    headKnot.position.x++
                    headKnot.adjustNextKnot()
                }

                Direction.Up -> repeat(it.length) {
                    headKnot.position.y++
                    headKnot.adjustNextKnot()
                }

                Direction.Down -> repeat(it.length) {
                    headKnot.position.y--
                    headKnot.adjustNextKnot()
                }
            }
        }
    }

    data class Knot(val name: String, val position: Position, val nextKnot: Knot?) {
        val allPositions = mutableSetOf<Position>().also { it.add(position.copy()) }

        fun adjustNextKnot() {
            if (nextKnot != null && !position.isAdjacentTo(nextKnot.position)) {
                if (position.x != nextKnot.position.x && position.y != nextKnot.position.y) {
                    adjustX()
                    adjustY()
                } else if (position.x != nextKnot.position.x) {
                    adjustX()
                } else if (position.y != nextKnot.position.y) {
                    adjustY()
                }

                nextKnot.allPositions.add(nextKnot.position.copy())
                nextKnot.adjustNextKnot()
            }
        }

        private fun adjustX() = if (position.x > nextKnot!!.position.x) nextKnot.position.x++ else nextKnot.position.x--

        private fun adjustY() = if (position.y > nextKnot!!.position.y) nextKnot.position.y++ else nextKnot.position.y--
    }

    data class Position(var x: Int, var y: Int) {
        fun isAdjacentTo(other: Position) = other.x in (x - 1)..(x + 1) && other.y in (y - 1)..(y + 1)
    }

    data class Step(val direction: Direction, val length: Int)

    enum class Direction {
        Left, Right, Up, Down;

        companion object {
            fun from(c: String) = when (c) {
                "R" -> Right
                "L" -> Left
                "U" -> Up
                "D" -> Down
                else -> throw Exception()
            }
        }
    }
}