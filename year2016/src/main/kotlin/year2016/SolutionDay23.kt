package year2016

import common.BaseSolution
import year2016.SolutionDay23.Instruction.Companion.isValidRegisterId

fun main() = println(SolutionDay23().result())

class SolutionDay23 : BaseSolution() {

    override val day = 23

    override fun task1(): String {
        val computer = Computer(common.Register(7, 0, 0, 0))
        computer.executeInstructions()
        return computer.registerAValue().toString()
    }

    override fun task2(): String {
        val computer = Computer(common.Register(12, 0, 0, 0))
        computer.executeInstructions()
        return computer.registerAValue().toString()
    }

    private inner class Computer(private val register: common.Register) {
        private val initialInstructions = input().split("\r\n").map { line ->
            when {
                line.startsWith("cpy") -> CopyInstruction.from(line)
                line.startsWith("inc") -> IncrementInstruction.from(line)
                line.startsWith("dec") -> DecrementInstruction.from(line)
                line.startsWith("jnz") -> JumpInstruction.from(line)
                line.startsWith("tgl") -> ToggleInstruction.from(line)
                else -> throw IllegalArgumentException("Invalid instruction: $line")
            }
        }

        fun registerAValue(): Long = register.read(0)

        fun executeInstructions() {
            val instructions = initialInstructions.toMutableList()
            var instructionIndex = 0
            while (instructionIndex in instructions.indices) {
                val instruction = instructions[instructionIndex]
                if (!instruction.isValid()) {
                    instructionIndex++
                    continue
                }

                when (instruction) {
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

                    is ToggleInstruction -> {
                        val value = register.read(instruction.registerId)
                        val targetInstructionIndex = instructionIndex + value.toInt()
                        val targetInstruction = instructions.getOrNull(targetInstructionIndex)
                        if (targetInstruction is IncrementInstruction) {
                            instructions[targetInstructionIndex] = DecrementInstruction(targetInstruction.registerId)
                        } else if (targetInstruction is ToggleInstruction) {
                            instructions[targetInstructionIndex] = IncrementInstruction(targetInstruction.registerId)
                        } else if (targetInstruction is JumpInstruction) {
                            instructions[targetInstructionIndex] = CopyInstruction(targetInstruction.condition, targetInstruction.conditionFromRegisterId, targetInstruction.offsetFromRegisterId ?: -1L)
                        } else if (targetInstruction is CopyInstruction) {
                            instructions[targetInstructionIndex] = JumpInstruction(targetInstruction.value, targetInstruction.fromRegisterId, null, targetInstruction.toRegisterId)
                        }

                        instructionIndex++
                    }
                }
            }
        }
    }

    private interface Instruction {
        fun isValid(): Boolean

        companion object {
            val registerIdMapping: Map<Char, Long> get() = mapOf('a' to 0L, 'b' to 1L, 'c' to 2L, 'd' to 3L)

            fun isValidRegisterId(id: Long?) = id == null || id in (0L..3L)
        }
    }

    private data class CopyInstruction(val value: Int?, val fromRegisterId: Long?, val toRegisterId: Long) : Instruction {
        override fun isValid(): Boolean = isValidRegisterId(fromRegisterId) && isValidRegisterId(toRegisterId)

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
        override fun isValid(): Boolean = isValidRegisterId(registerId)

        companion object {
            fun from(text: String): IncrementInstruction {
                val regex = Regex("""inc ([a-d])""")
                val result = regex.find(text) ?: throw IllegalArgumentException("Invalid instruction: $text")
                return IncrementInstruction(Instruction.registerIdMapping[result.groupValues[1][0]]!!)
            }
        }
    }

    private data class DecrementInstruction(val registerId: Long) : Instruction {
        override fun isValid(): Boolean = isValidRegisterId(registerId)

        companion object {
            fun from(text: String): DecrementInstruction {
                val regex = Regex("""dec ([a-d])""")
                val result = regex.find(text) ?: throw IllegalArgumentException("Invalid instruction: $text")
                return DecrementInstruction(Instruction.registerIdMapping[result.groupValues[1][0]]!!)
            }
        }
    }

    private data class JumpInstruction(val condition: Int?, val conditionFromRegisterId: Long?, val offset: Int?, val offsetFromRegisterId: Long?) : Instruction {

        override fun isValid(): Boolean = isValidRegisterId(conditionFromRegisterId) && isValidRegisterId(offsetFromRegisterId)

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

    private data class ToggleInstruction(val registerId: Long) : Instruction {
        override fun isValid(): Boolean = isValidRegisterId(registerId)

        companion object {
            fun from(text: String): ToggleInstruction {
                val regex = Regex("""tgl ([a-d])""")
                val result = regex.find(text) ?: throw IllegalArgumentException("Invalid instruction: $text")
                return ToggleInstruction(Instruction.registerIdMapping[result.groupValues[1][0]]!!)
            }
        }
    }
}