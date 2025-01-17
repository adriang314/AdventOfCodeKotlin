package year2017

import common.BaseSolution

fun main() = println(SolutionDay10().result())

class SolutionDay10 : BaseSolution() {

    override val day = 10

    private val lengths1 = input().split(",").map { it.toInt() }
    private val lengths2 = input().map { it.code } + listOf(17, 31, 73, 47, 23)

    override fun task1(): String {
        val sparseHash = computeSparseHash(lengths1, 1)
        return (sparseHash[0] * sparseHash[1]).toString()
    }

    override fun task2(): String {
        val sparseHash = computeSparseHash(lengths2, 64)
        val denseHash = computeDenseHash(sparseHash)
        return denseHash.joinToString("") { "%02x".format(it) }
    }

    private fun computeSparseHash(lengths: List<Int>, rounds: Int): List<Int> {
        val listSize = 256
        val list = (0..<listSize).toMutableList()
        var currentPosition = 0
        var skipSize = 0

        repeat(rounds) {
            for (length in lengths) {
                // Reverse the section of the list
                val sublist = (0..<length).map { list[(currentPosition + it) % listSize] }.reversed()
                for (i in 0..<length) {
                    list[(currentPosition + i) % listSize] = sublist[i]
                }

                // Move the current position forward and increase the skip size
                currentPosition = (currentPosition + length + skipSize++) % listSize
            }
        }
        return list
    }

    private fun computeDenseHash(sparseHash: List<Int>): List<Int> {
        return sparseHash.chunked(16).map { block -> block.reduce { acc, num -> acc xor num } }
    }
}