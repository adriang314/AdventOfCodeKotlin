package year2016

import common.BaseSolution
import common.List2D

fun main() = println(SolutionDay06().result())

class SolutionDay06 : BaseSolution() {

    override val day = 6

    private val inputLines = input().split("\r\n").map { line -> line.toList() }

    override fun task1(): String {
        return List2D.rotateRight(inputLines).map { chars ->
            chars.groupingBy { it }.eachCount().maxByOrNull { it.value }!!.key
        }.joinToString("")
    }

    override fun task2(): String {
        return List2D.rotateRight(inputLines).map { chars ->
            chars.groupingBy { it }.eachCount().minByOrNull { it.value }!!.key
        }.joinToString("")
    }
}