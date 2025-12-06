package year2025

import common.BaseSolution
import common.combine
import common.length

fun main() = println(SolutionDay05().result())

class SolutionDay05 : BaseSolution() {

    override val day = 5

    private val ingredients = input().split("\r\n").filter { !it.contains("-") }
        .filter { it.isNotBlank() }
        .map { Ingredient(it.toLong()) }

    private val freshness = input().split("\r\n").filter { it.contains("-") }
        .map {
            val parts = it.split("-")
            Freshness(parts[0].toLong()..parts[1].toLong())
        }

    override fun task1(): String {
        return ingredients.count { it.isFresh(freshness) }.toString()
    }

    override fun task2(): String {
        val freshValues = freshness.fold(listOf<LongRange>()) { acc, next -> next.values.combine(acc) }
        val freshCount = freshValues.sumOf { it.length() }
        return freshCount.toString()
    }

    private data class Freshness(val values: LongRange)

    private data class Ingredient(val id: Long) {
        fun isFresh(freshness: List<Freshness>): Boolean = freshness.any { it.values.contains(id) }
    }
}