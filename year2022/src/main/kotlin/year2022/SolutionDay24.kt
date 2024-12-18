package year2022

import common.BaseSolution
import java.util.LinkedList

fun main() = println(SolutionDay24().result())

private const val rowIndexShift = 100000L

private fun positionIndex(rowIdx: Int, colIdx: Int) = (rowIdx + 1) * rowIndexShift + colIdx

class SolutionDay24 : BaseSolution() {
    override val day = 24
    
    override fun task1(): String {
        val blizzards = Blizzards(blizzardPositions())

        val finder = PathFinder(endPosition)
        finder.addState(PathFinder.State(startPosition, blizzards, 1))
        val bestPath = finder.find()

        val result = bestPath.round + -1
        return result.toString()
    }

    override fun task2(): String {
        val blizzards = Blizzards(blizzardPositions())

        val finder1 = PathFinder(endPosition)
        finder1.addState(PathFinder.State(startPosition, blizzards, 1))
        val bestPath = finder1.find()

        val finder2 = PathFinder(startPosition)
        finder2.addState(PathFinder.State(endPosition, blizzards, bestPath.round))
        val bestPath2 = finder2.find()

        val finder3 = PathFinder(endPosition)
        finder3.addState(PathFinder.State(startPosition, blizzards, bestPath2.round))
        val bestPath3 = finder3.find()

        val result = bestPath3.round + 1
        return result.toString()
    }

    private val startPosition: Position
    private val endPosition: Position
    private val valley: Valley


    init {
        val lines = input().split("\r\n")
        valley = Valley(lines.first().length - 2, lines.size - 2)
        startPosition = lines.first()
            .mapIndexed { colIdx, c ->
                if (c == '.') Position(0, colIdx).also { it.valley = valley } else null
            }.filterNotNull().first()
        endPosition = lines.last()
            .mapIndexed { colIdx, c ->
                if (c == '.') Position(lines.size - 1, colIdx).also { it.valley = valley } else null
            }.filterNotNull().first()

        valley.startPosition = startPosition
        valley.endPosition = endPosition
    }

    private fun blizzardPositions(): List<BlizzardPosition> {
        return input().split("\r\n").mapIndexed { rowIdx, s ->
            s.mapIndexed { colIdx, c ->
                val direction = Direction.from(c)
                if (direction != null)
                    BlizzardPosition(rowIdx, colIdx, direction).also { it.valley = valley }
                else
                    null
            }.filterNotNull()
        }.flatten()
    }

    class PathFinder(private val endPosition: Position) {

        private val states = LinkedList<State>()

        fun addState(state: State) = states.add(state)

        fun find(): State {

            var lastBlizzardMoveRound = 0

            do {
                val state = states.poll()!!
                val currentPosition = state.position
                val blizzards = state.blizzards
                val round = state.round

                if (currentPosition == endPosition) {
                    return state
                }

                if (lastBlizzardMoveRound != round) {
                    blizzards.move()
                    lastBlizzardMoveRound = round
                }

                val availableMoves = currentPosition.availableMoves(blizzards)
                val newStates = availableMoves.map { State(currentPosition.move(it), blizzards, round + 1) }

                states.addAll(newStates.filter { !states.contains(it) })

                // cannot stay on position with blizzard
                if (!blizzards.onPosition(currentPosition.positionIdx())) {
                    val newState = State(currentPosition, state.blizzards, round + 1)
                    if (!states.contains(newState))
                        states.add(newState)
                }
            } while (states.isNotEmpty())

            throw Exception()
        }

        data class State(val position: Position, val blizzards: Blizzards, val round: Int)
    }

    class Blizzards(blizzardPositions: List<BlizzardPosition>) {
        private var map = blizzardPositions.groupBy { it.positionIdx() }

        fun move() {
            map = map.values.flatten().map { it.move() }.groupBy { it.positionIdx() }
        }

        fun onPosition(positionIndex: Long) = map.containsKey(positionIndex)
    }

    data class Valley(val length: Int, val height: Int) {
        lateinit var startPosition: Position
        lateinit var endPosition: Position

        fun contains(x: Int, y: Int): Boolean {
            if (startPosition.rowIdx == x && startPosition.colIdx == y)
                return true
            if (endPosition.rowIdx == x && endPosition.colIdx == y)
                return true
            if (x <= 0 || y <= 0 || x > height || y > length)
                return false
            return true
        }
    }

    data class Position(val rowIdx: Int, val colIdx: Int) {
        lateinit var valley: Valley

        fun positionIdx() = positionIndex(rowIdx, colIdx)

        fun availableMoves(blizzards: Blizzards) =
            Direction.values().filter { canMove(it, blizzards) }

        private fun canMove(direction: Direction, blizzards: Blizzards): Boolean {
            return when (direction) {
                Direction.Up -> canMove(rowIdx - 1, colIdx, blizzards)
                Direction.Down -> canMove(rowIdx + 1, colIdx, blizzards)
                Direction.Left -> canMove(rowIdx, colIdx - 1, blizzards)
                Direction.Right -> canMove(rowIdx, colIdx + 1, blizzards)
            }
        }

        private fun canMove(rowIdx: Int, colIdx: Int, blizzards: Blizzards): Boolean {
            if (valley.contains(rowIdx, colIdx)) {
                val newPositionIdx = positionIndex(rowIdx, colIdx)
                return !blizzards.onPosition(newPositionIdx)
            }

            return false
        }

        fun move(direction: Direction) = when (direction) {
            Direction.Up -> copy(rowIdx = rowIdx - 1).also { it.valley = valley }
            Direction.Down -> copy(rowIdx = rowIdx + 1).also { it.valley = valley }
            Direction.Left -> copy(colIdx = colIdx - 1).also { it.valley = valley }
            Direction.Right -> copy(colIdx = colIdx + 1).also { it.valley = valley }
        }
    }

    data class BlizzardPosition(var rowIdx: Int, var colIdx: Int, val direction: Direction) {
        lateinit var valley: Valley

        fun positionIdx() = positionIndex(rowIdx, colIdx)

        fun move(): BlizzardPosition {
            when (direction) {
                Direction.Up -> {
                    if (rowIdx - 1 == 0) rowIdx = valley.height else rowIdx -= 1
                }

                Direction.Down -> {
                    if (rowIdx + 1 > valley.height) rowIdx = 1 else rowIdx += 1
                }

                Direction.Left -> {
                    if (colIdx - 1 == 0) colIdx = valley.length else colIdx -= 1
                }

                Direction.Right -> {
                    if (colIdx + 1 > valley.length) colIdx = 1 else colIdx += 1
                }
            }

            return this
        }
    }

    enum class Direction {
        Up, Down, Left, Right;

        companion object {
            fun from(c: Char) = when (c) {
                '>' -> Right
                '<' -> Left
                'v' -> Down
                '^' -> Up
                else -> null
            }
        }
    }
}