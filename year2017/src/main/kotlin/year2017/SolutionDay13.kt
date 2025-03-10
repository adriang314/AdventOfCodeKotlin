package year2017

import common.BaseSolution

fun main() = println(SolutionDay13().result())

class SolutionDay13 : BaseSolution() {

    override val day = 13

    private val layers = input().lines().map {
        val (depth, range) = it.split(": ").map(String::toInt)
        Layer(depth, range)
    }

    override fun task1(): String {
        val result = layers.filter { it.isScannerOnTop() }.sumOf { it.severity }
        return result.toString()
    }

    override fun task2(): String {
        var delay = 0
        while (true) {
            if (layers.all { layer -> !layer.isScannerOnTop(delay) }) {
                break
            } else {
                delay++
            }
        }

        return delay.toString()
    }

    private class Layer(val depth: Int, val range: Int) {
        val severity = depth * range

        fun isScannerOnTop(delay: Int = 0): Boolean {
            return (depth + delay) % ((range - 1) * 2) == 0
        }
    }
}