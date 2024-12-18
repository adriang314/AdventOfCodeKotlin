package year2024

import common.BaseSolution

fun main() = println(SolutionDay03().result())

class SolutionDay03 : BaseSolution() {

    override val day = 3
    
    private val multiplyRegex = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)")
    private val doRegex = Regex("do\\(\\)")
    private val doNotRegex = Regex("don't\\(\\)")

    private val multiplies: Map<Int, Multiply>
    private val doList: List<Int>
    private val doNotList: List<Int>

    init {
        input().let { input ->
            multiplies = multiplyRegex.findAll(input).toList().associate {
                it.range.first to Multiply(
                    it.destructured.component1().toInt(),
                    it.destructured.component2().toInt()
                )
            }
            doList = doRegex.findAll(input).toList().map { it.range.first }
            doNotList = doNotRegex.findAll(input).toList().map { it.range.first }
        }
    }

    override fun task1(): String {
        return multiplies.values.sumOf { it.result }.toString()
    }

    override fun task2(): String {
        var sum = 0L
        var enabled = true

        for (i in 0..multiplies.keys.sorted().max()) {
            when {
                doNotList.contains(i) -> enabled = false
                doList.contains(i) -> enabled = true
                enabled && multiplies.containsKey(i) -> sum += multiplies[i]!!.result
            }
        }

        return sum.toString()
    }

    private data class Multiply(val x: Int, val y: Int) {
        val result = x * y
    }
}