package year2015

import common.BaseSolution

fun main() = println(SolutionDay07().result())

class SolutionDay07 : BaseSolution() {

    override val day = 7

    private val instructions = input().split("\r\n").map { Instruction.from(it) }

    override fun task1(): String {
        val result = InstructionExecutor.start(instructions.toMutableList(), mutableMapOf())
        return result.toString()
    }

    override fun task2(): String {
        val result = InstructionExecutor.start(instructions.toMutableList(), mutableMapOf(Reference("b") to InstructionExecutor.start(instructions.toMutableList(), mutableMapOf())))
        return result.toString()
    }

    private object InstructionExecutor {
        fun start(instructions: MutableList<Instruction>, referenceValues: MutableMap<Reference, Int>): Int {
            while (instructions.isNotEmpty()) {
                instructions.removeAll(instructions.mapNotNull { it.tryExecute(referenceValues).let { result -> if (result) it else null } })
            }
            return referenceValues[Reference("a")] ?: throw IllegalStateException("Unable to get reference 'a'")
        }
    }

    private interface Instruction {
        val type: Type

        fun tryExecute(referenceValues: MutableMap<Reference, Int>): Boolean

        enum class Type {
            And, Or, Not, ShiftRight, ShiftLeft, Assignment
        }

        companion object {
            fun from(input: String): Instruction {
                val regex1 = """^(\w+) (AND|OR|RSHIFT|LSHIFT) (\w+) -> (\w+)$""".toRegex()
                val regex2 = """^NOT (\w+) -> (\w+)$""".toRegex()
                val regex3 = """^(\w+) -> (\w+)$""".toRegex()

                return regex1.matchEntire(input)?.let {
                    val (arg1, op, arg2, res) = it.destructured
                    when (op) {
                        "AND" -> AndInstruction(Variable.from(arg1), Variable.from(arg2), Reference(res))
                        "OR" -> OrInstruction(Variable.from(arg1), Variable.from(arg2), Reference(res))
                        "LSHIFT" -> ShiftLeftInstruction(Variable.from(arg1), Variable.from(arg2), Reference(res))
                        "RSHIFT" -> ShiftRightInstruction(Variable.from(arg1), Variable.from(arg2), Reference(res))
                        else -> null
                    }
                } ?: regex2.matchEntire(input)?.let {
                    val (arg1, res) = it.destructured
                    NotInstruction(Variable.from(arg1), Reference(res))
                } ?: regex3.matchEntire(input)?.let {
                    val (arg1, res) = it.destructured
                    return AssignmentInstruction(Variable.from(arg1), Reference(res))
                } ?: throw IllegalStateException("Unknown instruction: $input")
            }
        }
    }

    private data class Reference(val name: String)

    private data class Variable(val reference: Reference? = null, val value: Int? = null) {
        fun resolve(referenceValues: Map<Reference, Int>): Int? {
            return value ?: referenceValues[reference]
        }

        companion object {
            fun from(input: String): Variable = input.toIntOrNull()?.let { Variable(null, it) } ?: Variable(Reference(input))
        }
    }

    private data class OrInstruction(val arg1: Variable, val arg2: Variable, val result: Reference) : Instruction {
        override val type = Instruction.Type.Or

        override fun tryExecute(referenceValues: MutableMap<Reference, Int>): Boolean {
            return arg1.resolve(referenceValues)?.let { arg1Value ->
                arg2.resolve(referenceValues)?.let { arg2Value ->
                    referenceValues.putIfAbsent(result, arg1Value.or(arg2Value))
                    true
                }
            } ?: false
        }
    }

    private data class AndInstruction(val arg1: Variable, val arg2: Variable, val result: Reference) : Instruction {
        override val type = Instruction.Type.And

        override fun tryExecute(referenceValues: MutableMap<Reference, Int>): Boolean {
            return arg1.resolve(referenceValues)?.let { arg1Value ->
                arg2.resolve(referenceValues)?.let { arg2Value ->
                    referenceValues.putIfAbsent(result, arg1Value.and(arg2Value))
                    true
                }
            } ?: false
        }
    }

    private data class ShiftLeftInstruction(val arg1: Variable, val arg2: Variable, val result: Reference) : Instruction {
        override val type = Instruction.Type.ShiftLeft

        override fun tryExecute(referenceValues: MutableMap<Reference, Int>): Boolean {
            return arg1.resolve(referenceValues)?.let { arg1Value ->
                arg2.resolve(referenceValues)?.let { arg2Value ->
                    referenceValues.putIfAbsent(result, arg1Value.shl(arg2Value))
                    true
                }
            } ?: false
        }
    }

    private data class ShiftRightInstruction(val arg1: Variable, val arg2: Variable, val result: Reference) : Instruction {
        override val type = Instruction.Type.ShiftRight

        override fun tryExecute(referenceValues: MutableMap<Reference, Int>): Boolean {
            return arg1.resolve(referenceValues)?.let { arg1Value ->
                arg2.resolve(referenceValues)?.let { arg2Value ->
                    referenceValues.putIfAbsent(result, arg1Value.shr(arg2Value))
                    true
                }
            } ?: false
        }
    }

    private data class NotInstruction(val arg1: Variable, val result: Reference) : Instruction {
        override val type = Instruction.Type.Not

        override fun tryExecute(referenceValues: MutableMap<Reference, Int>): Boolean {
            return arg1.resolve(referenceValues)?.let { arg1Value ->
                referenceValues.putIfAbsent(result, arg1Value.inv())
                true
            } ?: false
        }
    }

    private data class AssignmentInstruction(val arg1: Variable, val result: Reference) : Instruction {
        override val type = Instruction.Type.Assignment

        override fun tryExecute(referenceValues: MutableMap<Reference, Int>): Boolean {
            return arg1.resolve(referenceValues)?.let { arg1Value ->
                referenceValues.putIfAbsent(result, arg1Value)
                true
            } ?: false
        }
    }
}