package year2020

import common.BaseSolution

fun main() = println(SolutionDay03().result())

class SolutionDay03 : BaseSolution() {

    override val day = 3
    
    override fun task1(): String {
        return visibleTreesInPath(points[0][0], ::next3R1DPoint).toString()
    }

    override fun task2(): String {
        val trees1 = visibleTreesInPath(points[0][0], ::next1R1DPoint)
        val trees2 = visibleTreesInPath(points[0][0], ::next3R1DPoint)
        val trees3 = visibleTreesInPath(points[0][0], ::next5R1DPoint)
        val trees4 = visibleTreesInPath(points[0][0], ::next7R1DPoint)
        val trees5 = visibleTreesInPath(points[0][0], ::next1R2DPoint)
        return (1L * trees1 * trees2 * trees3 * trees4 * trees5).toString()
    }

    private fun visibleTreesInPath(startPoint: Point, nextPoint: (Point) -> Point?): Int {
        var trees = 0
        var currentPoint = startPoint
        do {
            val next = nextPoint(currentPoint)
            if (next != null) {
                if (next.isTree) {
                    trees++
                }
                currentPoint = next
            }
        } while (next != null)
        return trees
    }

    private fun next1R1DPoint(p: Point) = p.right!!.down
    private fun next3R1DPoint(p: Point) = p.right!!.right!!.right!!.down
    private fun next5R1DPoint(p: Point) = p.right!!.right!!.right!!.right!!.right!!.down
    private fun next7R1DPoint(p: Point) = p.right!!.right!!.right!!.right!!.right!!.right!!.right!!.down
    private fun next1R2DPoint(p: Point) = p.right!!.down?.down

    val points: List<List<Point>> = input()
        .split("\r\n")
        .mapIndexed { rowIdx, line -> line.mapIndexed { colIdx, c -> Point(rowIdx, colIdx, c) } }
    val height: Int = points.size
    val length: Int = points.first().size

    init {
        for (i in 0 until height) {
            for (j in 0 until length) {
                val current = points[i][j]
                val up = points.getOrNull(i - 1)?.getOrNull(j)
                val down = points.getOrNull(i + 1)?.getOrNull(j)
                val left = points.getOrNull(i)?.getOrNull(j - 1)
                val right = points.getOrNull(i)?.getOrNull((j + 1) % length)

                current.up = up
                current.down = down
                current.left = left
                current.right = right
            }
        }
    }

    class Point(val rowIdx: Int, val colIdx: Int, val value: Char) {
        val id = "[$rowIdx,$colIdx]"
        var left: Point? = null
        var right: Point? = null
        var up: Point? = null
        var down: Point? = null

        val isTree = value == '#'

        override fun toString() = "[$rowIdx,$colIdx] $value "
    }
}