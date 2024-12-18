package year2024

import common.BaseSolution
import common.isEven
import common.numberOfDigits

fun main() = println(SolutionDay11().result())

class SolutionDay11 : BaseSolution() {

    override val day = 11
    
    private val stones = input().split(" ").map { Stone(it.toLong()) }
    private val rules = Rules()

    override fun task1(): String {
        return blink(25).toString()
    }

    override fun task2(): String {
        return blink(75).toString()
    }

    private fun blink(times: Int): Long {
        // key = stone id, value = this stone count
        val stoneStats = mutableMapOf<Long, Long>()
        // initial state
        stones.forEach { stone -> stoneStats[stone.id] = 1 }

        repeat(times) {
            val stoneStatsChanges = mutableListOf<() -> Unit>()

            stoneStats.forEach { (stone, currCount) ->
                val result = rules.apply(stone)

                stoneStatsChanges.add {
                    // remove previous ones
                    stoneStats.compute(stone) { _, count -> count!! - currCount }
                    // add first new ones
                    stoneStats.compute(result.first) { _, count -> if (count == null) currCount else count + currCount }
                    // add second new ones - if exist
                    if (result.second != null)
                        stoneStats.compute(result.second!!) { _, count -> if (count == null) currCount else count + currCount }
                }
            }

            // apply changes to stats
            stoneStatsChanges.forEach { it() }

            // remove stones with zero instances
            stoneStats.filter { it.value == 0L }.keys.forEach { stoneStats.remove(it) }
        }

        return stoneStats.entries.sumOf { it.value }
    }

    private class Stone(val id: Long) {
        override fun toString() = id.toString()
    }

    private class Rules {
        fun apply(stone: Long): Pair<Long, Long?> {
            if (stone == 0L) {
                return Pair(1L, null)
            }

            val digits = stone.numberOfDigits()
            return if (digits.isEven()) {
                when (digits) {
                    2 -> Pair(stone / 10L, stone % 10L)
                    4 -> Pair(stone / 100L, stone % 100L)
                    6 -> Pair(stone / 1_000L, stone % 1_000L)
                    8 -> Pair(stone / 10_000L, stone % 10_000L)
                    10 -> Pair(stone / 100_000L, stone % 100_000L)
                    12 -> Pair(stone / 1_000_000L, stone % 1_000_000L)
                    14 -> Pair(stone / 10_000_000L, stone % 10_000_000L)
                    16 -> Pair(stone / 100_000_000L, stone % 100_000_000L)
                    else -> throw RuntimeException("Not supported")
                }
            } else {
                Pair(stone * 2024L, null)
            }
        }
    }
}