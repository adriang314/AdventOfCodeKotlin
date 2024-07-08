package year2020

import common.BaseSolution
import java.util.LinkedList

fun main() = println(SolutionDay24().result())

class SolutionDay24 : BaseSolution() {

    override val day = 24
    override val year = 2020

    override fun task1(): String {
        val count = getInitialTiles().count { !it.value }
        return count.toString()
    }

    override fun task2(): String {
        val tiles = getInitialTiles()
        repeat(100) {
            endOfDayTilesFlip(tiles)
        }
        val count = tiles.count { !it.value }
        return count.toString()
    }

    private val tileReferences = input().split("\r\n").map { TileReference(it) }

    private fun endOfDayTilesFlip(tiles: MutableMap<TilePosition, Boolean>) {
        val tilesToFlip = mutableSetOf<TilePosition>()

        tiles.forEach { tile ->
            if (shouldFlipTile(tile.key, tiles))
                tilesToFlip.add(tile.key)

            tile.key.neighbours.forEach { neighbourTile ->
                if (shouldFlipTile(neighbourTile, tiles))
                    tilesToFlip.add(neighbourTile)
            }
        }

        tilesToFlip.forEach { tile ->
            tiles.compute(tile) { _, isWhite ->
                if (isWhite == null) false else !isWhite
            }
        }
    }

    private fun shouldFlipTile(tilePosition: TilePosition, tiles: Map<TilePosition, Boolean>): Boolean {

        fun Boolean.boolToInt() = if (this) 1 else 0

        val totalWhiteNeighbours = tilePosition.neighbours.sumOf { (tiles[it] ?: true).boolToInt() }
        val isWhite = tiles[tilePosition] ?: true

        return (isWhite && totalWhiteNeighbours == 4) ||
                (!isWhite && (totalWhiteNeighbours == 6 || totalWhiteNeighbours in (0..3)))
    }

    private fun getInitialTiles(): MutableMap<TilePosition, Boolean> {
        val tilesSet = mutableMapOf<TilePosition, Boolean>() // true if it is a white tile
        tileReferences.forEach { tileReference ->
            val tilePosition = TilePosition.from(tileReference)
            tilesSet.compute(tilePosition) { _, isWhite ->
                if (isWhite == null) false else !isWhite
            }
        }

        return tilesSet
    }

    private data class TilePosition(val rowIdx: Int, val colIdx: Int) {

        private val westNeighbour by lazy { TilePosition(rowIdx, colIdx - 1) }
        private val eastNeighbour by lazy { TilePosition(rowIdx, colIdx + 1) }
        private val southWestNeighbour by lazy { TilePosition(rowIdx - 1, colIdx - 1) }
        private val southEastNeighbour by lazy { TilePosition(rowIdx - 1, colIdx) }
        private val northWestNeighbour by lazy { TilePosition(rowIdx + 1, colIdx) }
        private val northEastNeighbour by lazy { TilePosition(rowIdx + 1, colIdx + 1) }

        val neighbours by lazy {
            listOf(
                westNeighbour,
                eastNeighbour,
                southEastNeighbour,
                southWestNeighbour,
                northEastNeighbour,
                northWestNeighbour
            )
        }

        companion object {
            fun from(reference: TileReference): TilePosition {
                var rowIdx = 0
                var colIdx = 0

                reference.directions.forEach {
                    when (it) {
                        Direction.East -> colIdx++
                        Direction.West -> colIdx--
                        Direction.SouthEast -> rowIdx--
                        Direction.NorthWest -> rowIdx++
                        Direction.SouthWest -> {
                            rowIdx--
                            colIdx--
                        }

                        Direction.NorthEast -> {
                            rowIdx++
                            colIdx++
                        }
                    }
                }

                return TilePosition(rowIdx, colIdx)
            }
        }
    }

    private data class TileReference(private val reference: String) {
        val directions: List<Direction>

        init {
            val tmpDirections = LinkedList<Direction>()
            var tmpDirection = ""
            reference.forEach {
                tmpDirection += it
                val direction = Direction.from(tmpDirection)
                if (direction != null) {
                    tmpDirections.add(direction)
                    tmpDirection = ""
                }
            }

            directions = tmpDirections
        }
    }

    private enum class Direction(val code: String) {
        East("e"),
        SouthEast("se"),
        SouthWest("sw"),
        West("w"),
        NorthWest("nw"),
        NorthEast("ne");

        companion object {
            fun from(text: String) = values().firstOrNull { it.code == text }
        }
    }
}