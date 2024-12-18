package year2021

import common.BaseSolution

fun main() = println(SolutionDay14().result())

class SolutionDay14 : BaseSolution() {

    override val day = 14
    
    override fun task1(): String {
        var patternMap = initPatternMap()
        repeat(10) { patternMap = executeRules(patternMap) }
        return computeResult(patternMap).toString()
    }

    override fun task2(): String {
        var patternMap = initPatternMap()
        repeat(40) { patternMap = executeRules(patternMap) }
        return computeResult(patternMap).toString()
    }

    private val initPattern: String
    private val rules: Map<String, InsertionRule>

    init {
        val inputSplit = input().split("\r\n\r\n")
        initPattern = inputSplit[0]
        rules = inputSplit[1].split("\r\n").map { line ->
            val lineSplit = line.split(" -> ")
            InsertionRule(lineSplit.first(), lineSplit.last().first())
        }.associateBy { rule -> rule.text }
    }

    private fun initPatternMap(): MutableMap<String, Pattern> {
        val map = mutableMapOf<String, Pattern>()
        for (idx in 0..initPattern.length - 2) {
            addPattern(map, initPattern.substring(idx, idx + 2), 1)
        }
        return map
    }

    private fun computeResult(patternMap: MutableMap<String, Pattern>): Long {
        val letterCount = mutableMapOf<Char, Long>(initPattern.last() to 1)
        patternMap.values.forEach { pattern ->
            letterCount.compute(pattern.name.first()) { _, curr ->
                if (curr == null) pattern.count else curr + pattern.count
            }
        }

        val maxLetterCount = letterCount.values.max()
        val minLetterCount = letterCount.values.min()
        return maxLetterCount - minLetterCount
    }

    private fun addPattern(map: MutableMap<String, Pattern>, name: String, count: Long) {
        val childPatterns = rules[name]?.patterns() ?: emptyList()
        map.compute(name) { _, curr -> curr?.increaseCount(count) ?: Pattern(name, childPatterns, count) }
    }

    private fun executeRules(patternMap: MutableMap<String, Pattern>): MutableMap<String, Pattern> {
        val newPatterns = patternMap.values.map { pattern -> Pair(pattern.childPatterns, pattern.count) }
        val newPatternsMap = mutableMapOf<String, Pattern>()
        newPatterns.forEach { pair ->
            pair.first.forEach {
                addPattern(newPatternsMap, it, pair.second)
            }
        }
        return newPatternsMap
    }

    data class Pattern(val name: String, val childPatterns: List<String>, var count: Long = 0) {
        fun increaseCount(count: Long): Pattern {
            this.count += count
            return this
        }
    }

    data class InsertionRule(val text: String, val insert: Char) {
        fun patterns() = listOf(text[0].toString() + insert, insert + text[1].toString())
    }
}