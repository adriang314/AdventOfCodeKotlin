package year2016

import common.BaseSolution

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {

    override val day = 12

    override fun task1(): String {
        val computer = Computer(common.Register(0, 0, 0, 0))
        computer.executeInstructions()
        return computer.registerAValue().toString()
    }

    override fun task2(): String {
        val computer = Computer(common.Register(0, 0, 1, 0))
        computer.executeInstructions()
        return computer.registerAValue().toString()
    }

    private inner class Computer(private val register: common.Register) {
        private val instructions = input().split("\r\n").map { line ->
            when {
                line.startsWith("cpy") -> CopyInstruction.from(line)
                line.startsWith("inc") -> IncrementInstruction.from(line)
                line.startsWith("dec") -> DecrementInstruction.from(line)
                line.startsWith("jnz") -> JumpInstruction.from(line)
                else -> throw IllegalArgumentException("Invalid instruction: $line")
            }
        }

        fun registerAValue(): Long = register.read(0)

        fun executeInstructions() {
            var instructionIndex = 0
            while (instructionIndex in instructions.indices) {
                when (val instruction = instructions[instructionIndex]) {
                    is CopyInstruction -> {
                        val value = instruction.value ?: register.read(instruction.fromRegisterId!!)
                        register.store(value.toLong(), instruction.toRegisterId)
                        instructionIndex++
                    }

                    is IncrementInstruction -> {
                        val value = register.read(instruction.registerId)
                        register.store(value + 1, instruction.registerId)
                        instructionIndex++
                    }

                    is DecrementInstruction -> {
                        val value = register.read(instruction.registerId)
                        register.store(value - 1, instruction.registerId)
                        instructionIndex++
                    }

                    is JumpInstruction -> {
                        val conditionValue = instruction.condition ?: register.read(instruction.conditionFromRegisterId!!)
                        if (conditionValue != 0L) {
                            val offsetValue = instruction.offset ?: register.read(instruction.offsetFromRegisterId!!)
                            instructionIndex += offsetValue.toInt()
                        } else {
                            instructionIndex++
                        }
                    }
                }
            }
        }
    }

    private interface Instruction {
        companion object {
            val registerIdMapping: Map<Char, Long> get() = mapOf('a' to 0L, 'b' to 1L, 'c' to 2L, 'd' to 3L)
        }
    }

    private data class CopyInstruction(val value: Int?, val fromRegisterId: Long?, val toRegisterId: Long) : Instruction {
        companion object {
            fun from(text: String): CopyInstruction {
                val regex = Regex("""cpy (-?\d+|[a-d]) ([a-d])""")
                val result = regex.find(text) ?: throw IllegalArgumentException("Invalid instruction: $text")
                val valuePart = result.groupValues[1]
                val value = valuePart.toIntOrNull()
                val fromRegisterId = if (value == null) Instruction.registerIdMapping[valuePart[0]]!! else null
                val toRegisterId = Instruction.registerIdMapping[result.groupValues[2][0]]!!
                return CopyInstruction(value, fromRegisterId, toRegisterId)
            }
        }
    }

    private data class IncrementInstruction(val registerId: Long) : Instruction {
        companion object {
            fun from(text: String): IncrementInstruction {
                val regex = Regex("""inc ([a-d])""")
                val result = regex.find(text) ?: throw IllegalArgumentException("Invalid instruction: $text")
                return IncrementInstruction(Instruction.registerIdMapping[result.groupValues[1][0]]!!)
            }
        }
    }

    private data class DecrementInstruction(val registerId: Long) : Instruction {
        companion object {
            fun from(text: String): DecrementInstruction {
                val regex = Regex("""dec ([a-d])""")
                val result = regex.find(text) ?: throw IllegalArgumentException("Invalid instruction: $text")
                return DecrementInstruction(Instruction.registerIdMapping[result.groupValues[1][0]]!!)
            }
        }
    }

    private data class JumpInstruction(val condition: Int?, val conditionFromRegisterId: Long?, val offset: Int?, val offsetFromRegisterId: Long?) : Instruction {
        companion object {
            fun from(text: String): JumpInstruction {
                val regex = Regex("""jnz (-?\d+|[a-d]) (-?\d+|[a-d])""")
                val result = regex.find(text) ?: throw IllegalArgumentException("Invalid instruction: $text")
                val conditionPart = result.groupValues[1]
                val condition = conditionPart.toIntOrNull()
                val conditionFromRegisterId = if (condition == null) Instruction.registerIdMapping[conditionPart[0]]!! else null
                val offsetPart = result.groupValues[2]
                val offset = offsetPart.toIntOrNull()
                val offsetFromRegisterId = if (offset == null) Instruction.registerIdMapping[offsetPart[0]]!! else null
                return JumpInstruction(condition, conditionFromRegisterId, offset, offsetFromRegisterId)
            }
        }
    }
}