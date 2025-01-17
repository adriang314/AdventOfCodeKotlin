package year2017

import common.BaseSolution
import common.Point3D

fun main() = println(SolutionDay11().result())

class SolutionDay11 : BaseSolution() {

    override val day = 11

    private val directions = input().split(",").map { dir -> Direction.entries.single { it.name.equals(dir, true) } }
    private val visitedPoints = mutableSetOf<Point3D>()
    private val startPoint = Point3D(0, 0, 0)
    private val endPoint: Point3D

    init {
        var point = startPoint
        directions.forEach { dir ->
            point = when (dir) {
                Direction.N -> point.copy(y = point.y + 1, z = point.z - 1)
                Direction.NE -> point.copy(x = point.x + 1, z = point.z - 1)
                Direction.NW -> point.copy(x = point.x - 1, y = point.y + 1)
                Direction.S -> point.copy(y = point.y - 1, z = point.z + 1)
                Direction.SE -> point.copy(x = point.x + 1, y = point.y - 1)
                Direction.SW -> point.copy(x = point.x - 1, z = point.z + 1)
            }
            visitedPoints.add(point)
        }

        endPoint = point
    }

    override fun task1(): String {
        val distance = startPoint.distanceTo(endPoint) / 2
        return distance.toString()
    }

    override fun task2(): String {
        val maxDistance = visitedPoints.maxOf { startPoint.distanceTo(it) / 2 }
        return maxDistance.toString()
    }

    private enum class Direction {
        N, NE, NW, S, SE, SW
    }
}