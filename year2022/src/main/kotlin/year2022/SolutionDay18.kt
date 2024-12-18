package year2022

import common.BaseSolution

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {
    override val day = 18
    
    override fun task1(): String {
        val shape = Shape3D()
        points.forEach { p -> shape.addPoint(p) }
        return shape.openSides.toString()
    }

    override fun task2(): String {
        val shape = Shape3D()
        points.forEach { p -> shape.addPoint(p) }
        val container = Shape3DContainer(points, shape)
        container.addOutsidePoints()
        container.addInsidePoints()
        return shape.openSides.toString()
    }

    private var points: List<Point3D>

    init {
        val regex = Regex("(\\d+),(\\d+),(\\d+)")
        points = input().split("\r\n").map {
            val (x, y, z) = regex.find(it)!!.destructured
            Point3D(x.toInt(), y.toInt(), z.toInt())
        }
    }

    class Shape3DContainer(points: List<Point3D>, private val shape: Shape3D) {
        private val minX = points.minOf { it.x } - 1
        private val maxX = points.maxOf { it.x } + 1
        private val minY = points.minOf { it.y } - 1
        private val maxY = points.maxOf { it.y } + 1
        private val minZ = points.minOf { it.z } - 1
        private val maxZ = points.maxOf { it.z } + 1
        private val xRange = minX..maxX
        private val yRange = minY..maxY
        private val zRange = minZ..maxZ

        fun addInsidePoints() {
            for (x in xRange)
                for (y in yRange)
                    for (z in zRange)
                        shape.addPoint(Point3D(x, y, z))
        }

        fun addOutsidePoints() = addOutsidePoint(Point3D(minX, minY, minZ))

        private fun addOutsidePoint(p: Point3D) {
            if (shape.addOutsidePoint(p))
                p.neighbours(xRange, yRange, zRange).forEach { addOutsidePoint(it) }
        }
    }

    class Shape3D {
        private var pointMap = mutableMapOf<Int, MutableList<Point3D>>()
        var openSides = 0L

        fun addOutsidePoint(p: Point3D): Boolean {
            if (!hasPoint(p)) {
                addPointToMap(p)
                return true
            }
            return false
        }

        fun addPoint(p: Point3D) {
            if (hasPoint(p))
                return

            val neighbours1 = pointMap[p.xyz - 1]?.filter { p.isNeighbour(it) }?.size ?: 0
            val neighbours2 = pointMap[p.xyz + 1]?.filter { p.isNeighbour(it) }?.size ?: 0
            val neighbours = neighbours1 + neighbours2

            addPointToMap(p)

            openSides += 6 - (2 * neighbours)
        }

        private fun hasPoint(p: Point3D): Boolean {
            val pointGroup = pointMap[p.xyz]
            return pointGroup != null && pointGroup.contains(p)
        }

        private fun addPointToMap(p: Point3D) = pointMap.compute(p.xyz) { _, curr ->
            curr?.also { it.add(p) } ?: mutableListOf(p)
        }
    }

    data class Point3D(val x: Int, val y: Int, val z: Int) {
        fun neighbours(xRange: IntRange, yRange: IntRange, zRange: IntRange) = listOf(
            Point3D(x + 1, y, z),
            Point3D(x - 1, y, z),
            Point3D(x, y + 1, z),
            Point3D(x, y - 1, z),
            Point3D(x, y, z + 1),
            Point3D(x, y, z - 1),
        ).filter { it.x in xRange && it.y in yRange && it.z in zRange }

        val xyz = x + y + z
        private val xy = 100 * x + y
        private val xz = 100 * x + z
        private val yz = 100 * y + z

        fun isNeighbour(p: Point3D) = (p.xyz == xyz + 1 || p.xyz == xyz - 1) && (p.xy == xy || p.yz == yz || p.xz == xz)
    }
}