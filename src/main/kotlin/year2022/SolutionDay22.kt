package year2022

import common.BaseSolution

fun main() = println(SolutionDay22().result())

class SolutionDay22 : BaseSolution() {
    override val day = 22
    override val year = 2022

    override fun task1(): String {
        val result = executeMoves(startTile, moves)
        return result.toString()
    }

    override fun task2(): String {
        val size = 50
        val map = map3D.flatten()

        mapEdges(
            Triple(
                Facing.Up,
                map.filter { it.rowIdx == (0 * size) && it.colIdx in (1 * size) until (2 * size) },
                Facing.Right
            ),
            Triple(
                Facing.Left,
                map.filter { it.colIdx == (0 * size) && it.rowIdx in (3 * size) until (4 * size) },
                Facing.Down
            ),
        )

        mapEdges(
            Triple(
                Facing.Left,
                map.filter { it.colIdx == (1 * size) && it.rowIdx in (0 * size) until (1 * size) },
                Facing.Right
            ),
            Triple(
                Facing.Left,
                map.filter { it.colIdx == (0 * size) && it.rowIdx in (2 * size) until (3 * size) }.reversed(),
                Facing.Right
            ),
        )

        mapEdges(
            Triple(
                Facing.Left,
                map.filter { it.colIdx == (1 * size) && it.rowIdx in (1 * size) until (2 * size) },
                Facing.Down
            ),
            Triple(
                Facing.Up,
                map.filter { it.rowIdx == (2 * size) && it.colIdx in (0 * size) until (1 * size) },
                Facing.Right
            ),
        )

        mapEdges(
            Triple(
                Facing.Up,
                map.filter { it.rowIdx == (0 * size) && it.colIdx in (2 * size) until (3 * size) },
                Facing.Up
            ),
            Triple(
                Facing.Down,
                map.filter { it.rowIdx == (4 * size) - 1 && it.colIdx in (0 * size) until (1 * size) },
                Facing.Down
            ),
        )

        mapEdges(
            Triple(
                Facing.Right,
                map.filter { it.colIdx == (3 * size) - 1 && it.rowIdx in (0 * size) until (1 * size) },
                Facing.Left
            ),
            Triple(
                Facing.Right,
                map.filter { it.colIdx == (2 * size) - 1 && it.rowIdx in (2 * size) until (3 * size) }.reversed(),
                Facing.Left
            ),
        )

        mapEdges(
            Triple(
                Facing.Down,
                map.filter { it.rowIdx == (1 * size) - 1 && it.colIdx in (2 * size) until (3 * size) },
                Facing.Left
            ),
            Triple(
                Facing.Right,
                map.filter { it.colIdx == (2 * size) - 1 && it.rowIdx in (1 * size) until (2 * size) },
                Facing.Up
            ),
        )

        mapEdges(
            Triple(
                Facing.Down,
                map.filter { it.rowIdx == (3 * size) - 1 && it.colIdx in (1 * size) until (2 * size) },
                Facing.Left
            ),
            Triple(
                Facing.Right,
                map.filter { it.colIdx == (1 * size) - 1 && it.rowIdx in (3 * size) until (4 * size) },
                Facing.Up
            ),
        )

        val result = executeMoves(startTile3D, moves)
        return result.toString()
    }

    private fun executeMoves(startTile: Tile, moves: List<Move>): Long {
        var facing = Facing.Right
        var currTile = startTile

        moves.forEach {
            if (it is MoveForward) {
                for (i in 0 until it.steps) {
                    val nextTile = currTile.go(facing)
                    if (nextTile.type == TileType.Rock)
                        break

                    if (nextTile.rowIdx != currTile.rowIdx && nextTile.colIdx != currTile.colIdx) {
                        facing = currTile.facing[facing]!!
                    }

                    currTile = nextTile
                }
            } else if (it is Turn) {
                facing = when (it) {
                    Turn.Left -> facing.turnLeft()
                    Turn.Right -> facing.turnRight()
                }
            }
        }

        return 1000L * (currTile.rowIdx + 1) + 4 * (currTile.colIdx + 1) + facing.value
    }

    private val moves: List<Move>
    private val mapLength: Int
    private val mapHeight: Int
    private val map: List<List<Tile>>
    private val map3D: List<List<Tile>>
    private val startTile: Tile
    private val startTile3D: Tile

    init {
        val inputParts = input().split("\r\n\r\n")

        moves = inputParts.last()
            .replace("R", " R ")
            .replace("L", " L ")
            .split(" ")
            .map {
                when (it) {
                    "R" -> Turn.Right
                    "L" -> Turn.Left
                    else -> MoveForward(it.toInt())
                }
            }

        val mapParts = inputParts.first().split("\r\n")
        mapLength = mapParts.maxOf { it.length }
        mapHeight = mapParts.size
        map = mapParts.mapIndexed { x, line ->
            (0 until mapLength).map { y -> Tile(x, y, TileType.from(line.getOrElse(y) { ' ' })) }
        }

        map3D = mapParts.mapIndexed { x, line ->
            (0 until mapLength).map { y -> Tile(x, y, TileType.from(line.getOrElse(y) { ' ' })) }
        }

        startTile = map.flatten().first { it.type == TileType.Open }
        startTile3D = map3D.flatten().first { it.type == TileType.Open }

        mapMoves(map)
        mapMoves(map3D)
    }

    private fun mapEdges(from: Triple<Facing, List<Tile>, Facing>, to: Triple<Facing, List<Tile>, Facing>) {
        for (i in 0 until from.second.size) {
            val fromTile = from.second[i]
            val toTile = to.second[i]

            fromTile.facing[from.first] = from.third
            toTile.facing[to.first] = to.third

            when (from.first) {
                Facing.Up -> fromTile.up = toTile
                Facing.Down -> fromTile.down = toTile
                Facing.Left -> fromTile.left = toTile
                Facing.Right -> fromTile.right = toTile
            }

            when (to.first) {
                Facing.Up -> toTile.up = fromTile
                Facing.Down -> toTile.down = fromTile
                Facing.Left -> toTile.left = fromTile
                Facing.Right -> toTile.right = fromTile
            }
        }
    }

    private fun mapMoves(map: List<List<Tile>>) {
        for (i in 0 until mapHeight) {
            for (j in 0 until mapLength) {
                val current = map[i][j]
                if (current.type == TileType.None)
                    continue

                var up: Tile
                var ii = i - 1
                do {
                    ii = (if (ii >= 0) ii else (ii + mapHeight)) % mapHeight
                    up = map[ii--][j]
                } while (up.type == TileType.None)

                var down: Tile
                ii = i + 1
                do {
                    ii = (if (ii >= 0) ii else (ii + mapHeight)) % mapHeight
                    down = map[ii++][j]
                } while (down.type == TileType.None)

                var left: Tile
                var jj = j - 1
                do {
                    jj = (if (jj >= 0) jj else (jj + mapLength)) % mapLength
                    left = map[i][jj--]
                } while (left.type == TileType.None)

                var right: Tile
                jj = j + 1
                do {
                    jj = (if (jj >= 0) jj else (jj + mapLength)) % mapLength
                    right = map[i][jj++]
                } while (right.type == TileType.None)

                current.up = up
                current.down = down
                current.left = left
                current.right = right
            }
        }
    }

    class Tile(
        val rowIdx: Int,
        val colIdx: Int,
        val type: TileType,
        var facing: MutableMap<Facing, Facing> = mutableMapOf()
    ) {
        var left: Tile? = null
        var right: Tile? = null
        var up: Tile? = null
        var down: Tile? = null

        fun go(facing: Facing) = when (facing) {
            Facing.Up -> up!!
            Facing.Left -> left!!
            Facing.Down -> down!!
            Facing.Right -> right!!
        }

        override fun toString() = "[$rowIdx,$colIdx] $type"
    }

    enum class Facing(val value: Int) {
        Up(3), Left(2), Down(1), Right(0);

        fun turnLeft() = when (this) {
            Up -> Left
            Left -> Down
            Down -> Right
            Right -> Up
        }

        fun turnRight() = when (this) {
            Up -> Right
            Left -> Up
            Down -> Left
            Right -> Down
        }
    }

    interface Move

    data class MoveForward(val steps: Int) : Move

    enum class Turn : Move { Left, Right; }

    enum class TileType {
        None, Rock, Open;

        companion object {
            fun from(c: Char) = when (c) {
                '#' -> Rock
                '.' -> Open
                else -> None
            }
        }
    }
}