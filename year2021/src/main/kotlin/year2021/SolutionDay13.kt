package year2021

import common.BaseSolution

fun main() = println(SolutionDay13().result())

class SolutionDay13 : BaseSolution() {

    override val day = 13
    
    override fun task1(): String {
        points = originalPoints
        fold(foldAlong.first())
        return points.size.toString()
    }

    override fun task2(): String {
        points = originalPoints
        foldAlong.forEach { fold(it) }

        val sortedPoints = points.groupBy { it.y }.mapValues {
            it.value.sortedWith(compareBy(Point::y, Point::x))
        }

        sortedPoints.keys.sorted().forEach { y ->
            val linePoints = sortedPoints[y]!!
            (0..linePoints.last().x).forEach { x ->
                if (linePoints.contains(Point(x, y))) print("#") else print(" ")
            }
            println("")
        }

        return "PGHRKLKL"
    }

    private val foldAlong: List<FoldAlong>
    private var points: Set<Point>
    private val originalPoints: Set<Point>

    init {
        val inputParts = input().split("\r\n\r\n")
        foldAlong = inputParts.last().split("\r\n").map { line ->
            if (line.startsWith("fold along y="))
                FoldAlong(y = line.replace("fold along y=", "").toInt())
            else
                FoldAlong(x = line.replace("fold along x=", "").toInt())
        }
        points = inputParts.first().split("\r\n").map { line ->
            val (x, y) = line.split(",")
            Point(x.toInt(), y.toInt())
        }.toSet()
        originalPoints = points
    }

    private fun fold(along: FoldAlong) {
        if (along.x != null)
            foldX(along.x)
        if (along.y != null)
            foldY(along.y)
    }

    private fun foldY(foldY: Int) {
        val pointsAtFoldIndex = points.filter { it.y == foldY }
        if (pointsAtFoldIndex.isNotEmpty())
            throw Exception()

        val stayingPoints = points.filter { it.y <= foldY }.toSet()
        val foldingPoints = points.filter { it.y > foldY }.map {
            val newY = it.y - 2 * (it.y - foldY)
            if (newY < 0) throw Exception()
            Point(it.x, newY)
        }

        points = stayingPoints.plus(foldingPoints)
    }

    private fun foldX(foldX: Int) {
        val pointsAtFoldIndex = points.filter { it.x == foldX }
        if (pointsAtFoldIndex.isNotEmpty())
            throw Exception()

        val stayingPoints = points.filter { it.x <= foldX }.toSet()
        val foldingPoints = points.filter { it.x > foldX }.map {
            val newX = it.x - 2 * (it.x - foldX)
            if (newX < 0) throw Exception()
            Point(newX, it.y)
        }

        points = stayingPoints.plus(foldingPoints)
    }

    data class Point(val x: Int, val y: Int)

    data class FoldAlong(val x: Int? = null, val y: Int? = null) {
        override fun toString() = if (x != null) "x=$x" else "y=$y"
    }
}