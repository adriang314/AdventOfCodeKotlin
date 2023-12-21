package solution

import java.util.LinkedList

class SolutionDay21 : BaseSolution() {

    override val day = 21

    override fun task1(): String {
        val points = Points(rawLines.mapIndexed { rowIdx, l -> Line(l, rowIdx).tiles }, rowSize, colSize)
        var i = 0L
        val targetSteps = 64L
        var fromTiles = listOf(points.startTile)
        do {
            fromTiles = points.oneMoreStep(++i, fromTiles)
        } while (i < targetSteps)

        val result = points.reachedTilesCount(targetSteps)
        return result.toString()
    }

    override fun task2(): String {
        val grownLines = growMap(rawLines, 10)
        val points = Points(grownLines.mapIndexed { rowIdx, l -> Line(l, rowIdx).tiles }, rowSize, colSize)
        var start = listOf(points.startTile)
        var i = 0L
        do {
            start = points.oneMoreStep(++i, start)
            println("iteration: $i")
        } while (i < 722L)

        val tiles720 = NormalizedTiles(720L, points.normalize(720L))
        val tiles589 = NormalizedTiles(589L, points.normalize(589L))
        val tiles458 = NormalizedTiles(458L, points.normalize(458L))

        val diff1 = diff(tiles720.tiles, tiles589.tiles)
        val diff2 = diff(tiles589.tiles, tiles458.tiles)
        val incrementDiff = diff(diff1, diff2)

        val calc =
            NormalizedTiles(
                26501365L,
                calculateReachedTiles(tiles458.tiles, 458L, incrementDiff, diff2, 26501365L, 131L)
            )
        return calc.reached.toString()
    }

    private var rawLines: List<String> = input().split("\r\n", "\n")
    private var colSize: Int = rawLines.first.length
    private var rowSize: Int = rawLines.size

    @Suppress("SameParameterValue")
    private fun calculateReachedTiles(
        initTilesAtGivenStepNumber: List<List<Long>>,
        initTilesStepNumber: Long,
        diffTilesInCycle: List<List<Long>>,
        initVsNextTilesDiff: List<List<Long>>,
        targetStepNumber: Long,
        cycleLength: Long,
    ): List<List<Long>> {
        val arr: MutableList<MutableList<Long>> = mutableListOf()
        for (i in initTilesAtGivenStepNumber.indices) {
            val currList = mutableListOf<Long>()
            arr.add(currList)
            for (j in 0..<initTilesAtGivenStepNumber[i].size) {
                val initValue = initTilesAtGivenStepNumber[i][j]
                val incrementValue = initVsNextTilesDiff[i][j]
                val cycleDiffValue = diffTilesInCycle[i][j]
                if (incrementValue == 0L)
                    currList.add(0L)
                else {
                    val targetStep = targetStepNumber / cycleLength
                    val initStep = initTilesStepNumber / cycleLength
                    val stepDiff = targetStep - initStep
                    val increment = (incrementValue * stepDiff) + cycleDiffValue * ((stepDiff) * (stepDiff - 1L) / 2L)
                    currList.add(initValue + increment)
                }
            }
        }
        return arr
    }

    private fun diff(list1: List<List<Long>>, list2: List<List<Long>>): List<List<Long>> {
        val arr: MutableList<MutableList<Long>> = mutableListOf()
        for (i in list1.indices) {
            val currList = mutableListOf<Long>()
            arr.add(currList)
            for (j in 0..<list1[i].size)
                currList.add(list1[i][j] - list2[i][j])
        }

        return arr
    }

    class NormalizedTiles(private val stepNumber: Long, val tiles: List<List<Long>>) {
        val reached = tiles.flatten().sumOf { it }

        override fun toString(): String {
            return "[Step number: $stepNumber reached: $reached] " + tiles.joinToString("#") { line ->
                line.joinToString(
                    ","
                )
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun growMap(lines: List<String>, factor: Int): List<String> {
        val tilesWithStartTile = lines.map { line ->
            var result = ""
            val cleanL = line.replace('S', '.')
            for (i in 0..<factor)
                result += cleanL
            result += line
            for (i in 0..<factor)
                result += cleanL
            result
        }

        val tilesNoStartTile = tilesWithStartTile.map { it.replace('S', '.') }

        val final = LinkedList<String>()
        for (i in 0..<factor)
            final.addAll(tilesNoStartTile)
        final.addAll(tilesWithStartTile)
        for (i in 0..<factor)
            final.addAll(tilesNoStartTile)
        return final
    }

    class Points(tiles: List<List<Tile>>, private val originalRowSize: Int, private val originalColSize: Int) {
        private val length = tiles.first.size
        private val height = tiles.size
        private val allPoints = tiles.flatten()
        var startTile: Tile = Tile(-1, -1, TileType.Start)

        init {
            for (i in 0..<height) {
                for (j in 0..<length) {
                    val curr = tiles[i][j]
                    val up = tiles.getOrNull(i - 1)?.getOrNull(j)
                    val down = tiles.getOrNull(i + 1)?.getOrNull(j)
                    val left = tiles.getOrNull(i)?.getOrNull(j - 1)
                    val right = tiles.getOrNull(i)?.getOrNull(j + 1)

                    if (up != null) curr.up = up
                    if (down != null) curr.down = down
                    if (left != null) curr.left = left
                    if (right != null) curr.right = right

                    curr.gotoList = listOfNotNull(up, down, left, right).filter { it.type != TileType.Rock }

                    if (curr.type == TileType.Start)
                        startTile = curr
                }
            }
        }

        fun normalize(stepNumber: Long): List<List<Long>> {
            val tiles: MutableList<MutableList<Long>> = mutableListOf()
            repeat(originalRowSize) {
                tiles.add(mutableListOf<Long>().also { list ->
                    repeat(originalColSize) { list.add(0L) }
                })
            }

            val oddSteps = stepNumber % 2L == 1L
            allPoints.filter { it.isInStep(stepNumber, oddSteps) }
                .forEach { tiles[it.rowIdx % originalRowSize][it.colIdx % originalColSize]++ }
            return tiles
        }

        fun reachedTilesCount(stepNumber: Long): Int {
            val oddSteps = stepNumber % 2L == 1L
            return allPoints.count { it.isInStep(stepNumber, oddSteps) }
        }

        fun oneMoreStep(stepNumber: Long, from: List<Tile>): List<Tile> {
            val result = from.map { p -> p.gotoList }.flatten().distinct()
            result.forEach {
                if (it.reachedAtStep == null) {
                    it.reachedAtStep = stepNumber
                    it.reachedAtStepEven = stepNumber % 2L == 0L
                }
            }

            return result
        }
    }

    class Line(line: String, rowIdx: Int) {
        val tiles = line.mapIndexed { colIdx, c -> Tile(rowIdx, colIdx, TileType.from(c)) }
    }

    data class Tile(val rowIdx: Int, val colIdx: Int, val type: TileType) {
        var up: Tile? = null
        var down: Tile? = null
        var left: Tile? = null
        var right: Tile? = null

        var reachedAtStep: Long? = null
        var reachedAtStepEven: Boolean? = null
        var gotoList: List<Tile> = emptyList()

        fun isInStep(steps: Long, oddSteps: Boolean) =
            reachedAtStep != null && reachedAtStep!! <= steps &&
                    ((oddSteps && reachedAtStepEven == false) || (!oddSteps && reachedAtStepEven == true))
    }

    enum class TileType {
        Start, Plot, Rock;

        companion object {
            fun from(c: Char) = when (c) {
                '#' -> Rock
                'S' -> Start
                '.' -> Plot
                else -> throw Exception("Unknown point type")
            }
        }
    }
}