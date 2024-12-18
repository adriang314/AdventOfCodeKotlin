package year2022

import common.BaseSolution
import java.lang.Exception

fun main() = println(SolutionDay17().result())

private const val chamberLength = 7

class SolutionDay17 : BaseSolution() {
    override val day = 17
    
    override fun task1(): String {
        val chamber = Chamber(windDirections)
        val rockLauncher = RockLauncher()

        repeat(2022) {
            val rock = rockLauncher.nextRock(chamber.height + 4)
            chamber.pushRock(rock)
        }

        return chamber.height.toString()
    }

    override fun task2(): String {
        val chamber = Chamber(windDirections)
        val rockLauncher = RockLauncher()

        repeat(5000) {
            val rock = rockLauncher.nextRock(chamber.height + 4)
            chamber.pushRock(rock)
        }

        val rocksToCount = 1_000_000_000_000L
        val cycleSize = findCycle(chamber)

        val initRange = 0 until cycleSize
        val firstCycleRange = cycleSize until (cycleSize + cycleSize)

        val initValue = chamber.heightChanges.filterIndexed { idx, _ -> idx in initRange }.sum()
        val cycleValue = chamber.heightChanges.filterIndexed { idx, _ -> idx in firstCycleRange }.sum()

        val cyclesToCount = (rocksToCount - cycleSize) / cycleSize
        val cyclesValue = cyclesToCount * cycleValue

        val reminder = rocksToCount - (cyclesToCount * cycleSize) - cycleSize
        val reminderRange = cycleSize until (cycleSize + reminder)
        val reminderValue = chamber.heightChanges.filterIndexed { idx, _ -> idx in reminderRange }.sum()
        val result = initValue + cyclesValue + reminderValue

        return result.toString()
    }

    private fun findCycle(chamber: Chamber): Int {
        val patternStartIdx = 2000
        val patternEndIdx = 3000
        val patternSize = patternEndIdx - patternStartIdx
        val patternToSearch = chamber.heightChanges.slice(patternStartIdx..patternEndIdx)
        var nextOneIdx = patternEndIdx + 1

        while (true) {
            for (i in 0..patternSize) {
                if (patternToSearch[i] != chamber.heightChanges[nextOneIdx + i])
                    break

                if (i == patternSize)
                    return nextOneIdx - patternStartIdx
            }

            nextOneIdx++
        }
    }

    private val windDirections = input().map { WindDirection.from(it) }

    abstract class Rock {
        abstract fun minX(): Int
        abstract fun maxX(): Int
        abstract fun maxY(x: Int): Int?
        abstract fun canGoLeft(obstacles: Obstacles): Boolean
        abstract fun canGoRight(obstacles: Obstacles): Boolean
        abstract fun canGoDown(obstacles: Obstacles): Boolean
        abstract val points: List<Point>

        fun moveRight() = points.forEach { it.x++ }
        fun moveLeft() = points.forEach { it.x-- }
        fun moveDown() = points.forEach { it.y-- }

        protected fun canGoLeft(obstacles: Obstacles, vararg points: Point) =
            points.all { !obstacles.map[it.leftX()]!!.contains(it.y) }

        protected fun canGoRight(obstacles: Obstacles, vararg points: Point) =
            points.all { !obstacles.map[it.rightX()]!!.contains(it.y) }

        protected fun canGoDown(obstacles: Obstacles, vararg points: Point) =
            points.all { !obstacles.map[it.x]!!.contains(it.belowY()) }
    }

    class HorizontalRock(y: Int) : Rock() {
        override val points = listOf(Point(2, y), Point(3, y), Point(4, y), Point(5, y))
        override fun minX() = points[0].x
        override fun maxX() = points[3].x
        override fun maxY(x: Int) = if (x >= minX() && x <= maxX()) points[0].y else null

        override fun canGoLeft(obstacles: Obstacles) =
            minX() > 0 && canGoLeft(obstacles, points[0])

        override fun canGoRight(obstacles: Obstacles) =
            maxX() < chamberLength - 1 && canGoRight(obstacles, points[3])

        override fun canGoDown(obstacles: Obstacles) =
            canGoDown(obstacles, points[0], points[1], points[2], points[3])
    }

    class PlusRock(y: Int) : Rock() {
        override val points = listOf(Point(3, y), Point(2, y + 1), Point(3, y + 1), Point(4, y + 1), Point(3, y + 2))
        override fun minX() = points[1].x
        override fun maxX() = points[3].x
        override fun maxY(x: Int) =
            if (x == minX() || x == maxX()) points[1].y else if (x == points[4].x) points[4].y else null

        override fun canGoLeft(obstacles: Obstacles) =
            minX() > 0 && canGoLeft(obstacles, points[0], points[1], points[4])

        override fun canGoRight(obstacles: Obstacles) =
            maxX() < chamberLength - 1 && canGoRight(obstacles, points[0], points[3], points[4])

        override fun canGoDown(obstacles: Obstacles) =
            canGoDown(obstacles, points[0], points[1], points[3])
    }

    class EllRock(y: Int) : Rock() {
        override val points = listOf(Point(2, y), Point(3, y), Point(4, y), Point(4, y + 1), Point(4, y + 2))
        override fun minX() = points[0].x
        override fun maxX() = points[4].x
        override fun maxY(x: Int) =
            if (x == minX() || x == minX() + 1) points[1].y else if (x == points[4].x) points[4].y else null

        override fun canGoLeft(obstacles: Obstacles) =
            minX() > 0 && canGoLeft(obstacles, points[0], points[3], points[4])

        override fun canGoRight(obstacles: Obstacles) =
            maxX() < chamberLength - 1 && canGoRight(obstacles, points[2], points[3], points[4])

        override fun canGoDown(obstacles: Obstacles) =
            canGoDown(obstacles, points[0], points[1], points[2])
    }

    class VerticalRock(y: Int) : Rock() {
        override val points = listOf(Point(2, y), Point(2, y + 1), Point(2, y + 2), Point(2, y + 3))
        override fun minX() = points[0].x
        override fun maxX() = points[3].x
        override fun maxY(x: Int) = if (x == minX()) points[3].y else null

        override fun canGoLeft(obstacles: Obstacles) =
            minX() > 0 && canGoLeft(obstacles, points[0], points[1], points[2], points[3])

        override fun canGoRight(obstacles: Obstacles) =
            maxX() < chamberLength - 1 && canGoRight(obstacles, points[0], points[1], points[2], points[3])

        override fun canGoDown(obstacles: Obstacles) =
            canGoDown(obstacles, points[0])
    }

    class CubeRock(y: Int) : Rock() {
        override val points = listOf(Point(2, y), Point(3, y), Point(2, y + 1), Point(3, y + 1))
        override fun minX() = points[0].x
        override fun maxX() = points[3].x
        override fun maxY(x: Int) = if (x >= minX() && x <= maxX()) points[3].y else null

        override fun canGoLeft(obstacles: Obstacles) =
            minX() > 0 && canGoLeft(obstacles, points[0], points[2])

        override fun canGoRight(obstacles: Obstacles) =
            maxX() < chamberLength - 1 && canGoRight(obstacles, points[1], points[3])

        override fun canGoDown(obstacles: Obstacles) =
            canGoDown(obstacles, points[0], points[1])
    }

    class RockLauncher {
        private var nextRockIdx = 0

        fun nextRock(y: Int) = when (nextRockIdx++ % 5) {
            0 -> HorizontalRock(y)
            1 -> PlusRock(y)
            2 -> EllRock(y)
            3 -> VerticalRock(y)
            4 -> CubeRock(y)
            else -> throw Exception()
        }
    }

    data class Chamber(val windDirections: List<WindDirection>) {
        var height = 0

        private var nextWindIdx = 0
        private val obstacles = Obstacles()
        val heightChanges = mutableListOf<Int>()

        private fun nextWind() = windDirections[(nextWindIdx++) % windDirections.size]

        private fun canGoLeft(rock: Rock) = rock.canGoLeft(obstacles)

        private fun canGoRight(rock: Rock) = rock.canGoRight(obstacles)

        private fun canGoDown(rock: Rock) = rock.canGoDown(obstacles)

        fun pushRock(rock: Rock) {
            while (true) {
                val nextWind = nextWind()
                if (nextWind == WindDirection.Left && canGoLeft(rock)) {
                    rock.moveLeft()
                } else if (nextWind == WindDirection.Right && canGoRight(rock)) {
                    rock.moveRight()
                }

                if (canGoDown(rock))
                    rock.moveDown()
                else {
                    rock.points.forEach { obstacles.map[it.x]!!.add(it.y) }
                    val newHeight = obstacles.map.values.flatten().maxOf { it }
                    heightChanges.add(newHeight - height)
                    height = newHeight
                    return
                }
            }
        }
    }

    class Obstacles {
        val map = mutableMapOf(
            0 to mutableSetOf(0),
            1 to mutableSetOf(0),
            2 to mutableSetOf(0),
            3 to mutableSetOf(0),
            4 to mutableSetOf(0),
            5 to mutableSetOf(0),
            6 to mutableSetOf(0),
        )
    }

    data class Point(var x: Int, var y: Int) {
        fun leftX() = x - 1
        fun rightX() = x + 1
        fun belowY() = y - 1
    }

    enum class WindDirection {
        Left, Right;

        companion object {
            fun from(c: Char) = when (c) {
                '>' -> Right
                '<' -> Left
                else -> throw Exception()
            }
        }
    }
}