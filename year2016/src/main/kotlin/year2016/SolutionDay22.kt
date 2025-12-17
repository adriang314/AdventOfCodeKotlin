package year2016

import common.BaseSolution
import common.Cell
import common.Grid
import common.Position
import common.length

fun main() = println(SolutionDay22().result())

class SolutionDay22 : BaseSolution() {

    override val day = 22

    private val nodeStatusRegex = """/dev/grid/node-x(\d+)-y(\d+)\s+(\d+)T\s+(\d+)T\s+(\d+)T\s+(\d+)%""".toRegex()

    private val nodes = input().split("\r\n").drop(2).map {
        val (x, y, size, used, available, use) = nodeStatusRegex.find(it)!!.destructured
        Node(Position(x.toInt(), y.toInt()), size.toInt(), used.toInt(), available.toInt(), use.toInt())
    }
    private val xRange = 0..nodes.maxOf { it.position.x }
    private val yRange = 0..nodes.maxOf { it.position.y }
    private val emptyNode = nodes.single { it.used == 0 }
    private val builder = Grid.Builder(xRange, yRange) { position ->
        val node = nodes.single { it.position == position }
        when {
            node == emptyNode -> '_'
            node.used > emptyNode.size -> '#'
            else -> '.'
        }
    }
    private val grid = Grid(builder) { c, position -> Point(position, c) }
    private val topRightCornerCell = grid.getCell(Position(xRange.last, 0))!!
    private val wallLeftNode = nodes.filter { it.used > emptyNode.size }.minBy { it.position.x }
    private val passageCell = grid.getCell(wallLeftNode.position.w())!!
    private val emptyCell = grid.getCell(emptyNode.position)!!

//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ..##################################
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ....................................
//    ..................................._
//    ....................................
//    ....................................


    override fun task1(): String {
        val viablePairs = nodes.fold(0) { acc, node -> acc + nodes.count { node.viablePair(it) } }
        return viablePairs.toString()
    }

    override fun task2(): String {
        var result = 0L

        // distance from empty cell -> passage cell
        result += emptyCell.distanceTo(passageCell)

        // distance from passage cell -> west cell to the top right cell
        result += passageCell.distanceTo(topRightCornerCell.w!!)

        // moving cell by one to the left and then taking empty back to the position on the west side uses 5 steps
        result += 5L * (xRange.length() - 2)

        // final move to the top left cell
        result += 1

        return result.toString()
    }

    private data class Node(val position: Position, val size: Int, val used: Int, val available: Int, val usePercentage: Int) {

        fun viablePair(other: Node): Boolean {
            return when {
                this === other || this.used == 0 -> false
                else -> this.used <= other.available
            }
        }
    }

    private class Point(position: Position, c: Char) : Cell<Point>(position, c)
}