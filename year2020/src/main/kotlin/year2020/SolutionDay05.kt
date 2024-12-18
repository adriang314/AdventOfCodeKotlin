package year2020

import common.BaseSolution
import common.length

fun main() = println(SolutionDay05().result())

class SolutionDay05 : BaseSolution() {

    override val day = 5
    
    override fun task1(): String {
        val maxSeatId = seats.maxOf { it.id }
        return maxSeatId.toString()
    }

    override fun task2(): String {
        val rowWith7SeatsFull = seats.groupBy { it.rowId }.filter { it.value.size == 7 }.entries.first()
        val rowIdx = rowWith7SeatsFull.key
        val colIdx = (0..7).minus(rowWith7SeatsFull.value.map { it.columnId }.toSet()).first()
        val seatId = rowIdx * 8 + colIdx
        return seatId.toString()
    }

    private val seats = input().split("\r\n").map { line -> Seat(line.map { Type.from(it) }) }

    data class Seat(val types: List<Type>) {
        val rowId = extractId(0..127, 0)
        val columnId = extractId(0..7, 7)
        val id = rowId * 8 + columnId

        private fun extractId(range: IntRange, idx: Int): Int {
            if (range.length() == 1)
                return range.first
            return when (types[idx]) {
                Type.Back, Type.Right -> extractId(range.last - (range.length() / 2) + 1..range.last, idx + 1)
                Type.Front, Type.Left -> extractId(range.first until range.first + (range.length() / 2), idx + 1)
            }
        }
    }

    enum class Type {
        Front, Back, Left, Right;

        companion object {
            fun from(c: Char) = when (c) {
                'F' -> Front
                'B' -> Back
                'L' -> Left
                'R' -> Right
                else -> throw Exception()
            }
        }
    }
}