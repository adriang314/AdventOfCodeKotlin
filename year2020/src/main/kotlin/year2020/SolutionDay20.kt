package year2020

import common.BaseSolution
import common.List2D

fun main() = println(SolutionDay20().result())

class SolutionDay20 : BaseSolution() {

    override val day = 20
    
    override fun task1(): String {
        val result = corners.scan(1L) { acc: Long, tile: Tile -> acc * tile.id }.last()
        return result.toString()
    }

    override fun task2(): String {
        val image = pictureCreator.prepare()
        val res = image.calcWaterRoughness()
        return res.toString()
    }

    private var tiles: List<Tile>
    private var inside: List<Tile>
    private var frame: List<Tile>
    private var corners: List<Tile>
    private val pictureCreator: PictureCreator

    init {
        val tilesInput = input().split("\r\n\r\n")
        tiles = tilesInput.map { tile ->
            val tileParts = tile.split("\r\n")
            val id = tileParts[0].split(" ").last().replace(":", "").toInt()
            val points = tileParts.drop(1).map { line ->
                line.toList()
            }
            Tile(id, points)
        }

        tiles.forEach { tile ->
            tile.neighbours =
                tiles.filter { it != tile }
                    .filter { otherTile -> otherTile.allIndexes.intersect(tile.allIndexes.toSet()).isNotEmpty() }
        }

        corners = tiles.filter { it.neighbours.size == 2 }
        frame = tiles.filter { it.neighbours.size == 3 }
        inside = tiles.filter { it.neighbours.size == 4 }

        pictureCreator = PictureCreator((frame.size / 4) + 2)
    }

    inner class PictureCreator(private val size: Int) {

        fun prepare(): Picture {
            arrangeTiles(corners.first())
            val image = assembleTiles()
            return image
        }

        private fun arrangeTiles(curr: Tile) {
            curr.assigned = true

            curr.neighbours.forEach { next ->
                val cwCommonEdgeIdx = next.cwIndex().filter { curr.cwIndex().contains(it) }
                val ccwCommonEdgeIdx = next.ccwIndex().filter { curr.cwIndex().contains(it) }
                if (cwCommonEdgeIdx.isNotEmpty()) {
                    val commonEdgeIdx = cwCommonEdgeIdx.single()
                    val currTileSideIdx = curr.cwIndex().indexOf(commonEdgeIdx)
                    val nextTileSideIdx = next.cwIndex().indexOf(commonEdgeIdx)
                    val rotations = (currTileSideIdx + 4 - nextTileSideIdx.oppositeIdx()) % 4
                    next.points = List2D.rotateRight(next.points, rotations)
                    if (currTileSideIdx % 2 == 0) next.points = List2D.flipVertically(next.points)
                    else next.points = List2D.flipHorizontally(next.points)
                    curr.setNeighbour(currTileSideIdx, next)
                    next.setNeighbour(currTileSideIdx.oppositeIdx(), curr)

                } else if (ccwCommonEdgeIdx.isNotEmpty()) {
                    val commonEdgeIdx = ccwCommonEdgeIdx.single()
                    val currTileSideIdx = curr.cwIndex().indexOf(commonEdgeIdx)
                    val nextTileSideIdx = next.ccwIndex().indexOf(commonEdgeIdx)
                    val rotations = (currTileSideIdx + 4 - nextTileSideIdx.oppositeIdx()) % 4
                    next.points = List2D.rotateRight(next.points, rotations)
                    curr.setNeighbour(currTileSideIdx, next)
                    next.setNeighbour(currTileSideIdx.oppositeIdx(), curr)
                } else {
                    throw RuntimeException("There must be common edge")
                }
            }

            curr.neighbours.filter { !it.assigned }.forEach { arrangeTiles(it) }
        }

        private fun Int.oppositeIdx(): Int = (this + 2) % 4

        @Suppress("USELESS_CAST")
        private fun assembleTiles(): Picture {
            val picture = Array(size) { Array(size) { null as Tile? } }
            var left = tiles.firstOrNull { it.left == null && it.top == null }

            for (y in picture.indices) {
                var right: Tile? = left
                for (x in picture[y].indices) {
                    picture[y][x] = right!!
                    right = right.right
                }
                left = left?.bottom
            }

            return Picture(picture.map { row ->
                row.map { tile ->
                    List2D.removeBorder(tile!!.points)
                }.flatMap { it.mapIndexed { i, list -> IndexedValue(i, list) } }
                    .groupBy({ (i, _) -> i }, { (_, v) -> v })
                    .map { (_, v) -> v.reduce { acc, list -> acc + list } }
            }.reduce { acc, list -> acc + list })
        }
    }

    class Picture(var parts: List<List<Char>>) {

        fun calcWaterRoughness(): Int? =
            findAllSeaMonsters()?.let { monsters -> parts.sumOf { r -> r.count { it == '#' } } - monsters * 15 }

        private fun findAllSeaMonsters(): Int? {
            countSeaMonsters().let { if (it != 0) return it }
            parts = List2D.rotateRight(parts)
            countSeaMonsters().let { if (it != 0) return it }
            parts = List2D.rotateRight(parts)
            countSeaMonsters().let { if (it != 0) return it }
            parts = List2D.rotateRight(parts)
            countSeaMonsters().let { if (it != 0) return it }
            parts = List2D.flipHorizontally(parts)
            countSeaMonsters().let { if (it != 0) return it }
            parts = List2D.rotateRight(parts)
            countSeaMonsters().let { if (it != 0) return it }
            parts = List2D.rotateRight(parts)
            countSeaMonsters().let { if (it != 0) return it }
            parts = List2D.rotateRight(parts)
            countSeaMonsters().let { if (it != 0) return it }
            return null
        }

        private fun countSeaMonsters(): Int =
            (0 until parts.size - 19).map { rowIdx ->
                (1 until parts.size - 1).map { colIdx -> isSeaMonster(rowIdx, colIdx) }
            }.flatten().count { it }

        private fun isSeaMonster(x: Int, y: Int) =
            parts[x][y] == '#' && parts[x + 18][y - 1] == '#' && parts[x + 5][y] == '#' && parts[x + 6][y] == '#' &&
                    parts[x + 11][y] == '#' && parts[x + 12][y] == '#' && parts[x + 17][y] == '#' &&
                    parts[x + 18][y] == '#' && parts[x + 19][y] == '#' && parts[x + 1][y + 1] == '#' &&
                    parts[x + 4][y + 1] == '#' && parts[x + 7][y + 1] == '#' && parts[x + 10][y + 1] == '#' &&
                    parts[x + 13][y + 1] == '#' && parts[x + 16][y + 1] == '#'
    }

    class Tile(
        val id: Int,
        var points: List<List<Char>>,
        var neighbours: List<Tile> = emptyList(),
        var assigned: Boolean = false,
    ) {
        var left: Tile? = null
        var right: Tile? = null
        var top: Tile? = null
        var bottom: Tile? = null

        fun setNeighbour(sideIdx: Int, tile: Tile) {
            when (sideIdx) {
                0 -> top = tile
                1 -> right = tile
                2 -> bottom = tile
                3 -> left = tile
            }
        }

        private fun topEdgeLR() = points.first().map { it }.joinToString("").value()
        private fun topEdgeRL() = points.first().reversed().map { it }.joinToString("").value()
        private fun bottomEdgeLR() = points.last().map { it }.joinToString("").value()
        private fun bottomEdgeRL() = points.last().reversed().map { it }.joinToString("").value()
        private fun leftEdgeUD() = points.map { it.first() }.map { it }.joinToString("").value()
        private fun leftEdgeDU() = points.map { it.first() }.reversed().map { it }.joinToString("").value()
        private fun rightEdgeUD() = points.map { it.last() }.map { it }.joinToString("").value()
        private fun rightEdgeDU() = points.map { it.last() }.reversed().map { it }.joinToString("").value()

        fun cwIndex() = listOf(topEdgeLR(), rightEdgeUD(), bottomEdgeRL(), leftEdgeDU()) // clockwise
        fun ccwIndex() = listOf(topEdgeRL(), rightEdgeDU(), bottomEdgeLR(), leftEdgeUD()) // counterclockwise
        val allIndexes = cwIndex().plus(ccwIndex())

        override fun toString() =
            "$id rotation ${cwIndex()} opposite ${ccwIndex()} neighbours ${neighbours.size} $assigned"

        companion object {
            fun Char.value() = when (this) {
                '.' -> 0
                '#' -> 1
                else -> throw RuntimeException("Unknown char")
            }

            fun String.value() = this.replace(".", "0").replace("#", "1").toInt(2)
        }
    }
}