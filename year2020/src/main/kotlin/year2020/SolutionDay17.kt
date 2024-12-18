package year2020

import common.BaseSolution
import java.util.LinkedList

fun main() = println(SolutionDay17().result())

class SolutionDay17 : BaseSolution() {

    override val day = 17
    
    override fun task1(): String {
        var activePoints = initialActivePoints3D
        repeat(6) {
            activePoints = process3D(activePoints)
        }
        return activePoints.size.toString()
    }

    override fun task2(): String {
        var activePoints = initialActivePoints4D
        repeat(6) {
            activePoints = process4D(activePoints)
        }
        return activePoints.size.toString()
    }

    private var initialActivePoints3D = input().split("\r\n").mapIndexed { y, line ->
        line.mapIndexedNotNull { x, c -> if (c == '#') Point3D(x, -1 * y, 0) else null }
    }.flatten()

    private var initialActivePoints4D = input().split("\r\n").mapIndexed { y, line ->
        line.mapIndexedNotNull { x, c -> if (c == '#') Point4D(x, -1 * y, 0, 0) else null }
    }.flatten()

    private fun process3D(activePoints: List<Point3D>): List<Point3D> {
        val area = area3D(activePoints)
        val activeMap = activePoints.groupBy { it.index }
        val newActivePoints = LinkedList<Point3D>()

        for (x in area.xRange) {
            for (y in area.yRange) {
                for (z in area.zRange) {
                    val isActive = evaluateActiveState3D(x, y, z, activeMap)
                    if (isActive) {
                        newActivePoints.add(Point3D(x, y, z))
                    }
                }
            }
        }

        return newActivePoints
    }

    private fun process4D(activePoints: List<Point4D>): List<Point4D> {
        val area = area4D(activePoints)
        val activeMap = activePoints.groupBy { it.index }
        val newActivePoints = LinkedList<Point4D>()

        for (x in area.xRange) {
            for (y in area.yRange) {
                for (z in area.zRange) {
                    for (w in area.wRange) {
                        val isActive = evaluateActiveState4D(x, y, z, w, activeMap)
                        if (isActive) {
                            newActivePoints.add(Point4D(x, y, z, w))
                        }
                    }
                }
            }
        }

        return newActivePoints
    }

    private fun evaluateActiveState3D(x: Int, y: Int, z: Int, activeMap: Map<Int, List<Point3D>>): Boolean {
        val index = x + y + z
        val indexRange = index - 3..index + 3
        val isActive = activeMap[index]?.any { it.x == x && it.y == y && it.z == z } ?: false
        val activeInIndexRange = indexRange.map { activeMap[it] ?: emptyList() }
            .flatten().filter { it.isNeighbour(x, y, z) }
        return if (isActive) {
            activeInIndexRange.size == 2 || activeInIndexRange.size == 3
        } else {
            activeInIndexRange.size == 3
        }
    }

    private fun evaluateActiveState4D(x: Int, y: Int, z: Int, w: Int, activeMap: Map<Int, List<Point4D>>): Boolean {
        val index = x + y + z + w
        val indexRange = index - 4..index + 4
        val isActive = activeMap[index]?.any { it.x == x && it.y == y && it.z == z && it.w == w } ?: false
        val activeInIndexRange = indexRange.map { activeMap[it] ?: emptyList() }
            .flatten().filter { it.isNeighbour(x, y, z, w) }
        return if (isActive) {
            activeInIndexRange.size == 2 || activeInIndexRange.size == 3
        } else {
            activeInIndexRange.size == 3
        }
    }

    private fun area3D(points: List<Point3D>): Area3D {
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        var minZ = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var maxY = Int.MIN_VALUE
        var maxZ = Int.MIN_VALUE

        points.forEach {
            if (it.x < minX) minX = it.x
            if (it.y < minY) minY = it.y
            if (it.z < minZ) minZ = it.z
            if (it.x > maxX) maxX = it.x
            if (it.y > maxY) maxY = it.y
            if (it.z > maxZ) maxZ = it.z
        }

        return Area3D(minX - 1..maxX + 1, minY - 1..maxY + 1, minZ - 1..maxZ + 1)
    }

    private fun area4D(points: List<Point4D>): Area4D {
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        var minZ = Int.MAX_VALUE
        var minW = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var maxY = Int.MIN_VALUE
        var maxZ = Int.MIN_VALUE
        var maxW = Int.MIN_VALUE

        points.forEach {
            if (it.x < minX) minX = it.x
            if (it.y < minY) minY = it.y
            if (it.z < minZ) minZ = it.z
            if (it.w < minW) minW = it.w
            if (it.x > maxX) maxX = it.x
            if (it.y > maxY) maxY = it.y
            if (it.z > maxZ) maxZ = it.z
            if (it.w > maxW) maxW = it.w
        }

        return Area4D(minX - 1..maxX + 1, minY - 1..maxY + 1, minZ - 1..maxZ + 1, minW - 1..maxW + 1)
    }

    data class Area3D(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange)

    data class Area4D(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange, val wRange: IntRange)

    data class Point3D(val x: Int, val y: Int, val z: Int) {
        val index = x + y + z

        fun isNeighbour(x: Int, y: Int, z: Int): Boolean {
            if (this.x == x && this.y == y && this.z == z)
                return false
            return (this.x == x || this.x == x - 1 || this.x == x + 1) &&
                    (this.y == y || this.y == y - 1 || this.y == y + 1) &&
                    (this.z == z || this.z == z - 1 || this.z == z + 1)
        }
    }

    data class Point4D(val x: Int, val y: Int, val z: Int, val w: Int) {
        val index = x + y + z + w

        fun isNeighbour(x: Int, y: Int, z: Int, w: Int): Boolean {
            if (this.x == x && this.y == y && this.z == z && this.w == w)
                return false
            return (this.x == x || this.x == x - 1 || this.x == x + 1) &&
                    (this.y == y || this.y == y - 1 || this.y == y + 1) &&
                    (this.z == z || this.z == z - 1 || this.z == z + 1) &&
                    (this.w == w || this.w == w - 1 || this.w == w + 1)
        }
    }
}