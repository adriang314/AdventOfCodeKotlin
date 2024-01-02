package year2022

import common.BaseSolution
import kotlin.math.max
import kotlin.math.min

fun main() = println(SolutionDay14().result())

class SolutionDay14 : BaseSolution() {
    override val day = 14
    override val year = 2022

    override fun task1(): String {
        setupMap(false)
        val sandsDropped = dropSands()
        return sandsDropped.toString()
    }

    override fun task2(): String {
        setupMap(true)
        val sandsDropped = dropSands()
        return sandsDropped.toString()
    }

    private var rocks: List<Rocks>
    private val maxY: Int
    private val startPosition = Point(500, 0)
    private val map = mutableMapOf<Point, Item>()
    private val sand = Sand()
    private val rock = Rock()

    init {
        val lines = input().split("\r\n")
        rocks = lines.map { line ->
            val points = line.split(" -> ").map { Point.from(it) }
            val rocks = (0..<points.size - 1).map { Rocks(points[it], points[it + 1]) }
            rocks
        }.flatten()

        maxY = rocks.maxOf { max(it.from.y, it.to.y) }
    }

    private fun dropSands(): Int {
        var counter = 0
        while (dropSand(startPosition.copy()))
            counter++
        return counter
    }

    private fun setupMap(extraFloor: Boolean) {
        map.clear()

        rocks.forEach {
            val minX = min(it.from.x, it.to.x)
            val minY = min(it.from.y, it.to.y)
            val maxX = max(it.from.x, it.to.x)
            val maxY = max(it.from.y, it.to.y)

            for (x in minX..maxX)
                for (y in minY..maxY)
                    map[Point(x, y)] = rock
        }

        if (extraFloor)
            for (x in (500 - (3 * maxY))..(500 + (3 * maxY)))
                map[Point(x, maxY + 2)] = rock
    }

    private fun dropSand(position: Point): Boolean {
        if (position.y > maxY + 2)
            return false
        // go down
        position.y++
        if (!map.containsKey(position))
            return dropSand(position)
        // go left
        position.x--
        if (!map.containsKey(position))
            return dropSand(position)
        // go right
        position.x += 2
        if (!map.containsKey(position))
            return dropSand(position)
        // no options
        position.y--
        position.x--
        if (map.containsKey(position))
            return false
        map[position] = sand
        return true
    }

    interface Item

    class Sand : Item

    class Rock : Item

    data class Rocks(val from: Point, val to: Point)

    data class Point(var x: Int, var y: Int) {
        companion object {
            fun from(s: String): Point {
                val split = s.split(",")
                return Point(split[0].toInt(), split[1].toInt())
            }
        }
    }
}


