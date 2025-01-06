package year2018

import common.*

fun main() = println(SolutionDay23().result())

class SolutionDay23 : BaseSolution() {

    override val day = 23

    private val regex = Regex("^pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)$")
    private val nanobots = Nanobots(input().lines().map { line ->
        val (x, y, z, r) = regex.find(line)!!.destructured
        Nanobot(Point3D(x.toLong(), y.toLong(), z.toLong()), r.toLong())
    })
    private val zeroPosition = Point3D(0L, 0L, 0L)

    override fun task1(): String {
        val result = nanobots.inRangeOfLargestSignalRadius()
        return result.toString()
    }

    override fun task2(): String {
        var maxInRangeNanobots = setOf(Nanobot(zeroPosition, nanobots.maxRange))
        var range = nanobots.maxRange
        while (range > 0) {
            range /= 2L
            maxInRangeNanobots = maxInRangeNanobots
                .flatMap { it.split(range) }
                .fold(Pair(0, emptySet<Nanobot>())) { maxInRangeSet, nanobot ->
                    nanobots.countInRange(nanobot).let { inRangeCount ->
                        when {
                            maxInRangeSet.first < inRangeCount -> Pair(inRangeCount, setOf(nanobot))
                            maxInRangeSet.first == inRangeCount -> Pair(
                                inRangeCount,
                                maxInRangeSet.second.plus(nanobot)
                            )

                            else -> maxInRangeSet
                        }
                    }
                }.second
        }

        val result = maxInRangeNanobots.minOf { zeroPosition.distanceTo(it.position) }
        return result.toString()
    }

    private class Nanobots(val list: List<Nanobot>) {
        private val largestSignalRadiusNanobot = list.maxBy { it.range }
        private val xRange = LongRange(list.minOf { it.position.x }, list.maxOf { it.position.x })
        private val yRange = LongRange(list.minOf { it.position.y }, list.maxOf { it.position.y })
        private val zRange = LongRange(list.minOf { it.position.z }, list.maxOf { it.position.z })

        val maxRange = sequenceOf(xRange, yRange, zRange).maxOf { it.length() }

        fun countInRange(other: Nanobot) = list.count { it.hasIntersection(other) }

        fun inRangeOfLargestSignalRadius() = list.count { nanobot -> largestSignalRadiusNanobot.inRange(nanobot) }
    }

    private class Nanobot(position: Point3D, range: Long) : Sphere(position, range) {

        fun split(range: Long): Iterable<Nanobot> =
            (-1..1).flatMap { xShift ->
                (-1..1).flatMap { yShift ->
                    (-1..1).map { zShift ->
                        val newPosition = Point3D(
                            position.x + xShift * range,
                            position.y + yShift * range,
                            position.z + zShift * range
                        )
                        Nanobot(newPosition, range)
                    }
                }
            }

        fun inRange(other: Nanobot): Boolean {
            return position.distanceTo(other.position) <= range
        }
    }
}
