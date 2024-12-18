package year2021

import common.BaseSolution

fun main() = println(SolutionDay03().result())

class SolutionDay03 : BaseSolution() {

    override val day = 3
    
    override fun task1(): String {
        val result = epsilon.toLong(2) * gamma.toLong(2)
        return result.toString()
    }

    override fun task2(): String {
        val result = oxygenGenerator.toLong(2) * co2.toLong(2)
        return result.toString()
    }

    private var co2: String
    private var oxygenGenerator: String
    private var epsilon: String
    private var gamma: String

    init {
        val lines = input().split("\r\n")
        val length = lines.first().length

        gamma = (0 until length).map { index ->
            val zeros = lines.map { it[index] }.count { it == '0' }
            val ones = lines.map { it[index] }.count { it == '1' }
            if (ones == zeros)
                throw Exception()
            if (ones > zeros) '1' else '0'
        }.joinToString("")

        epsilon = (0 until length).map { index ->
            val zeros = lines.map { it[index] }.count { it == '0' }
            val ones = lines.map { it[index] }.count { it == '1' }
            if (ones == zeros)
                throw Exception()
            if (ones < zeros) '1' else '0'
        }.joinToString("")

        oxygenGenerator = oxygenGeneratorFilter(lines, 0)
        co2 = co2Filter(lines, 0)
    }

    private fun oxygenGeneratorFilter(lines: List<String>, index: Int): String {
        if (lines.size == 1)
            return lines.first()

        val zeros = lines.map { Pair(it, it[index]) }.filter { it.second == '0' }
        val ones = lines.map { Pair(it, it[index]) }.filter { it.second == '1' }

        return if (ones.size >= zeros.size)
            oxygenGeneratorFilter(ones.map { it.first }, index + 1)
        else
            oxygenGeneratorFilter(zeros.map { it.first }, index + 1)
    }

    private fun co2Filter(lines: List<String>, index: Int): String {
        if (lines.size == 1)
            return lines.first()

        val zeros = lines.map { Pair(it, it[index]) }.filter { it.second == '0' }
        val ones = lines.map { Pair(it, it[index]) }.filter { it.second == '1' }

        return if (ones.size >= zeros.size)
            co2Filter(zeros.map { it.first }, index + 1)
        else
            co2Filter(ones.map { it.first }, index + 1)
    }
}