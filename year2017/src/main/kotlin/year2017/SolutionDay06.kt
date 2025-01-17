package year2017

import common.BaseSolution

fun main() = println(SolutionDay06().result())

class SolutionDay06 : BaseSolution() {

    override val day = 6

    private val initMemory = Memory(input().split("\\s+".toRegex()).map { it.toInt() })
    private val cache = mutableMapOf(initMemory to 0)
    private var currentStep = 1
    private var currentMemory = initMemory

    init {
        while (true) {
            currentMemory = currentMemory.redistribute()
            if (cache.contains(currentMemory))
                break
            cache[currentMemory] = currentStep++
        }
    }

    override fun task1(): String {
        return currentStep.toString()
    }

    override fun task2(): String {
        return (currentStep - cache[currentMemory]!!).toString()
    }

    private data class Memory(val banks: List<Int>) {
        fun redistribute(): Memory {
            val toRedistributeIdx = banks.indexOfFirst { it == banks.max() }
            val redistribution = banks.toMutableList()
            redistribution[toRedistributeIdx] = 0
            for (i in 1..banks[toRedistributeIdx]) {
                redistribution[(toRedistributeIdx + i) % banks.size] += 1
            }

            return Memory(redistribution)
        }
    }
}