package year2021

import common.BaseSolution
import kotlin.math.abs

fun main() = println(SolutionDay07().result())

class SolutionDay07 : BaseSolution() {

    override val day = 7
    override val year = 2021

    override fun task1(): String {
        val gatherPositionsWithFuel = (minPosition..maxPosition).associateWith { gatherPosition ->
            positions.sumOf { position -> abs(gatherPosition - position) }
        }

        return gatherPositionsWithFuel.minOf { it.value }.toString()
    }

    override fun task2(): String {
        val gatherPositionsWithFuel = (minPosition..maxPosition).associateWith { gatherPosition ->
            positions.sumOf { position ->
                val diff = abs(gatherPosition - position)
                diff * (diff + 1L) / 2L
            }
        }

        return gatherPositionsWithFuel.minOf { it.value }.toString()
    }

    private val positions = input().split(",").map { it.toInt() }
    private val minPosition = positions.min()
    private val maxPosition = positions.max()
}