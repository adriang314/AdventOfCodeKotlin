package year2025

import common.BaseSolution
import common.Cell
import common.Grid
import common.Position

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4

    private val map = Grid(input()) { value, position -> Point(position, value) }

    override fun task1(): String {
        return map.cells.count { it.isAccessibleRollOfPaper() }.toString()
    }

    override fun task2(): String {
        val initialRolls = map.cells.count { it.value == '@' }

        while (map.cells.any { it.isAccessibleRollOfPaper() }) {
            map.cells.forEach { it.tryRemoveRollOfPaper() }
        }

        val remainingRolls = map.cells.count { it.value == '@' }
        return (initialRolls - remainingRolls).toString()
    }

    private class Point(position: Position, c: Char) : Cell<Point>(position, c) {

        fun isRollOfPaper(): Boolean = value == '@'

        fun isAccessibleRollOfPaper(): Boolean = isRollOfPaper() && neighboursAll().count { it.isRollOfPaper() } < 4

        fun tryRemoveRollOfPaper() {
            if (isAccessibleRollOfPaper()) {
                value = '.'
            }
        }
    }
}