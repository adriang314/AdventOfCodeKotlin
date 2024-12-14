package year2024

import common.BaseSolution

fun main() = println(SolutionDay14().result())

class SolutionDay14 : BaseSolution() {

    override val day = 14
    override val year = 2024

    private val regex = Regex("^p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)$")
    private val area = Area(101, 103)
    private val initPoints = input().split("\r\n").map { line ->
        val (pX, pY, vX, vY) = regex.find(line)!!.destructured
        Point(pX.toInt(), pY.toInt()).also { it.velocity = Velocity(vX.toInt(), vY.toInt()) }
    }

    override fun task1(): String {
        val points = initPoints.map { point -> point.copy().also { it.velocity = point.velocity } }
        repeat(100) {
            points.forEach { it.move(area) }
        }

        val result = points.groupBy { area.quadrant(it) }
            .filter { it.key != null }
            .map { it.value.size }
            .fold(1) { acc: Int, i: Int -> acc * i }

        return result.toString()
    }

    override fun task2(): String {
        val points = initPoints.map { point -> point.copy().also { it.velocity = point.velocity } }
        var i = 0
        while (!area.hasChristmasTree(points)) {
            i++
            points.forEach { it.move(area) }
        }

        area.print(points)
        return i.toString()
    }

    private data class Point(var x: Int, var y: Int) {
        lateinit var velocity: Velocity

        fun move(area: Area) {
            x += velocity.x
            y += velocity.y

            x %= area.wide
            y %= area.tall

            while (x < 0)
                x += area.wide
            while (y < 0)
                y += area.tall
        }
    }

    private data class Area(val wide: Int, val tall: Int) {
        private val midWide = wide / 2
        private val midTall = tall / 2
        private val quadrant1Area = Pair(0 until midWide, 0 until midTall)
        private val quadrant2Area = Pair(midWide + 1..wide, 0 until midTall)
        private val quadrant3Area = Pair(0 until midWide, midTall + 1..tall)
        private val quadrant4Area = Pair(midWide + 1..wide, midTall + 1..tall)

        fun print(points: List<Point>) {
            val pointSet = points.toSet()
            for (y in 0..tall) {
                for (x in 0..wide) {
                    if (pointSet.contains(Point(x, y))) print('#') else print(' ')
                }
                println()
            }
        }

        fun hasChristmasTree(points: List<Point>): Boolean {
            val set = points.toSet()
            val pointsWithNeighbours = points.map { point ->
                val neighbour1 = Point(point.x, point.y + 1)
                val neighbour2 = Point(point.x, point.y - 1)
                val neighbour3 = Point(point.x + 1, point.y)
                val neighbour4 = Point(point.x - 1, point.y)

                if (set.contains(neighbour1) ||
                    set.contains(neighbour2) ||
                    set.contains(neighbour3) ||
                    set.contains(neighbour4)
                ) 1 else 0
            }.sum()

            // 200 is estimation how many points may not be arranged
            return pointsWithNeighbours > points.size - 200
        }

        fun quadrant(point: Point): Int? {
            if (quadrant1Area.first.contains(point.x) && quadrant1Area.second.contains(point.y))
                return 1
            if (quadrant2Area.first.contains(point.x) && quadrant2Area.second.contains(point.y))
                return 2
            if (quadrant3Area.first.contains(point.x) && quadrant3Area.second.contains(point.y))
                return 3
            if (quadrant4Area.first.contains(point.x) && quadrant4Area.second.contains(point.y))
                return 4
            return null
        }
    }

    private data class Velocity(val x: Int, val y: Int)
}