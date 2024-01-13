package year2021

import common.BaseSolution

fun main() = println(SolutionDay06().result())

class SolutionDay06 : BaseSolution() {

    override val day = 6
    override val year = 2021

    override fun task1(): String {
        return calSizeAfter(80).toString()
    }

    override fun task2(): String {
        return calSizeAfter(256).toString()
    }

    private fun calSizeAfter(days: Int): Long {
        val fishes = input().split(",").map { LanternFish(it.toInt()) }
        repeat(days) {
            fishes.forEach { it.produce() }
        }

        return fishes.sumOf { it.size() }
    }

    data class LanternFish(var timeToProduceNew: Int) {

        private var childFish = emptyMap<Int, Long>()

        fun size() = childFish.values.sumOf { it } + 1

        fun produce() {
            val newChildFishMap = mutableMapOf(
                0 to 0L, 1 to 0L, 2 to 0L, 3 to 0L, 4 to 0L,
                5 to 0L, 6 to 0L, 7 to 0L, 8 to 0L,
            )

            childFish.forEach { entry ->
                val time = entry.key
                val size = entry.value
                if (time == 0) {
                    newChildFishMap[8] = size
                    newChildFishMap[6] = newChildFishMap[6]!! + size
                } else
                    newChildFishMap[time - 1] = newChildFishMap[time - 1]!! + size
            }

            if (timeToProduceNew == 0) {
                newChildFishMap[8] = newChildFishMap[8]!! + 1
                timeToProduceNew = 6
            } else {
                timeToProduceNew--
            }

            childFish = newChildFishMap
        }
    }
}