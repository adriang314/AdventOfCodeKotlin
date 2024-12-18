package year2022

import common.BaseSolution

fun main() = println(SolutionDay23().result())

private const val rowIndexShift = 100000L

class SolutionDay23 : BaseSolution() {
    override val day = 23
    
    override fun task1(): String {
        val game = Game(elves)
        repeat(10) { game.nextRound() }
        val result = game.score()
        return result.toString()
    }

    override fun task2(): String {
        val game = Game(elves)
        var rounds = 0
        do {
            val result = game.nextRound()
            rounds++
        } while (!result)

        return rounds.toString()
    }

    private var elves = input().split("\r\n").mapIndexed { rowIdx, s ->
        s.mapIndexed { colIdx, c ->
            if (c == '#') ElfPosition("[$rowIdx,$colIdx]", rowIdx, colIdx) else null
        }.filterNotNull()
    }.flatten().toList()

    class Game(elves: List<ElfPosition>) {
        private var map = elves.associateBy { it.positionIdx }
        private val directionRules = DirectionRules()

        fun nextRound(): Boolean {
            directionRules.next()

            map.values.forEach {
                it.nextMove = null
                it.goBackMove = null

                val areElvesInNorth = elvesInNorth(it)
                val areElvesInSouth = elvesInSouth(it)
                val areElvesInWest = elvesInWest(it)
                val areElvesIEast = elvesIEast(it)

                // find next move for each elf
                if (areElvesInNorth || areElvesInSouth || areElvesInWest || areElvesIEast) {
                    it.nextMove = directionRules.pickDirection(
                        !areElvesInNorth,
                        !areElvesInSouth,
                        !areElvesInWest,
                        !areElvesIEast
                    )
                    it.goBackMove = it.nextMove?.opposite()
                }
            }

            val noElvesMove = map.values.all { it.nextMove == null }
            if (noElvesMove)
                return true

            val elvesInNewPositionsGroup = map.values.map { it.move() }.groupBy { it.positionIdx }
            val elvesInGoodPositions = elvesInNewPositionsGroup.filter { it.value.size == 1 }.values.flatten()
            val elvesInBadPositions = elvesInNewPositionsGroup.filter { it.value.size > 1 }.values.flatten()
            val elvesInPrevPositions = elvesInBadPositions.map { it.goBack() }

            map = elvesInGoodPositions.plus(elvesInPrevPositions).associateBy { it.positionIdx }
            return false
        }

        fun score(): Long {
            val minRowIdx = map.values.minOf { it.rowIdx }
            val maxRowIdx = map.values.maxOf { it.rowIdx }
            val minColIdx = map.values.minOf { it.colIdx }
            val maxColIdx = map.values.maxOf { it.colIdx }

            val areaRowSize = maxRowIdx - minRowIdx + 1L
            val areaColSize = maxColIdx - minColIdx + 1L
            val areaSize = areaColSize * areaRowSize
            val areaWithoutElves = areaSize - map.size
            return areaWithoutElves
        }

        private fun elvesInNorth(elfPosition: ElfPosition): Boolean {
            val elfInN = elfPosition.positionIdx - rowIndexShift
            val elfInNE = elfPosition.positionIdx - rowIndexShift + 1
            val elfInNW = elfPosition.positionIdx - rowIndexShift - 1
            return map.containsKey(elfInN) || map.containsKey(elfInNW) || map.containsKey(elfInNE)
        }

        private fun elvesInSouth(elfPosition: ElfPosition): Boolean {
            val elfInS = elfPosition.positionIdx + rowIndexShift
            val elfInSE = elfPosition.positionIdx + rowIndexShift + 1
            val elfInSW = elfPosition.positionIdx + rowIndexShift - 1
            return map.containsKey(elfInS) || map.containsKey(elfInSW) || map.containsKey(elfInSE)
        }

        private fun elvesInWest(elfPosition: ElfPosition): Boolean {
            val elfInW = elfPosition.positionIdx - 1
            val elfInNW = elfPosition.positionIdx - rowIndexShift - 1
            val elfInSW = elfPosition.positionIdx + rowIndexShift - 1
            return map.containsKey(elfInW) || map.containsKey(elfInSW) || map.containsKey(elfInNW)
        }

        private fun elvesIEast(elfPosition: ElfPosition): Boolean {
            val elfInE = elfPosition.positionIdx + 1
            val elfInNE = elfPosition.positionIdx - rowIndexShift + 1
            val elfInSE = elfPosition.positionIdx + rowIndexShift + 1
            return map.containsKey(elfInE) || map.containsKey(elfInSE) || map.containsKey(elfInNE)
        }
    }

    data class ElfPosition(
        val name: String,
        val rowIdx: Int,
        val colIdx: Int,
        var nextMove: Direction? = null,
        var goBackMove: Direction? = null
    ) {
        val positionIdx = (rowIdx + 1) * rowIndexShift + colIdx

        fun move() = move(nextMove)

        fun goBack() = move(goBackMove)

        private fun move(direction: Direction?) = when (direction) {
            Direction.N -> copy(rowIdx = rowIdx - 1)
            Direction.S -> copy(rowIdx = rowIdx + 1)
            Direction.W -> copy(colIdx = colIdx - 1)
            Direction.E -> copy(colIdx = colIdx + 1)
            else -> this
        }
    }

    class DirectionRules {
        private val directions = listOf(Direction.N, Direction.S, Direction.W, Direction.E)
        private var counter = 0
        private var currentDirection: Direction? = null

        fun next() {
            currentDirection = directions[counter++ % directions.size]
        }

        fun pickDirection(goNorth: Boolean, goSouth: Boolean, goWest: Boolean, goEast: Boolean): Direction? {
            val currentDirectionIdx = directions.indexOf(currentDirection!!)
            for (i in directions.indices) {
                val direction = directions[(currentDirectionIdx + i) % directions.size]
                if (direction == Direction.N && goNorth)
                    return direction
                if (direction == Direction.S && goSouth)
                    return direction
                if (direction == Direction.W && goWest)
                    return direction
                if (direction == Direction.E && goEast)
                    return direction
            }
            return null
        }
    }

    enum class Direction {
        N, S, W, E;

        fun opposite() = when (this) {
            N -> S
            S -> N
            W -> E
            E -> W
        }
    }
}