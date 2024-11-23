package year2018

import common.BaseSolution
import kotlin.math.abs

fun main() = println(SolutionDay06().result())

class SolutionDay06 : BaseSolution() {

    override val day = 6
    override val year = 2018

    private val points = Points(input().split("\r\n").map { line ->
        val parts = line.split(",")
        Point(parts[0].trim().toInt(), parts[1].trim().toInt())
    })

    override fun task1(): String {
        val finitePoints = points.findFinitePoints()
        val result = finitePoints.maxOf { fp -> points.countClosest(fp) }
        return result.toString()
    }

    override fun task2(): String {
        val regions = (-500..500).sumOf { x ->
            (-500..500).mapNotNull { y ->
                val sum = points.sumOfDistances(Point(x, y))
                if (sum < 10_000) 1 else null
            }.sum()
        }

        return regions.toString()
    }

    private data class Points(val list: List<Point>) {

        fun countClosest(from: Point): Int {

            data class Processed(var processed: Boolean)

            val closestPoints = mutableMapOf(from to Processed(false))

            while (closestPoints.any { !it.value.processed }) {
                closestPoints.filter { !it.value.processed }.forEach { closestPoint ->
                    closestPoint.key.neighbours().filter { neighbour -> !closestPoints.containsKey(neighbour) }
                        .forEach { to ->
                            if (isSingleNearestDistance(from, to))
                                closestPoints[to] = Processed(false)
                        }
                    closestPoint.value.processed = true
                }
            }

            return closestPoints.size - 1
        }

        fun findFinitePoints(): List<Point> {
            list.forEach { point ->
                point.infinite = point.distantPoints()
                    .any { distantPoint -> isSingleNearestDistance(point, distantPoint) }
            }
            return list.filter { it.infinite == false }
        }


        fun sumOfDistances(to: Point): Int {
            return list.sumOf { from -> Distance(from, to).value }
        }

        private fun isSingleNearestDistance(from: Point, to: Point): Boolean {
            val nearestDistances = nearestDistances(to)
            return nearestDistances.size == 1 && nearestDistances.first().from == from
        }

        private fun nearestDistances(to: Point): List<Distance> {
            return list.map { from -> Distance(from, to) }
                .groupBy { it.value }
                .minBy { it.key }.value
        }
    }

    private data class Point(val x: Int, val y: Int, var infinite: Boolean? = null) {
        fun distantPoints(): List<Point> {
            val shift = 10_000
            return listOf(Point(x + shift, y), Point(x - shift, y), Point(x, y + shift), Point(x, y - shift))
        }

        fun neighbours() =
            listOf(Point(x + 1, y), Point(x - 1, y), Point(x, y + 1), Point(x, y - 1))
    }

    private data class Distance(val from: Point, val to: Point) {
        val value = abs(from.x - to.x) + abs(from.y - to.y)
    }
}