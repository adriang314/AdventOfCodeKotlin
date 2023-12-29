package year2023

import kotlin.collections.ArrayDeque

fun main() {
    println("${SolutionDay23()}")
}

class SolutionDay23 : BaseSolution() {

    override val day = 23

    override fun task1(): String {
//        val tiles = Tiles(tiles, false)
//        val paths = tiles.getPaths()
//        val maxPath = (paths.maxOf { it.lenght } - 1)
//        return maxPath.toString()

        return ""
    }

    override fun task2(): String {
        val tiles = Tiles(tiles, true)
        val paths = tiles.getPaths()
        val maxPath = (paths.maxOf { it.lenght } - 1)
        return maxPath.toString()
    }

    private var tiles: List<List<Tile>>

    init {
        val rawLines = input().split("\r\n", "\n")
        tiles = rawLines.mapIndexed { rowIdx, line -> Line(line, rowIdx).tiles }
    }

    class Line(l: String, rowIdx: Int) {
        var tiles = l.mapIndexed { colIdx, c -> Tile(rowIdx, colIdx, TileType.from(c)) }
    }

    data class Path(var prevTile: Tile?, var currTile: Tile, var lenght: Int, val map: Map<Tile, Boolean>)

    class Tiles(tiles: List<List<Tile>>, slopesAsPaths: Boolean) {
        private val height = tiles.size
        private val length = tiles.first.size
        private val start = tiles[0][1]
        private val end = tiles.last[length - 2]

        init {
            for (i in 0..<length) {
                for (j in 0..<height) {
                    val curr = tiles[i][j]
                    val up = tiles.getOrNull(i - 1)?.getOrNull(j)
                    val down = tiles.getOrNull(i + 1)?.getOrNull(j)
                    val left = tiles.getOrNull(i)?.getOrNull(j - 1)
                    val right = tiles.getOrNull(i)?.getOrNull(j + 1)

                    curr.neighbours = if (!slopesAsPaths) {
                        when (curr.type) {
                            TileType.Forest -> emptyList()
                            TileType.Path -> listOfNotNull(up, down, left, right)
                            TileType.UpSlope -> listOfNotNull(up)
                            TileType.DownSlope -> listOfNotNull(down)
                            TileType.LeftSlope -> listOfNotNull(left)
                            TileType.RightSlope -> listOfNotNull(right)
                        }
                    } else {
                        listOfNotNull(up, down, left, right)
                    }.filter { it.type != TileType.Forest }
                        .associateWith { 1 }.toMutableMap()
                }
            }

            val tilesNoForest = tiles.flatten().filter { it.type != TileType.Forest }
            val stack = ArrayDeque<Iteration>()
            stack.add(Iteration(tilesNoForest.first, tilesNoForest.first, listOf(tilesNoForest.first)))

            while (stack.isNotEmpty()) {
                val currentIteration = stack.removeFirst()
                val iterationPrevTile = currentIteration.prev

                var prevTile = iterationPrevTile
                var currTile = currentIteration.curr
                var size = 1
                do {
                    val newCurrNeighbours =
                        currTile.neighbours.filter { it.key != prevTile && it.key != iterationPrevTile }
                    if (newCurrNeighbours.size == 1) {
                        val neighbour = newCurrNeighbours.entries.first().key
                        size++
                        if (iterationPrevTile.neighbours.containsKey(currTile)) {
                            iterationPrevTile.neighbours.remove(currTile)
                            iterationPrevTile.neighbours[neighbour] = size
                        }
                        prevTile = currTile
                        currTile = neighbour
                    }
                } while (newCurrNeighbours.size == 1)

                currTile.neighbours.remove(prevTile)
                currTile.neighbours[iterationPrevTile] = iterationPrevTile.neighbours[currTile]!!

                val newPaths = currTile.neighbours.keys
                    .filter { !currentIteration.steps.contains(it) }
                    .map { nextTile -> Iteration(currTile, nextTile, currentIteration.steps.plus(currTile)) }

                stack.addAll(newPaths)
            }
        }

        fun getPaths(): List<Path> {
            val result: MutableList<Path> = mutableListOf()
            val stack = ArrayDeque<Path>()
            stack.add(Path(null, start, 0, mapOf(start to true)))

            while (stack.isNotEmpty()) {
                val currPath = stack.removeFirst()
                val currTile = currPath.currTile
                if (currTile == end)
                    result.add(currPath)

                val newPaths = currTile.neighbours.keys
                    .filter { !currPath.map.containsKey(it) }
                    .map { nextTile ->
                        val size = currTile.neighbours[nextTile]!!
                        Path(
                            currTile, nextTile, currPath.lenght + size,
                            currPath.map.plus(currPath.currTile to true)
                        )
                    }

                stack.addAll(newPaths)
            }

            return result
        }
    }

    data class Iteration(val prev: Tile, val curr: Tile, val steps: List<Tile>)

    data class Tile(val rowIdx: Int, val colIdx: Int, val type: TileType) {
        var neighbours: MutableMap<Tile, Int> = mutableMapOf()
    }

    enum class TileType {
        Forest,
        Path,
        UpSlope,
        DownSlope,
        LeftSlope,
        RightSlope;

        companion object {
            fun from(c: Char) = when (c) {
                '#' -> Forest
                '.' -> Path
                '^' -> UpSlope
                'v' -> DownSlope
                '>' -> RightSlope
                '<' -> LeftSlope
                else -> throw Exception("Unknown type")
            }
        }
    }
}