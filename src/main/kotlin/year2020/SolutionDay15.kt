package year2020

import common.BaseSolution

fun main() = println(SolutionDay15().result())

class SolutionDay15 : BaseSolution() {

    override val day = 15
    override val year = 2020

    override fun task1(): String {
        val number = numberAtTurn(2020L)
        return number.toString()
    }

    override fun task2(): String {
        val number = numberAtTurn(30000000L)
        return number.toString()
    }

    private val numbers = input().split(",").map { it.toLong() }

    private fun numberAtTurn(turn: Long): Long {
        val numberMap = mutableMapOf<Long, TurnInfo>()
        numbers.forEachIndexed { index, number -> numberMap[number] = TurnInfo(number, index + 1L, null) }

        var currNumber = numbers.last()
        var currTurn = numbers.size + 1L
        var spokenBefore = false
        while (currTurn <= turn) {
            val nextNumber = nextNumber(numberMap, currNumber, currTurn, spokenBefore)
            spokenBefore = numberMap.containsKey(nextNumber.number)
            numberMap[nextNumber.number] = nextNumber
            currNumber = nextNumber.number
            currTurn++
        }
        return currNumber
    }

    private fun nextNumber(
        numberMap: MutableMap<Long, TurnInfo>,
        currNumber: Long,
        currTurn: Long,
        spokenBefore: Boolean
    ): TurnInfo {
        if (!spokenBefore)
            return TurnInfo(0L, currTurn, numberMap[0L]?.turn)
        val before = numberMap[currNumber]!!
        val nextNumber = before.turn - before.prevTurn!!
        return TurnInfo(nextNumber, currTurn, numberMap[nextNumber]?.turn)
    }

    data class TurnInfo(val number: Long, val turn: Long, val prevTurn: Long?)
}