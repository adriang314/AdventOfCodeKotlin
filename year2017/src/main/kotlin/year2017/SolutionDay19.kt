package year2017

import common.*

fun main() = println(SolutionDay19().result())

class SolutionDay19 : BaseSolution() {

    override val day = 19

    private val map = Grid(input()) { c, position -> Point(position, c) }
    private val startPoint = map.cells.single { it.position.y == 0 && it.isVerticalPath() }
    private val result = Result()

    init {
        var currentPoint = startPoint
        var currentDirection = Direction.S

        do {
            val nextPoint = map.getCell(currentPoint.position.next(currentDirection))
            if (nextPoint == null || nextPoint.isEmptySpace()) {
                break
            }

            if (nextPoint.isPathCrossing()) {
                currentDirection = nextPoint.neighboursWithDirection().single { it.first != currentDirection.turnBack() }.first

            }
            if (nextPoint.isLetter()) {
                result.lettersFound.add(nextPoint.value)
            }

            result.steps++
            currentPoint = nextPoint

        } while (true)
    }

    override fun task1(): String {
        return result.lettersFound.joinToString("")
    }

    override fun task2(): String {
        return result.steps.toString()
    }

    private class Result(val lettersFound: MutableList<Char> = mutableListOf(), var steps: Int = 1)

    private class Point(position: Position, c: Char) : Cell<Point>(position, c) {
        fun isLetter() = value.isLetter()
        fun isPathCrossing() = value == '+'
        fun isHorizontalPath() = value == '-'
        fun isHorizontalOrCrossingPath() = isHorizontalPath() || isPathCrossing() || isLetter()
        fun isVerticalPath() = value == '|'
        fun isVerticalOrCrossPath() = isVerticalPath() || isPathCrossing() || isLetter()
        fun isEmptySpace() = value == ' '

        override fun canGoN() = n != null && n!!.isVerticalOrCrossPath()
        override fun canGoS() = s != null && s!!.isVerticalOrCrossPath()
        override fun canGoW() = w != null && w!!.isHorizontalOrCrossingPath()
        override fun canGoE() = e != null && e!!.isHorizontalOrCrossingPath()
    }
}