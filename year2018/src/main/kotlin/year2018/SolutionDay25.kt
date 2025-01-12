package year2018

import common.BaseSolution
import common.Point4D

fun main() = println(SolutionDay25().result())

class SolutionDay25 : BaseSolution() {

    override val day = 25

    private val regex = Regex("^(-?\\d+),(-?\\d+),(-?\\d+),(-?\\d+)$")
    private val points = input().split("\r\n").map { line ->
        val (x, y, z, t) = regex.find(line)!!.destructured
        Point4D(x.toLong(), y.toLong(), z.toLong(), t.toLong())
    }

    override fun task1(): String {
        val constellations = mutableListOf<Constellation>()
        points.forEach { point ->
            val matching = constellations.filter { it.contains(point) }
            constellations.removeAll(matching)
            val newConstellation = matching.fold(Constellation(point)) { current, next -> current.merge(next) }
            constellations.add(newConstellation)
        }

        return constellations.count().toString()
    }

    override fun task2(): String {
        return ""
    }

    private class Constellation(val points: Set<Point4D>) {
        constructor(point: Point4D) : this(setOf(point))

        fun contains(point: Point4D) = points.any { it.distanceTo(point) <= 3 }

        fun merge(other: Constellation) = Constellation(points union other.points)

        override fun toString() = points.map { it }.joinToString(",")
    }
}
