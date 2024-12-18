package year2021

import common.*

fun main() = println(SolutionDay25().result())

class SolutionDay25 : BaseSolution() {

    override val day = 25
    
    override fun task1(): String {
        var counter = 1L
        while (move())
            counter++
        return counter.toString()
    }

    override fun task2(): String {
        return ""
    }

    private val seaCucumbers: List<SeaCucumber>
    private val seaCucumbersMap: Map<Direction, List<SeaCucumber>>
    private val map: SeaMap

    init {
        val lines = input().split("\r\n")
        map = SeaMap(lines.first().length, lines.size)
        seaCucumbers = lines.mapIndexed { rowIdx, line ->
            line.mapIndexedNotNull { colIdx, type ->
                Direction.from(type)?.let { SeaCucumber(rowIdx, colIdx, it, map) }
            }
        }.flatten()

        seaCucumbersMap = seaCucumbers.groupBy { it.direction }
    }

    private fun move(): Boolean {
        val anyMoveEast = moveCucumbers(Direction.East)
        val anyMoveWest = moveCucumbers(Direction.South)
        return anyMoveWest || anyMoveEast
    }

    private fun moveCucumbers(direction: Direction): Boolean {
        val cucumbersPositionIdx = seaCucumbers.map { it.positionIndex() }.toHashSet()
        val eastGoingCucumbers = seaCucumbersMap[direction]!!
        val anyMove = checkIfCanMove(eastGoingCucumbers, cucumbersPositionIdx)
        eastGoingCucumbers.forEach { seaCucumber -> seaCucumber.move() }
        return anyMove
    }

    private fun checkIfCanMove(cucumbers: List<SeaCucumber>, cucumbersPositionIdx: HashSet<Int>): Boolean {
        var anyMove = false
        cucumbers.forEach { seaCucumber ->
            val nextPositionIdx = seaCucumber.nextPositionIndex()
            if (!cucumbersPositionIdx.contains(nextPositionIdx)) {
                seaCucumber.canMove = true
                anyMove = true
            }
        }

        return anyMove
    }

    data class SeaCucumber(
        var rowIdx: Int,
        var colIdx: Int,
        val direction: Direction,
        val map: SeaMap,
        var canMove: Boolean = false
    ) {
        fun move() {
            if (canMove) {
                when (direction) {
                    Direction.South -> rowIdx = (rowIdx + 1) % map.height
                    Direction.East -> colIdx = (colIdx + 1) % map.length
                }
                canMove = false
            }
        }

        fun positionIndex() = rowIdx * 1000 + colIdx

        fun nextPositionIndex(): Int {
            return when (direction) {
                Direction.South -> ((rowIdx + 1) % map.height) * 1000 + colIdx
                Direction.East -> rowIdx * 1000 + ((colIdx + 1) % map.length)
            }
        }
    }

    data class SeaMap(val length: Int, val height: Int)

    enum class Direction {
        South, East;

        companion object {
            fun from(type: Char) = when (type) {
                '>' -> East
                'v' -> South
                else -> null
            }
        }
    }
}
