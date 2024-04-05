package year2019

import common.BaseSolution

fun main() = println(SolutionDay02().result())

class SolutionDay02 : BaseSolution() {

    override val day = 2
    override val year = 2019

    private val initNumbers = input().split(",").map { it.toLong() }

    override fun task1(): String {
        val numbers = initNumbers.toMutableList()
        numbers[1] = 12
        numbers[2] = 2
        val result = calc(numbers)
        return result.toString()
    }

    override fun task2(): String {
        val expected = 19690720L
        var result = 0L
        (0L..99L).map { noun ->
            (0L..99L).map { verb ->
                val numbers = initNumbers.toMutableList()
                numbers[1] = noun
                numbers[2] = verb
                kotlin.runCatching {
                    if (calc(numbers) == expected)
                        result = 100 * noun + verb
                }
            }
        }

        return result.toString()
    }

    private fun calc(numbers: MutableList<Long>): Long {
        var opCodeIdx = 0
        do {
            val pos0 = numbers[opCodeIdx]
            if (pos0 == 99L)
                break
            val pos1 = numbers[opCodeIdx + 1].toInt()
            val pos2 = numbers[opCodeIdx + 2].toInt()
            val pos3 = numbers[opCodeIdx + 3].toInt()
            when (pos0) {
                1L -> numbers[pos3] = numbers[pos1] + numbers[pos2]
                2L -> numbers[pos3] = numbers[pos1] * numbers[pos2]
                else -> throw RuntimeException("Unsupported")
            }
            opCodeIdx += 4
        } while (true)

        return numbers[0]
    }
}