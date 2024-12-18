package year2019

import common.BaseSolution
import kotlin.math.abs

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16
    
    private val inputSignal = input().toList().map { it.digitToInt() }
    private val inputSignal10000 = input().let { input ->
        val builder = StringBuilder(input.length * 10_000)
        repeat(10_000) {
            builder.append(input)
        }
        builder.toString()
    }.toList().map { it.digitToInt() }

    private val inputSignal10000Offset = inputSignal.take(7).joinToString("").toInt()

    override fun task1(): String {
        var input = inputSignal
        repeat(100) {
            input = executePhase(input)
        }
        val result = input.take(8).joinToString("") { it.toString() }
        return result
    }

    override fun task2(): String {
        var input = inputSignal10000
        repeat(100) {
            println("Phase $it")
            input = executePhase2(input, inputSignal10000Offset)
        }

        val result = input.drop(inputSignal10000Offset).take(8).joinToString("") { it.toString() }
        return result
    }

    private fun executePhase2(input: List<Int>, offset: Int): List<Int> {
        var lastSum = 0
        return (input.size - 1 downTo 0).map { position ->
            // no need to know digits before offset
            if (position < offset) {
                0
            } else {
                lastSum = (lastSum + input[position]) % 10
                lastSum
            }
        }.reversed()
    }

    private fun executePhase(input: List<Int>): List<Int> {
        return List(input.size) { position ->
            var sum = 0L
            for (index in input.indices) {
                val repeatValue = BasePattern.instance.getForPosition(position, index)
                when (repeatValue) {
                    0 -> Unit
                    -1 -> sum -= input[index]
                    1 -> sum += input[index]
                    else -> throw RuntimeException("x")
                }
            }

            (abs(sum) % 10).toInt()
        }
    }

    private class BasePattern {
        private val baseValue = listOf(0, 1, 0, -1)

        fun getForPosition(position: Int, index: Int): Int = if (position == 0) {
            baseValue[(index + 1) % baseValue.size]
        } else {
            baseValue[((index + 1) / (position + 1)) % baseValue.size]
        }

        companion object {
            val instance = BasePattern()
        }
    }
}