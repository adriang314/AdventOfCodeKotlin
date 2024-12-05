package year2024

import common.BaseSolution

fun main() = println(SolutionDay05().result())

class SolutionDay05 : BaseSolution() {

    override val day = 5
    override val year = 2024

    private val rules: List<Rule>
    private val updates: List<Update>

    init {
        val input = input().split("\r\n")
        val tmpRules = mutableListOf<Rule>()
        val tmpUpdates = mutableListOf<Update>()

        var isRule = true
        input.forEach { line ->
            if (line.isEmpty())
                isRule = false
            else if (isRule) {
                val parts = line.split("|")
                tmpRules.add(Rule(parts[0].toInt(), parts[1].toInt()))
            } else {
                val parts = line.split(",").map { it.toInt() }
                tmpUpdates.add(Update(parts))
            }
        }

        rules = tmpRules.toList()
        updates = tmpUpdates.toList()
    }

    override fun task1(): String {
        val result = updates.filter { update -> update.isFollowingRules(rules) }.sumOf { it.middle() }
        return result.toString()
    }

    override fun task2(): String {
        val result = updates.filter { update -> !update.isFollowingRules(rules) }
            .map { it.order(rules) }.sumOf { it.middle() }
        return result.toString()
    }

    private data class Rule(val next: Int, val prev: Int)

    private class Update(val list: List<Int>) {
        // key == page, value == index
        private val map: Map<Int, Int> = list.mapIndexed { index, s -> s to index }.toMap()

        fun middle() = list[list.count() / 2]

        fun order(rules: List<Rule>): Update {
            rules.forEach { rule ->
                val prev = map[rule.prev]
                val next = map[rule.next]
                if (prev != null && next != null && prev < next) {
                    val newList = list.toMutableList()
                    newList[prev] = rule.next
                    newList[next] = rule.prev
                    val newUpdate = Update(newList)
                    return newUpdate.order(rules)
                }
            }
            return this
        }

        fun isFollowingRules(rules: List<Rule>): Boolean {
            rules.forEach { rule ->
                val prev = map[rule.prev]
                val next = map[rule.next]
                if (prev != null && next != null && prev < next)
                    return false
            }
            return true
        }
    }
}