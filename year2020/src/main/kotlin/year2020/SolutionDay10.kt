package year2020

import common.BaseSolution

fun main() = println(SolutionDay10().result())

class SolutionDay10 : BaseSolution() {

    override val day = 10
    
    override fun task1(): String {
        val diff = sortedAdapters.scan(Diffs()) { acc: Diffs, i: Int ->
            when (i - acc.prev) {
                1 -> acc.one++
                2 -> acc.two++
                3 -> acc.three++
            }
            acc.prev = i
            acc
        }.last()

        return (diff.one * diff.three).toString()
    }

    override fun task2(): String {
        val paths = calc(0, 0)
        return paths.toString()
    }

    private val sortedAdapters: List<Int>
    private val cache = mutableMapOf<Pair<Int, Int>, Long>()

    init {
        val adapters = input().split("\r\n").map { it.toInt() }.sortedBy { it }.toMutableList()
        adapters.add(0, 0)
        adapters.add(adapters.last() + 3)
        sortedAdapters = adapters
    }

    private fun calc(current: Int, index: Int): Long {
        val key = Pair(current, index)
        if (cache.containsKey(key))
            return cache[key]!!

        val next1 = sortedAdapters.getOrNull(index + 1)
        val next2 = sortedAdapters.getOrNull(index + 2)
        val next3 = sortedAdapters.getOrNull(index + 3)

        if (next3 != null && next3 - current <= 3) {
            val result = calc(next3, index + 3) + calc(next2!!, index + 2) + calc(next1!!, index + 1)
            cache[key] = result
            return result
        } else if (next2 != null && next2 - current <= 3) {
            val result = calc(next2, index + 2) + calc(next1!!, index + 1)
            cache[key] = result
            return result
        } else if (next1 != null) {
            val result = calc(next1, index + 1)
            cache[key] = result
            return result
        } else {
            return 1
        }
    }

    data class Diffs(var one: Int = 0, var two: Int = 0, var three: Int = 0, var prev: Int = 0)
}