package year2017

import common.BaseSolution
import kotlin.math.max

fun main() = println(SolutionDay08().result())

class SolutionDay08 : BaseSolution() {

    override val day = 8

    private val executor: Executor

    init {
        val regex = Regex("^(\\w+) (\\w+) (-?\\d+) if (\\w+) (\\S+) (-?\\d+)$")
        val operations = input().lines().map { line ->
            val (reg1, opType, val1, reg2, op, val2) = regex.find(line)!!.destructured
            Operation(reg1, OperationType.from(opType), val1.toInt(), Condition(reg2, Operator.from(op), val2.toInt()))
        }
        executor = Executor(operations)
        executor.start()
    }

    override fun task1(): String {
        return executor.maxValueInRegistry().toString()
    }

    override fun task2(): String {
        return executor.highestValueInRegister.toString()
    }

    private class Executor(val operations: List<Operation>) {
        private val registry = mutableMapOf<String, Int>()
        var highestValueInRegister: Int? = null

        fun start() = operations.forEach { op ->
            op.execute(registry)?.let { highestValueInRegister = max(it, highestValueInRegister ?: 0) }
        }

        fun maxValueInRegistry() = registry.values.max()
    }

    private data class Operation(val reg: String, val type: OperationType, val value: Int, val cond: Condition) {
        fun execute(registry: MutableMap<String, Int>): Int? {
            if (!cond.isSuccessful(registry)) {
                return null
            }

            when (type) {
                OperationType.Increase -> registry[reg] = registry.getOrDefault(reg, 0) + value
                OperationType.Decrease -> registry[reg] = registry.getOrDefault(reg, 0) - value
            }
            return registry[reg]
        }
    }

    private data class Condition(val register: String, val operator: Operator, val value: Int) {
        fun isSuccessful(registry: Map<String, Int>): Boolean {
            val registerValue = registry.getOrDefault(register, 0)
            return when (operator) {
                Operator.Less -> registerValue < value
                Operator.LessEqual -> registerValue <= value
                Operator.Equal -> registerValue == value
                Operator.NotEqual -> registerValue != value
                Operator.Greater -> registerValue > value
                Operator.GreaterEqual -> registerValue >= value
            }
        }
    }

    private enum class Operator(val value: String) {
        Less("<"), LessEqual("<="), Equal("=="), NotEqual("!="), Greater(">"), GreaterEqual(">=");

        companion object {
            fun from(value: String) = entries.single { it.value == value }
        }
    }

    private enum class OperationType(val value: String) {
        Increase("inc"), Decrease("dec");

        companion object {
            fun from(value: String) = entries.single { it.value == value }
        }
    }
}