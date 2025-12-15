package year2015

import common.BaseSolution

fun main() = println(SolutionDay25().result())

class SolutionDay25 : BaseSolution() {

    override val day = 25

    private val codeNumber: Int
    private val firstCodeNumber = 20151125L

    init {
        val regex = """row (\d+), column (\d+)""".toRegex()
        val (row, col) = regex.find(input())!!.destructured
        val rowNumber = row.toInt()
        val colNumber = col.toInt()

        var code = 1
        var increment = 1
        repeat(rowNumber - 1) { code += increment++ }

        increment = 1
        repeat(colNumber - 1) { code += (rowNumber + increment++) }

        codeNumber = code
    }

    override fun task1(): String {
        var currentCodeNumber = firstCodeNumber
        repeat(codeNumber - 1) {
            currentCodeNumber = (currentCodeNumber * 252533L) % 33554393L
        }
        return currentCodeNumber.toString()
    }

    override fun task2(): String {
        return ""
    }
}