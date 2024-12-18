package year2020

import common.BaseSolution

fun main() = println(SolutionDay19().result())

class SolutionDay19 : BaseSolution() {

    override val day = 19
    
    override fun task1(): String {
        val result = messages.filter { ruleZero.matches(it) }
        return result.size.toString()
    }

    override fun task2(): String {
        val result = messages.filter { ruleZeroUpdated.matches(it) }
        return result.size.toString()
    }

    private val messages: List<String>
    private val rules: List<Rule>
    private val rulesUpdated: List<Rule>
    private val ruleZero: Rule
    private val ruleZeroUpdated: Rule

    init {
        val inputParts = input().split("\r\n\r\n")
        rules = inputParts[0].split("\r\n").map { line ->
            val parts = line.split(": ")
            Rule(parts[0].toInt(), parts[1].replace("\"", ""))
        }

        rulesUpdated = rules.filter { it.id != 8 && it.id != 11 }.map { it.copy() }
            .plus(Rule(8, "42 | 42 8"))
            .plus(Rule(11, "42 31 | 42 11 31"))

        ruleZero = rules.first { it.id == 0 }
        ruleZeroUpdated = rulesUpdated.first { it.id == 0 }

        messages = inputParts[1].split("\r\n")

        buildReferences(rules)
        buildReferences(rulesUpdated)
    }

    private fun buildReferences(rules: List<Rule>) {
        rules.forEach { rule ->
            rule.innerRules = rule.formula.split(" | ").map { innerRules ->
                innerRules.split(" ").mapNotNull { innerRule -> rules.firstOrNull { it.id == innerRule.toIntOrNull() } }
            }.filter { it.isNotEmpty() }
        }
    }

    data class Rule(val id: Int, val formula: String) {

        var innerRules: List<List<Rule>> = emptyList()

        fun matches(message: String): Boolean {
            val result = matches(message, 0)
            return result.contains(message.length)
        }

        private fun matches(message: String, index: Int): List<Int> {
            if (innerRules.isEmpty()) {
                return if (message.length > index && message.substring(index).startsWith(formula))
                    listOf(index + formula.length)
                else
                    emptyList()
            }

            return innerRules.map { matches(it, message, index) }.flatten()
        }

        private companion object {
            fun matches(rules: List<Rule>, message: String, index: Int): List<Int> {
                if (rules.isEmpty())
                    return emptyList()
                var currIndices = listOf(index)
                for (rule in rules) {
                    currIndices = currIndices.map { rule.matches(message, it) }.flatten().distinct()
                }
                return currIndices
            }
        }
    }
}