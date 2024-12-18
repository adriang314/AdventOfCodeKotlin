package year2021

import common.BaseSolution

fun main() = println(SolutionDay20().result())

class SolutionDay20 : BaseSolution() {

    override val day = 20
    
    override fun task1(): String {
        val count = calculate(2)
        return count.toString()
    }

    override fun task2(): String {
        val count = calculate(50)
        return count.toString()
    }

    private var inputParts = input().split("\r\n\r\n")

    private fun calculate(times: Int): Int {
        val algorithm = Algorithm(inputParts[0], 0)
        var points = getPoints(inputParts[1])

        repeat(times) {
            points = extendPoints(points, algorithm)
            algorithm.apply(points)
        }

        return points.flatten().count { it.type == '#' }
    }

    private fun getPoints(input: String): List<List<Point>> {
        return input.split("\r\n")
            .mapIndexed { _, line -> line.toList().mapIndexed { _, c -> Point(c, c) } }
    }

    private fun mapNeighbours(points: List<List<Point>>) {
        val height: Int = points.size
        val length: Int = points.first().size

        for (rowIdx in 0 until height) {
            for (colIdx in 0 until length) {
                val current = points[rowIdx][colIdx]

                current.top = points.getOrNull(rowIdx - 1)?.getOrNull(colIdx)
                current.bottom = points.getOrNull(rowIdx + 1)?.getOrNull(colIdx)
                current.left = points.getOrNull(rowIdx)?.getOrNull(colIdx - 1)
                current.right = points.getOrNull(rowIdx)?.getOrNull(colIdx + 1)
                current.topLeft = points.getOrNull(rowIdx - 1)?.getOrNull(colIdx - 1)
                current.topRight = points.getOrNull(rowIdx - 1)?.getOrNull(colIdx + 1)
                current.bottomLeft = points.getOrNull(rowIdx + 1)?.getOrNull(colIdx - 1)
                current.bottomRight = points.getOrNull(rowIdx + 1)?.getOrNull(colIdx + 1)
            }
        }
    }

    private fun extendPoints(points: List<List<Point>>, algorithm: Algorithm): List<List<Point>> {
        val newLength = points.first().size + 2
        val extensionType = algorithm.extensionType()
        val extendedPoints = (0 until newLength).map { rowIdx ->
            if ((rowIdx - 1 < 0) || (rowIdx - 1) >= points.size)
                (0 until newLength).map { Point(extensionType, extensionType) }
            else
                listOf(Point(extensionType, extensionType))
                    .plus(points[rowIdx - 1])
                    .plus(listOf(Point(extensionType, extensionType)))
        }

        mapNeighbours(extendedPoints)
        return extendedPoints
    }

    data class Point(var type: Char, var newType: Char) {
        var left: Point? = null
        var right: Point? = null
        var top: Point? = null
        var bottom: Point? = null
        var topLeft: Point? = null
        var topRight: Point? = null
        var bottomLeft: Point? = null
        var bottomRight: Point? = null
    }

    class Algorithm(private val input: String, private var iteration: Int) {

        fun apply(points: List<List<Point>>) {
            points.flatten().forEach { it.newType = calcType(it) }
            points.flatten().forEach { it.type = it.newType }
            iteration++
        }

        fun extensionType() = if (iteration % 2 == 0) '.' else '#'

        private fun calcType(point: Point): Char {
            val builder = StringBuilder()
            builder.append(point.topLeft.type().value())
            builder.append(point.top.type().value())
            builder.append(point.topRight.type().value())
            builder.append(point.left.type().value())
            builder.append(point.type().value())
            builder.append(point.right.type().value())
            builder.append(point.bottomLeft.type().value())
            builder.append(point.bottom.type().value())
            builder.append(point.bottomRight.type().value())
            val index = builder.toString().toInt(2)
            return input[index]
        }

        private fun Point?.type() = this?.type ?: if (iteration % 2 == 0) '.' else '#'

        private fun Char.value() = if (this == '.') '0' else '1'
    }
}
