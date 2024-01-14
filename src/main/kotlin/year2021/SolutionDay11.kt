package year2021

import common.BaseSolution

fun main() = println(SolutionDay11().result())

class SolutionDay11 : BaseSolution() {

    override val day = 11
    override val year = 2021

    override fun task1(): String {
        val points = getPoints().flatten()
        var totalFlashes = 0L
        repeat(100) {
            totalFlashes += nextRound(points)
            points.forEach { it.resetIfFlashes() }
        }
        return totalFlashes.toString()
    }

    override fun task2(): String {
        val points = getPoints().flatten()
        var round = 0L
        do {
            val flashes = nextRound(points)
            points.forEach { it.resetIfFlashes() }
            val allFlashes = flashes == 100
            round++
        } while (!allFlashes)
        return round.toString()
    }

    private fun nextRound(points: List<Point>): Int {
        points.forEach { it.value++ }
        points.filter { it.value > 9 }.forEach { it.pointFlashes() }
        return points.count { it.flashes }
    }

    private fun getPoints(): List<List<Point>> {
        val points = input().split("\r\n")
            .mapIndexed { x, line -> line.toList().mapIndexed { y, c -> Point(x, y, c.digitToInt()) } }
        val height: Int = points.size
        val length: Int = points.first().size

        for (i in 0..<height) {
            for (j in 0..<length) {
                val current = points[i][j]
                val top = points.getOrNull(i - 1)?.getOrNull(j)
                val bottom = points.getOrNull(i + 1)?.getOrNull(j)
                val left = points.getOrNull(i)?.getOrNull(j - 1)
                val right = points.getOrNull(i)?.getOrNull(j + 1)
                val topLeft = points.getOrNull(i - 1)?.getOrNull(j - 1)
                val topRight = points.getOrNull(i - 1)?.getOrNull(j + 1)
                val bottomLeft = points.getOrNull(i + 1)?.getOrNull(j - 1)
                val bottomRight = points.getOrNull(i + 1)?.getOrNull(j + 1)

                if (top != null) current.top = top
                if (bottom != null) current.bottom = bottom
                if (left != null) current.left = left
                if (right != null) current.right = right
                if (topLeft != null) current.topLeft = topLeft
                if (topRight != null) current.topRight = topRight
                if (bottomLeft != null) current.bottomLeft = bottomLeft
                if (bottomRight != null) current.bottomRight = bottomRight

                current.neighbours = listOfNotNull(left, right, top, bottom, topLeft, topRight, bottomLeft, bottomRight)
            }
        }

        return points
    }

    data class Point(val x: Int, val y: Int, var value: Int, var flashes: Boolean = false) {
        var left: Point? = null
        var right: Point? = null
        var top: Point? = null
        var bottom: Point? = null
        var topLeft: Point? = null
        var topRight: Point? = null
        var bottomLeft: Point? = null
        var bottomRight: Point? = null
        var neighbours: List<Point> = emptyList()

        fun resetIfFlashes() {
            if (flashes) {
                flashes = false
                value = 0
            }
        }

        fun pointFlashes() {
            if (flashes)
                return

            flashes = true
            neighbours.forEach { it.value++ }
            neighbours.filter { it.value > 9 }.forEach { it.pointFlashes() }
        }
    }
}