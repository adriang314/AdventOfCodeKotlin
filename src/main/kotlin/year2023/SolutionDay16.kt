package year2023

import common.BaseSolution
import year2023.SolutionDay16.TileType.*
import year2023.SolutionDay16.Direction.*

fun main() {
    println("${SolutionDay16()}")
}

class SolutionDay16 : BaseSolution() {

    override val day = 16

    override fun task1(): String {
        val result = explorer.findMaxEnergizedTiles(tiles.topLeftTile())
        return result.toString()
    }

    override fun task2(): String {
        val result = explorer.findMaxEnergizedTiles(tiles.edgeTiles())
        return result.toString()
    }

    private var explorer: Explorer
    private var tiles: Tiles

    init {
        val rawLines = input().split("\n")
        tiles = Tiles(rawLines.mapIndexed { rowIdx, l -> Line(l, rowIdx).tiles })
        explorer = Explorer(tiles)
    }

    data class BeamMove(val tile: Tile, val from: Direction)

    class Explorer(private val tiles: Tiles) {

        fun findMaxEnergizedTiles(startTiles: List<Pair<Tile, Direction>>) =
            startTiles.maxOfOrNull {
                tiles.removeEnergy()
                move(it.first, it.second, mutableMapOf())
                tiles.energized()
            } ?: 0

        private fun move(tile: Tile, direction: Direction, beamMoves: MutableMap<BeamMove, Boolean>) {
            tile.energized = true

            val beamMove = BeamMove(tile, direction)
            if (beamMoves.containsKey(beamMove))
                return

            beamMoves[beamMove] = true

            val nextTile: Tile? = when (direction) {
                RIGHT -> tiles.get(tile.rowIdx, tile.colIdx + 1)
                LEFT -> tiles.get(tile.rowIdx, tile.colIdx - 1)
                UP -> tiles.get(tile.rowIdx - 1, tile.colIdx)
                DOWN -> tiles.get(tile.rowIdx + 1, tile.colIdx)
            }

            nextTile?.let {
                if (nextTile.canBeamMoveDown(direction))
                    move(nextTile, DOWN, beamMoves)
                if (nextTile.canBeamMoveUp(direction))
                    move(nextTile, UP, beamMoves)
                if (nextTile.canBeamMoveLeft(direction))
                    move(nextTile, LEFT, beamMoves)
                if (nextTile.canBeamMoveRight(direction))
                    move(nextTile, RIGHT, beamMoves)
            }
        }
    }

    class Line(l: String, rowIdx: Int) {
        val tiles = l.mapIndexed { colIdx, c ->
            when (c) {
                '|' -> Tile(rowIdx, colIdx, MIRROR_SPLIT_TD)
                '-' -> Tile(rowIdx, colIdx, MIRROR_SPLIT_LR)
                '\\' -> Tile(rowIdx, colIdx, MIRROR_DOWNWARD)
                '/' -> Tile(rowIdx, colIdx, MIRROR_UPWARD)
                '.' -> Tile(rowIdx, colIdx, EMPTY_SPACE)
                else -> null
            }
        }.filterNotNull()
    }

    enum class Direction { LEFT, RIGHT, UP, DOWN }

    class Tiles(private val tiles: List<List<Tile>>) {

        fun removeEnergy() = tiles.asSequence().flatten().forEach { it.energized = false }

        fun topLeftTile() = listOf(Pair(tiles[0][0], DOWN))

        fun edgeTiles(): List<Pair<Tile, Direction>> {
            val topEdge = tiles.first.filter { it.type == MIRROR_SPLIT_TD || it.type == EMPTY_SPACE }
                .map { Pair(it, DOWN) }
            val bottomEdge = tiles.last.filter { it.type == MIRROR_SPLIT_TD || it.type == EMPTY_SPACE }
                .map { Pair(it, UP) }
            val leftEdge = tiles.map { row -> row.filter { it.colIdx == 0 } }.flatten()
                .filter { it.type == MIRROR_SPLIT_LR || it.type == EMPTY_SPACE }.map { Pair(it, RIGHT) }
            val rightEdge = tiles.map { row -> row.filter { it.colIdx == row.size - 1 } }.flatten()
                .filter { it.type == MIRROR_SPLIT_LR || it.type == EMPTY_SPACE }.map { Pair(it, LEFT) }
            return topEdge.plus(bottomEdge).plus(leftEdge).plus(rightEdge)
        }

        fun energized() = tiles.flatten().count { it.energized }

        fun get(rowIdx: Int, colIdx: Int) = tiles.getOrNull(rowIdx)?.getOrNull(colIdx)
    }

    enum class TileType { MIRROR_SPLIT_LR, MIRROR_SPLIT_TD, MIRROR_UPWARD, MIRROR_DOWNWARD, EMPTY_SPACE }

    data class Tile(val rowIdx: Int, val colIdx: Int, val type: TileType, var energized: Boolean = false) {

        fun canBeamMoveUp(from: Direction) = when (from) {
            LEFT -> type == MIRROR_SPLIT_TD || type == MIRROR_DOWNWARD
            RIGHT -> type == MIRROR_SPLIT_TD || type == MIRROR_UPWARD
            UP -> type == EMPTY_SPACE || type == MIRROR_SPLIT_TD
            DOWN -> false
        }

        fun canBeamMoveDown(from: Direction) = when (from) {
            LEFT -> type == MIRROR_SPLIT_TD || type == MIRROR_UPWARD
            RIGHT -> type == MIRROR_SPLIT_TD || type == MIRROR_DOWNWARD
            UP -> false
            DOWN -> type == EMPTY_SPACE || type == MIRROR_SPLIT_TD
        }

        fun canBeamMoveRight(from: Direction) = when (from) {
            LEFT -> false
            RIGHT -> type == EMPTY_SPACE || type == MIRROR_SPLIT_LR
            UP -> type == MIRROR_SPLIT_LR || type == MIRROR_UPWARD
            DOWN -> type == MIRROR_SPLIT_LR || type == MIRROR_DOWNWARD
        }

        fun canBeamMoveLeft(from: Direction) = when (from) {
            LEFT -> type == EMPTY_SPACE || type == MIRROR_SPLIT_LR
            RIGHT -> false
            UP -> type == MIRROR_SPLIT_LR || type == MIRROR_DOWNWARD
            DOWN -> type == MIRROR_SPLIT_LR || type == MIRROR_UPWARD
        }
    }
}