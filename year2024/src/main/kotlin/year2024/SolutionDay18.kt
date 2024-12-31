package year2024

import common.BaseSolution
import common.Cell
import common.Grid
import common.Position

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {

    override val day = 18

    private val bytePositions: List<Position> = input().split("\r\n")
        .map { line -> line.split(",").let { Position(it[0].toInt(), it[1].toInt()) } }

    private val mapSize = 70

    override fun task1(): String {
        val shortestPathLength = findShortestPathLength(1024)!!
        return shortestPathLength.toString()
    }

    override fun task2(): String {
        (1025..bytePositions.size).forEach { bytesToTake ->
            findShortestPathLength(bytesToTake) ?: return bytePositions[bytesToTake - 1].toString()
        }
        throw RuntimeException("Not found")
    }

    private fun findShortestPathLength(bytesToUse: Int): Int? {
        val bytes = bytePositions.take(bytesToUse)
        val builder = Grid.Builder(0..mapSize, 0..mapSize) { position -> if (bytes.contains(position)) '#' else '.' }
        val grid = Grid(builder) { c, position -> Point(position, c) }
        val startPoint = grid.getCell(Position(0, 0))!!
        val endPoint = grid.getCell(Position(mapSize, mapSize))!!
        return startPoint.findShortestPath(endPoint)?.connections
    }

    private class Point(position: Position, c: Char) : Cell<Point>(position, c) {
        override fun canGoN() = n?.value == '.'
        override fun canGoS() = s?.value == '.'
        override fun canGoW() = w?.value == '.'
        override fun canGoE() = e?.value == '.'
    }
}