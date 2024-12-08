package year2024

import common.BaseSolution
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun main() = println(SolutionDay08().result())

class SolutionDay08 : BaseSolution() {

    override val day = 8
    override val year = 2024

    private val map = input().split("\r\n").mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            val position = Position(x, y)
            if (c != '.')
                position to Antenna(c, position)
            else
                position to null
        }
    }.flatten().toMap()

    private val antennaGroups = map.values.filterNotNull().groupBy { it.name }.values

    override fun task1(): String {
        val antiNodes = mutableSetOf<Position>()
        antennaGroups.forEach { group ->
            for (i in 0 until group.size - 1) {
                for (j in i + 1 until group.size) {
                    val antenna1 = group[i]
                    val antenna2 = group[j]

                    antenna1.findAntiNodes(antenna2).filter { map.contains(it) }.forEach { antiNodes.add(it) }
                }
            }
        }
        return antiNodes.size.toString()
    }

    override fun task2(): String {
        val antiNodes = mutableSetOf<Position>()
        antennaGroups.forEach { group ->
            for (i in 0 until group.size - 1) {
                for (j in i + 1 until group.size) {
                    val antenna1 = group[i]
                    val antenna2 = group[j]
                    val line = Line(antenna1.position, antenna2.position)

                    map.keys.filter { line.contains(it) }.forEach { antiNodes.add(it) }
                }
            }
        }
        return antiNodes.size.toString()
    }

    private data class Antenna(val name: Char, val position: Position) {

        fun findAntiNodes(other: Antenna): Sequence<Position> {
            if (this === other)
                throw IllegalArgumentException("Other antenna must be different")

            val distanceX = abs(position.x - other.position.x)
            val leftPosition = if (position.x <= other.position.x) position else other.position
            val rightPosition = if (leftPosition === position) other.position else position

            val line = Line(position, other.position)
            val node1 = line.findPosition(leftPosition.x - distanceX)
            val node2 = line.findPosition(rightPosition.x + distanceX)

            return sequenceOf(node1, node2)
        }
    }

    private data class Position(val x: Int, val y: Int)

    private data class Line(val p1: Position, val p2: Position) {
        private val xDiff = p1.x - p2.x
        private val yDiff = p1.y - p2.y
        private val a = 1.0 * yDiff / xDiff
        private val b = p1.y - (p1.x * a)

        private fun y(x: Int) = (a * x) + b

        fun contains(p: Position) =
            y(p.x).let {
                if ((it - it.roundToInt()).absoluteValue > 0.0001)
                    false
                else
                    it.roundToInt() == p.y
            }

        fun findPosition(x: Int) = Position(x, y(x).roundToInt())
    }
}