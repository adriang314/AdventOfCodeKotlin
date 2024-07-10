package year2019

import common.BaseSolution
import java.util.*

fun main() = println(SolutionDay08().result())

class SolutionDay08 : BaseSolution() {

    override val day = 8
    override val year = 2019

    private val layers = input().toList().map { it.digitToInt() }.let {
        val queue = LinkedList(it)
        val layerSize = 150 // 25 * 6
        val layers = mutableListOf<Layer>()
        while (queue.isNotEmpty()) {
            val pixels = mutableListOf<List<Int>>()

            var row = mutableListOf<Int>()
            for (i in 0 until layerSize) {
                if (i % 25 == 0) {
                    if (row.isNotEmpty())
                        pixels.add(row)
                    row = mutableListOf()
                }
                row.add(queue.poll())
            }

            if (row.isNotEmpty())
                pixels.add(row)

            layers.add(Layer(pixels))
        }

        layers.toList()
    }

    override fun task1(): String {
        val maxZerosLayer = layers.minBy { it.zeros }
        val result = maxZerosLayer.ones * maxZerosLayer.twos
        return result.toString()
    }

    override fun task2(): String {
        (0 until 6).map { i ->
            (0 until 25).map { j ->
                layers.asSequence().map { it.getAt(i, j) }.first { it != 2 }
            }
        }.forEach { row ->
            println(row.map { if (it == 1) '#' else ' ' }.joinToString(""))
        }

        return "BCYEF"
    }

    private class Layer(private val pixels: List<List<Int>>) {
        val zeros = pixels.asSequence().flatten().count { it == 0 }
        val ones = pixels.asSequence().flatten().count { it == 1 }
        val twos = pixels.asSequence().flatten().count { it == 2 }

        fun getAt(rowIdx: Int, colIdx: Int): Int = pixels[rowIdx][colIdx]
    }
}