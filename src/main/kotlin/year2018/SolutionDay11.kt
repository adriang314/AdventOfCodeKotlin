package year2018

import common.BaseSolution

fun main() = println(SolutionDay11().result())

class SolutionDay11 : BaseSolution() {

    override val day = 11
    override val year = 2018

    private val serialNumber = input().toInt()
    private val grid = Grid(serialNumber)

    override fun task1(): String {
        val result = grid.cellPowerLevels[3]!!.maxBy { it.value.powerLevel }.value.topLeftPosition
        return "${result.x},${result.y}"
    }

    override fun task2(): String {
        val result = grid.cellPowerLevels
            .mapValues { it.value.maxBy { v -> v.value.powerLevel }.value }
            .maxBy { it.value.powerLevel }
        return "${result.value.topLeftPosition.x},${result.value.topLeftPosition.y},${result.key}"
    }

    private data class Position(val x: Int, val y: Int)

    private data class PowerLevel(val position: Position, val serialNumber: Int) {
        val powerLevel: Int

        init {
            val rackId = position.x + 10L
            var tmpPowerLevel: Long = rackId * position.y
            tmpPowerLevel += serialNumber
            tmpPowerLevel *= rackId
            tmpPowerLevel %= 1000
            tmpPowerLevel -= (tmpPowerLevel % 100)
            tmpPowerLevel /= 100
            tmpPowerLevel -= 5
            powerLevel = tmpPowerLevel.toInt()
        }
    }

    private class Cell(val topLeftPosition: Position, val powerLevel: Int)

    private class Grid(serialNumber: Int) {
        private val size = 300
        val positions = mutableMapOf<Position, PowerLevel>()
        val cellPowerLevels = mutableMapOf<Int, MutableMap<Position, Cell>>() // key = size

        init {
            for (x in 1..size) {
                for (y in 1..size) {
                    positions[Position(x, y)] = PowerLevel(Position(x, y), serialNumber)
                }
            }

            for (cellRadius in (0..(size - 1) / 2)) {
                //println("radius = $cellRadius")
                for (x in (cellRadius + 1)..size - cellRadius) {
                    for (y in (cellRadius + 1)..size - cellRadius) {
                        val cellSize = (cellRadius * 2) + 1
                        val cellCenter = Position(x, y)
                        var cell: Cell
                        if (cellRadius == 0) {
                            cell = Cell(Position(x, y), positions[cellCenter]!!.powerLevel)
                        } else {
                            var cellPowerLevel = cellPowerLevels[cellSize - 2]!![cellCenter]!!.powerLevel

                            // top, bottom edge
                            ((-1 * cellRadius)..cellRadius).forEach { offsetX ->
                                sequenceOf((-1 * cellRadius), cellRadius).forEach { offsetY ->
                                    cellPowerLevel += positions[Position(x + offsetX, y + offsetY)]!!.powerLevel
                                }
                            }

                            // left, right edge
                            sequenceOf(-1 * cellRadius, cellRadius).forEach { offsetX ->
                                ((-1 * cellRadius) + 1..(cellRadius - 1)).forEach { offsetY ->
                                    cellPowerLevel += positions[Position(x + offsetX, y + offsetY)]!!.powerLevel
                                }
                            }

                            cell = Cell(Position(x - cellRadius, y - cellRadius), cellPowerLevel)
                        }

                        cellPowerLevels.compute(cellSize) { _, currentCellPowerLevels ->
                            var map = currentCellPowerLevels
                            if (map == null)
                                map = mutableMapOf()
                            map[cellCenter] = cell
                            map
                        }
                    }
                }
            }
        }
    }
}
