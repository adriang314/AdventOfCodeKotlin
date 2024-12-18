package year2022

import common.BaseSolution

fun main() = println(SolutionDay11().result())

class SolutionDay11 : BaseSolution() {
    override val day = 11
    
    override fun task1(): String {
        val monkeys = monkeys()
        repeat(20) { executeRound(monkeys, true) }
        val result = monkeys.map { it.inspectedItems }.sortedDescending().take(2).reduce { acc, l -> acc * l }
        return result.toString()
    }

    override fun task2(): String {
        val monkeys = monkeys()
        repeat(10000) { executeRound(monkeys, false) }
        val result = monkeys.map { it.inspectedItems }.sortedDescending().take(2).reduce { acc, l -> acc * l }
        return result.toString()
    }

    private fun monkeys(): List<Monkey> {
        val monkeys = input().split("\r\n\r\n")
        val monkeyIdRegex = Regex("Monkey (\\d+):$", RegexOption.MULTILINE)
        val startingItemsRegex = Regex("Starting items: (.*)$", RegexOption.MULTILINE)
        val operationRegex = Regex("Operation: new = (\\S+) (\\S) (\\S+)$", RegexOption.MULTILINE)
        val divisibleByRegex = Regex("Test: divisible by (\\d+)$", RegexOption.MULTILINE)
        val ifTrueRegex = Regex("If true: throw to monkey (\\d+)$", RegexOption.MULTILINE)
        val ifFalseRegex = Regex("If false: throw to monkey (\\d+)$", RegexOption.MULTILINE)

        return monkeys.map {
            val monkeyIdMatch = monkeyIdRegex.find(it)!!
            val monkeyId = monkeyIdMatch.groupValues[1].toInt()
            val startingItemsMatch = startingItemsRegex.find(it)!!
            val startingItems = ArrayDeque(startingItemsMatch.groupValues[1].split(", ").map { v -> v.toLong() })
            val operationMatch = operationRegex.find(it)!!
            val operationLeft = OperationArg(operationMatch.groupValues[1])
            val operation = Operation.from(operationMatch.groupValues[2])
            val operationRight = OperationArg(operationMatch.groupValues[3])
            val divisibleByMatch = divisibleByRegex.find(it)!!
            val divisibleBy = divisibleByMatch.groupValues[1].toInt()
            val ifTrueMatch = ifTrueRegex.find(it)!!
            val ifTrue = ifTrueMatch.groupValues[1].toInt()
            val ifFalseMatch = ifFalseRegex.find(it)!!
            val ifFalse = ifFalseMatch.groupValues[1].toInt()

            Monkey(monkeyId, startingItems, operation, operationLeft, operationRight, divisibleBy, ifTrue, ifFalse)
        }
    }

    private fun executeRound(monkeys: List<Monkey>, divByThree: Boolean) {
        val divisibleByAll = monkeys.map { it.divisibleBy }.reduce { acc, i -> acc * i }
        monkeys.forEach {
            while (it.items.isNotEmpty()) {
                val item = it.items.removeFirst()
                it.inspectedItems++
                var worryLevel = executeOperation(it.operation, it.operationLeftArg, it.operationRightArg, item)
                if (divByThree)
                    worryLevel /= 3L
                val throwToMonkeyId = if (worryLevel % it.divisibleBy == 0L)
                    it.testTrue else it.testFalse
                val throwToMonkey = monkeys.first { monkey -> monkey.id == throwToMonkeyId }
                throwToMonkey.items.add(worryLevel % divisibleByAll)
            }
        }
    }

    private fun executeOperation(operation: Operation, leftArg: OperationArg, rightArg: OperationArg, item: Long) =
        when (operation) {
            Operation.Add -> Math.addExact(leftArg.valueFor(item), rightArg.valueFor(item))
            Operation.Multiply -> Math.multiplyExact(leftArg.valueFor(item), rightArg.valueFor(item))
        }

    data class Monkey(
        val id: Int,
        val items: ArrayDeque<Long>,
        val operation: Operation,
        val operationLeftArg: OperationArg,
        val operationRightArg: OperationArg,
        val divisibleBy: Int,
        var testTrue: Int,
        var testFalse: Int,
        var inspectedItems: Long = 0L,
    )

    data class OperationArg(val arg: String) {
        fun valueFor(item: Long) = if (arg == "old") item else arg.toLong()
    }

    enum class Operation {
        Add, Multiply;

        companion object {
            fun from(c: String) = when (c) {
                "*" -> Multiply
                "+" -> Add
                else -> throw Exception()
            }
        }
    }
}