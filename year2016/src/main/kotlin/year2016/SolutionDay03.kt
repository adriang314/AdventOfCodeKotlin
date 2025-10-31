package year2016

import common.BaseSolution

fun main() = println(SolutionDay03().result())

class SolutionDay03 : BaseSolution() {

    override val day = 3

    private val triangleSideLengths = input().split("\r\n").map { sideLengths ->
        sideLengths.trim().split(Regex("\\s+")).map { it.toInt() }
    }

    private val triangles1 = triangleSideLengths.map { sideLengths ->
        Triangle(sideLengths[0], sideLengths[1], sideLengths[2])
    }

    private val triangles2 = triangleSideLengths.foldIndexed(mutableListOf<Triangle>()) { index, triangles, sideLengths ->
        when (index % 3) {
            0 -> {
                triangles.add(Triangle(sideLengths[0]))
                triangles.add(Triangle(sideLengths[1]))
                triangles.add(Triangle(sideLengths[2]))
            }

            1 -> {
                triangles[triangles.size - 3].side2Length = sideLengths[0]
                triangles[triangles.size - 2].side2Length = sideLengths[1]
                triangles[triangles.size - 1].side2Length = sideLengths[2]
            }

            2 -> {
                triangles[triangles.size - 3].side3Length = sideLengths[0]
                triangles[triangles.size - 2].side3Length = sideLengths[1]
                triangles[triangles.size - 1].side3Length = sideLengths[2]
            }
        }
        triangles
    }

    override fun task1(): String {
        val validTriangles = triangles1.count { it.isValid() }
        return validTriangles.toString()
    }

    override fun task2(): String {
        val validTriangles = triangles2.count { it.isValid() }
        return validTriangles.toString()
    }

    private class Triangle(val side1Length: Int, var side2Length: Int = 0, var side3Length: Int = 0) {
        fun isValid(): Boolean {
            return side1Length + side2Length > side3Length &&
                    side1Length + side3Length > side2Length &&
                    side2Length + side3Length > side1Length
        }
    }
}