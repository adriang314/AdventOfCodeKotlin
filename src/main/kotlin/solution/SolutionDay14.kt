package solution

import java.lang.Exception

class SolutionDay14 : BaseSolution() {

    override val day = 14

    override fun task1(): String {
        board.tiltNorth()
        return board.totalValue().toString()
    }

    override fun task2(): String {
        for (i in 0..<1_000_000_000L) {
            board.tilt()
            val key = board.toString()
            if (cache.containsKey(key)) {
                val cachedResult = cache[key]!!
                val beforeCycle = cachedResult.index
                val cycleLength = i - cachedResult.index
                val limitWithoutNotCycledAttempts = 1_000_000_000L - beforeCycle
                val tiltsLeftToDo = limitWithoutNotCycledAttempts % cycleLength
                for (j in 0..<tiltsLeftToDo - 1) {
                    board.tilt()
                }
                return board.totalValue().toString()
            }

            cache[key] = CachedResult(board.totalValue(), i)
        }
        throw Exception("No cycle found")
    }

    private var board: Board
    private val cache = mutableMapOf<String, CachedResult>()

    init {
        val rawLines = input().split("\r\n", "\n")
        val lines = rawLines.mapIndexed { index, s -> Line(s, index, rawLines.size) }
        val rocks = lines.map { it.rocks }.flatten()
        val rowLength = lines.first.line.length
        val rowNumber = lines.size
        board = Board(Rocks(rocks), rowLength, rowNumber)
    }

    data class CachedResult(val value: Long, val index: Long)

    class Board(private val rocks: Rocks, private val length: Int, private val height: Int) {

        override fun toString() = rocks.toString()

        fun totalValue() = rocks.totalValue()

        fun tilt() {
            tiltNorth()
            tiltWest()
            tiltSouth()
            tiltEast()
        }

        fun tiltNorth() {
            for (colIdx in 0..<length) {
                tilt(
                    Direction.North,
                    rocks = { rocks.getColumn(colIdx).sortedByDescending { it.rowIdx } },
                    moveBy = { prev: Rock, curr: Rock -> prev.rowIdx - curr.rowIdx - 1 },
                    finalMoveBy = { prev -> prev?.rowIdx ?: 0 }
                )
            }
        }

        private fun tiltWest() {
            for (rowIdx in 0..<height) {
                tilt(
                    Direction.West,
                    rocks = { rocks.getRow(rowIdx).sortedByDescending { it.colIdx } },
                    moveBy = { prev: Rock, curr: Rock -> prev.colIdx - curr.colIdx - 1 },
                    finalMoveBy = { prev -> prev?.colIdx ?: 0 }
                )
            }
        }

        private fun tiltSouth() {
            for (colIdx in 0..<length) {
                tilt(
                    Direction.South,
                    rocks = { rocks.getColumn(colIdx).sortedBy { it.rowIdx } },
                    moveBy = { prev: Rock, curr: Rock -> curr.rowIdx - prev.rowIdx - 1 },
                    finalMoveBy = { prev -> height - (prev?.rowIdx ?: 0) - 1 }
                )
            }
        }

        private fun tiltEast() {
            for (rowIdx in 0..<height) {
                tilt(
                    Direction.East,
                    rocks = { rocks.getRow(rowIdx).sortedBy { it.colIdx } },
                    moveBy = { prev: Rock, curr: Rock -> curr.colIdx - prev.colIdx - 1 },
                    finalMoveBy = { prev -> length - (prev?.colIdx ?: 0) - 1 }
                )
            }
        }

        private fun tilt(
            direction: Direction,
            rocks: () -> Sequence<Rock>,
            moveBy: (prev: Rock, curr: Rock) -> Int,
            finalMoveBy: (prev: Rock?) -> Int
        ) {
            val grp = mutableListOf<RockMove>()
            var movedBy = 0
            var prevRock: Rock? = null
            for (rock in rocks()) {
                if (prevRock != null) {
                    movedBy += moveBy(prevRock, rock)
                }

                prevRock = rock
                if (rock.isRoundRock) {
                    grp.forEach { it.moveBy += movedBy }
                    grp.add(RockMove(rock))
                    movedBy = 0
                } else {
                    grp.forEach { it.moveBy += movedBy }
                    grp.forEach { it.rock.move(it.moveBy, direction) }
                    grp.clear()
                    movedBy = 0
                    prevRock = null
                }
            }

            grp.forEach { it.moveBy += finalMoveBy(prevRock) }
            grp.forEach { it.rock.move(it.moveBy, direction) }
        }
    }

    class Line(val line: String, private val rowIdx: Int, private val height: Int) {
        val rocks: List<Rock> = line.mapIndexed { index, c ->
            if (c == '.')
                null
            else
                Rock(height, rowIdx, index, c == 'O', c)
        }.filterNotNull()
    }

    data class RockMove(val rock: Rock, var moveBy: Int = 0)

    enum class Direction { North, South, East, West }

    data class Rocks(private val list: List<Rock>) {

        override fun toString() = list.sortedWith(compareBy(Rock::rowIdx, Rock::colIdx)).joinToString { it.toString() }

        fun totalValue() = list.sumOf { it.value() }

        fun getRow(idx: Int) = list.asSequence().filter { it.rowIdx == idx }

        fun getColumn(idx: Int) = list.asSequence().filter { it.colIdx == idx }
    }

    data class Rock(val height: Int, var rowIdx: Int, var colIdx: Int, val isRoundRock: Boolean, val c: Char) {

        override fun toString() = "$c[$rowIdx,$colIdx]"

        fun value() = if (isRoundRock) (height - rowIdx).toLong() else 0L

        fun move(moveBy: Int, direction: Direction) {
            when (direction) {
                Direction.North -> rowIdx -= moveBy
                Direction.South -> rowIdx += moveBy
                Direction.East -> colIdx += moveBy
                Direction.West -> colIdx -= moveBy
            }
        }
    }
}