package year2024

import common.BaseSolution

fun main() = println(SolutionDay19().result())

class SolutionDay19 : BaseSolution() {

    override val day = 19

    private val patterns: Set<TowelPattern>
    private val towels: List<Towel>
    private val checker: TowelDesignMatcher

    init {
        val lines = input().split("\r\n")

        patterns = lines[0].split(", ").map { setOf(TowelPattern(it)) }.flatten().toSet()
        towels = lines.drop(2).map { Towel(it) }
        checker = TowelDesignMatcher(patterns)
    }

    override fun task1(): String {
        val matchingTowels = towels.count { checker.matches(it) > 0 }
        return matchingTowels.toString()
    }

    override fun task2(): String {
        val matchingTowels = towels.sumOf { checker.matches(it) }
        return matchingTowels.toString()
    }

    private class TowelDesignMatcher(private val patterns: Set<TowelPattern>) {
        private val cache = mutableMapOf<String, Long>()

        fun matches(towel: Towel): Long {
            if (towel.value.isEmpty())
                return 1L

            if (cache.containsKey(towel.value))
                return cache[towel.value]!!

            val matchingPatterns = patterns.filter { pattern -> towel.value.startsWith(pattern.value) }
            if (matchingPatterns.isEmpty()) {
                cache[towel.value] = 0
                return 0
            }

            val result = matchingPatterns.sumOf { matchingPattern -> matches(towel.usePattern(matchingPattern)) }
            cache[towel.value] = result
            return result
        }
    }

    private data class TowelPattern(val value: String)

    private data class Towel(val value: String) {
        fun usePattern(pattern: TowelPattern) = Towel(value.replaceFirst(pattern.value, ""))
    }
}