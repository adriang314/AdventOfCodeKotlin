package year2021

import common.*

fun main() = println(SolutionDay22().result())

class SolutionDay22 : BaseSolution() {

    override val day = 22
    override val year = 2021

    override fun task1(): String {
        val result = execute(operations.filter { it.inRange(-50, 50) })
        return result.toString()
    }

    override fun task2(): String {
        val result = execute(operations)
        return result.toString()
    }

    private var operations: List<Operation>

    init {
        val lines = input().split("\r\n")
        val regex = Regex("(\\w+) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")
        operations = lines.map {
            val (isOn, x1, x2, y1, y2, z1, z2) = regex.find(it)!!.destructured
            Operation(isOn == "on", Cube(x1.toInt()..x2.toInt(), y1.toInt()..y2.toInt(), z1.toInt()..z2.toInt()))
        }
    }

    private fun execute(operations: List<Operation>): Long {
        var cubes = listOf<Cube>()
        operations.forEach { operation ->
            cubes = cubes.map { it.remove(operation.cube) }.flatten()
            if (operation.isOn)
                cubes = cubes.plus(operation.cube)
        }
        return cubes.sumOf { it.size() }
    }

    data class Cube(val x: IntRange, val y: IntRange, val z: IntRange) {

        fun inRange(min: Int, max: Int) =
            x.first >= min && x.last <= max && y.first >= min && y.last <= max && z.first >= min && z.last <= max

        fun size(): Long = 1L * x.length() * y.length() * z.length()

        fun remove(other: Cube): List<Cube> {
            if (!hasIntersection(other))
                return listOf(this)
            if (inside(other))
                return emptyList()

            val cubes = mutableListOf<Cube>()

            if (x.first < other.x.first)
                cubes.add(Cube(x.first until other.x.first, y, z))
            if (other.x.last < x.last)
                cubes.add(Cube(other.x.last + 1..x.last, y, z))

            val xInner = this.x.intersection(other.x)
            if (y.first < other.y.first)
                cubes.add(Cube(xInner, y.first until other.y.first, z))
            if (other.y.last < y.last)
                cubes.add(Cube(xInner, other.y.last + 1..y.last, z))

            val yInner = this.y.intersection(other.y)
            if (z.first < other.z.first)
                cubes.add(Cube(xInner, yInner, z.first until other.z.first))
            if (other.z.last < z.last)
                cubes.add(Cube(xInner, yInner, other.z.last + 1..z.last))

            return cubes
        }

        fun hasIntersection(other: Cube) =
            x.hasIntersection(other.x) && y.hasIntersection(other.y) && z.hasIntersection(other.z)

        fun inside(other: Cube) =
            x.inside(other.x) && y.inside(other.y) && z.inside(other.z)
    }

    data class Operation(val isOn: Boolean, val cube: Cube) {
        fun inRange(min: Int, max: Int) = cube.inRange(min, max)
    }
}
