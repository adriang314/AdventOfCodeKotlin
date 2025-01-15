package year2017

import common.BaseSolution
import common.Position
import kotlin.math.abs

fun main() = println(SolutionDay03().result())

class SolutionDay03 : BaseSolution() {

    override val day = 3

    override fun task1(): String {
        return calculateManhattanDistance(input().toInt()).toString()
    }

    override fun task2(): String {
        return findFirstLargerValue(input().toInt()).toString()
    }

    private fun calculateManhattanDistance(target: Int): Int {
        if (target == 1)
            return 0

        // Find the layer of the target number
        var layer = 0
        while ((2 * layer + 1) * (2 * layer + 1) < target) {
            layer++
        }

        // Calculate the side length of the current layer
        val sideLength = 2 * layer
        val minNumberInLayer = (2 * (layer - 1) + 1) * (2 * (layer - 1) + 1) + 1

        // Calculate the position of the target number in the current layer
        val positionInLayer = target - minNumberInLayer
        val offset = positionInLayer % sideLength

        // Calculate the Manhattan Distance
        val distanceToCenterOfSide = abs(offset - layer)
        return layer + distanceToCenterOfSide + 1
    }

    private fun findFirstLargerValue(target: Int): Int {
        val directions = listOf(Pair(1, 0), Pair(0, 1), Pair(-1, 0), Pair(0, -1))
        val grid = mutableMapOf(Position(0, 0) to 1)
        var x = 0
        var y = 0
        var step = 1
        var directionIndex = 0

        while (true) {
            for (i in 0..<2) {
                for (j in 0..<step) {
                    x += directions[directionIndex].first
                    y += directions[directionIndex].second
                    val value =
                        (-1..1).sumOf { dx ->
                            (-1..1).sumOf { dy ->
                                if (dx == 0 && dy == 0) 0 else grid.getOrDefault(Position(x + dx, y + dy), 0)
                            }
                        }
                    if (value > target)
                        return value
                    grid[Position(x, y)] = value
                }
                directionIndex = (directionIndex + 1) % 4
            }
            step++
        }
    }
}