package year2015

import common.BaseSolution
import common.Cell
import common.Grid
import common.Position

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {

    override val day = 18

    override fun task1(): String {
        val map = Grid(input()) { value, position -> Point(position, value) }
        repeat(100) {
            map.cells.forEach { it.calcNextValue() }
            map.cells.forEach { it.applyNextValue() }
        }

        return map.cells.count { it.isLightOn() }.toString()
    }

    override fun task2(): String {
        val map = Grid(input()) { value, position -> Point(position, value) }
        repeat(100) {
            map.cells.forEach { it.calcNextValue(true) }
            map.cells.forEach { it.applyNextValue() }
        }

        return map.cells.count { it.isLightOn(true) }.toString()
    }

    private class Point(position: Position, c: Char) : Cell<Point>(position, c) {
        private var nextValue: Char? = null

        private fun isCorner() = listOf(Position(0, 0), Position(0, 99), Position(99, 0), Position(99, 99)).contains(position)

        fun isLightOn(isCornerOn: Boolean = false) = if (isCornerOn && isCorner()) true else this.value == '#'

        fun calcNextValue(isCornerOn: Boolean = false) {
            val neighboursOn = neighboursAll().count { it.isLightOn(isCornerOn) }
            nextValue = when {
                isCornerOn && isCorner() -> '#'
                isLightOn(isCornerOn) -> if (neighboursOn == 2 || neighboursOn == 3) '#' else '.'
                else -> if (neighboursOn == 3) '#' else '.'
            }
        }

        fun applyNextValue() {
            this.value = nextValue!!
            nextValue = null
        }
    }
}