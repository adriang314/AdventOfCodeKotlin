package year2019

import common.BaseSolution
import java.math.BigInteger

fun main() = println(SolutionDay24().result())

class SolutionDay24 : BaseSolution() {

    override val day = 24
    
    private val tileList = input().split("\r\n").mapIndexed { rowIdx, line ->
        line.mapIndexed { colIdx, code ->
            val counter = rowIdx * 5 + colIdx
            Tile(Position(rowIdx, colIdx, 0), TileType.from(code), BigInteger.valueOf(2).pow(counter).toLong())
        }
    }.flatten()

    override fun task1(): String {
        val availableBio = mutableSetOf<Long>()
        val tiles = Tiles(tileList)
        do {
            tiles.evaluate()
            if (availableBio.contains(tiles.biodiversity()))
                break
            availableBio.add(tiles.biodiversity())
        } while (true)
        return tiles.biodiversity().toString()
    }

    override fun task2(): String {
        val tiles = Tiles(tileList)
        repeat(200) {
            println(it)
            tiles.evaluateTask2()
        }
        val result = tiles.bugsCount()
        return result.toString()
    }

    private enum class TileType(val code: Char) {
        BUG('#'), SPACE('.');

        companion object {
            @OptIn(ExperimentalStdlibApi::class)
            fun from(code: Char) = TileType.entries.first { it.code == code }
        }
    }

    private class Tiles(list: List<Tile>) {

        private val mapTask1 = list.associateBy { it.pos }

        private var bugPositions = list.filter { it.type == TileType.BUG }.map { it.pos }.toSet()

        fun bugsCount() = bugPositions.size

        fun evaluateTask2() {
            val areaToCheck = bugPositions.map { it.multiLevelNeighbourPositions().plus(it).toList() }.flatten().toSet()
            bugPositions = areaToCheck.mapNotNull { position ->
                val bugInPosition = bugPositions.contains(position)
                val bugsAround = bugPositions.intersect(position.multiLevelNeighbourPositions().toSet()).count()
                if (bugInPosition)
                    if (bugsAround == 1) position else null
                else
                    if (bugsAround in 1..2) position else null
            }.toSet()
        }

        fun biodiversity(): Long {
            return mapTask1.values.filter { it.type == TileType.BUG }.sumOf { it.biodiversity }
        }

        fun evaluate() {
            mapTask1.values.forEach { it.evaluate(bugsAtPosition(it.pos.regularNeighbourPositions())) }
            mapTask1.values.forEach { it.executeEvaluation() }
        }

        fun bugsAtPosition(positions: List<Position>): Int {
            return mapTask1.values.filter { it.pos in positions }.count { it.type == TileType.BUG }
        }
    }

    private data class Position(
        val rowIdx: Int,
        val colIdx: Int,
        val level: Int,
    ) {
        fun regularNeighbourPositions() = listOf(
            Position(rowIdx - 1, colIdx, level),
            Position(rowIdx + 1, colIdx, level),
            Position(rowIdx, colIdx - 1, level),
            Position(rowIdx, colIdx + 1, level),
        )

        fun multiLevelNeighbourPositions() = when (rowIdx) {
            0 -> when (colIdx) {
                0 -> sequenceOf(
                    Position(1, 0, level),
                    Position(0, 1, level),
                    Position(2, 1, level + 1),
                    Position(1, 2, level + 1),
                )

                1 -> sequenceOf(
                    Position(0, 0, level),
                    Position(0, 2, level),
                    Position(1, 1, level),
                    Position(1, 2, level + 1),
                )

                2 -> sequenceOf(
                    Position(0, 1, level),
                    Position(0, 3, level),
                    Position(1, 2, level),
                    Position(1, 2, level + 1),
                )

                3 -> sequenceOf(
                    Position(0, 2, level),
                    Position(0, 4, level),
                    Position(1, 3, level),
                    Position(1, 2, level + 1),
                )

                4 -> sequenceOf(
                    Position(0, 3, level),
                    Position(1, 4, level),
                    Position(2, 3, level + 1),
                    Position(1, 2, level + 1),
                )

                else -> throw RuntimeException()
            }

            1 -> when (colIdx) {
                0 -> sequenceOf(
                    Position(0, 0, level),
                    Position(1, 1, level),
                    Position(2, 0, level),
                    Position(2, 1, level + 1),
                )

                1 -> sequenceOf(
                    Position(1, 0, level),
                    Position(1, 2, level),
                    Position(0, 1, level),
                    Position(2, 1, level),
                )

                2 -> sequenceOf(
                    Position(1, 1, level),
                    Position(1, 3, level),
                    Position(0, 2, level),
                    Position(0, 0, level - 1),
                    Position(0, 1, level - 1),
                    Position(0, 2, level - 1),
                    Position(0, 3, level - 1),
                    Position(0, 4, level - 1),
                )

                3 -> sequenceOf(
                    Position(1, 2, level),
                    Position(1, 4, level),
                    Position(0, 3, level),
                    Position(2, 3, level),
                )

                4 -> sequenceOf(
                    Position(1, 3, level),
                    Position(0, 4, level),
                    Position(2, 4, level),
                    Position(2, 3, level + 1),
                )

                else -> throw RuntimeException()
            }

            2 -> when (colIdx) {
                0 -> sequenceOf(
                    Position(1, 0, level),
                    Position(3, 0, level),
                    Position(2, 1, level),
                    Position(2, 1, level + 1),
                )

                1 -> sequenceOf(
                    Position(2, 0, level),
                    Position(1, 1, level),
                    Position(3, 1, level),
                    Position(0, 0, level - 1),
                    Position(1, 0, level - 1),
                    Position(2, 0, level - 1),
                    Position(3, 0, level - 1),
                    Position(4, 0, level - 1),
                )

                2 -> emptySequence()
                3 -> sequenceOf(
                    Position(1, 3, level),
                    Position(2, 4, level),
                    Position(3, 3, level),
                    Position(0, 4, level - 1),
                    Position(1, 4, level - 1),
                    Position(2, 4, level - 1),
                    Position(3, 4, level - 1),
                    Position(4, 4, level - 1),
                )

                4 -> sequenceOf(
                    Position(1, 4, level),
                    Position(3, 4, level),
                    Position(2, 3, level),
                    Position(2, 3, level + 1),
                )

                else -> throw RuntimeException()
            }

            3 -> when (colIdx) {
                0 -> sequenceOf(
                    Position(2, 0, level),
                    Position(4, 0, level),
                    Position(3, 1, level),
                    Position(2, 1, level + 1),
                )

                1 -> sequenceOf(
                    Position(3, 0, level),
                    Position(3, 2, level),
                    Position(2, 1, level),
                    Position(4, 1, level),
                )

                2 -> sequenceOf(
                    Position(3, 1, level),
                    Position(3, 3, level),
                    Position(4, 2, level),
                    Position(4, 0, level - 1),
                    Position(4, 1, level - 1),
                    Position(4, 2, level - 1),
                    Position(4, 3, level - 1),
                    Position(4, 4, level - 1),
                )

                3 -> sequenceOf(
                    Position(3, 2, level),
                    Position(3, 4, level),
                    Position(2, 3, level),
                    Position(4, 3, level),
                )

                4 -> sequenceOf(
                    Position(3, 3, level),
                    Position(2, 4, level),
                    Position(4, 4, level),
                    Position(2, 3, level + 1),
                )

                else -> throw RuntimeException()
            }

            4 -> when (colIdx) {
                0 -> sequenceOf(
                    Position(3, 0, level),
                    Position(4, 1, level),
                    Position(2, 1, level + 1),
                    Position(3, 2, level + 1),
                )

                1 -> sequenceOf(
                    Position(4, 0, level),
                    Position(4, 2, level),
                    Position(3, 1, level),
                    Position(3, 2, level + 1),
                )

                2 -> sequenceOf(
                    Position(4, 1, level),
                    Position(4, 3, level),
                    Position(3, 2, level),
                    Position(3, 2, level + 1),
                )

                3 -> sequenceOf(
                    Position(4, 2, level),
                    Position(4, 4, level),
                    Position(3, 3, level),
                    Position(3, 2, level + 1),
                )

                4 -> sequenceOf(
                    Position(4, 3, level),
                    Position(3, 4, level),
                    Position(2, 3, level + 1),
                    Position(3, 2, level + 1),
                )

                else -> throw RuntimeException()
            }

            else -> throw RuntimeException()

        }

    }

    private data class Tile(
        val pos: Position,
        var type: TileType,
        val biodiversity: Long,
        var newTileType: TileType? = null
    ) {
        fun evaluate(bugNeighbours: Int) {
            newTileType = if (type == TileType.BUG)
                if (bugNeighbours == 1) TileType.BUG else TileType.SPACE
            else
                if (bugNeighbours in 1..2) TileType.BUG else TileType.SPACE
        }

        fun executeEvaluation() {
            type = newTileType!!
            newTileType = null
        }
    }
}