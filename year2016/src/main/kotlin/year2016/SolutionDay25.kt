package year2016

import common.BaseSolution

fun main() = println(SolutionDay25().result())

class SolutionDay25 : BaseSolution() {

    override val day = 25

    override fun task1(): String {
        (0L..500L).forEach { aRegisterValue ->
            val computer = Computer(common.Register(aRegisterValue, 0, 0, 0))
            if (computer.executeInstructions(100))
                return aRegisterValue.toString()
        }

        throw IllegalStateException("Unable to find correct A register value")
    }

    override fun task2(): String {
        return ""
    }

    private inner class Computer(private val register: common.Register) {
        private val output = mutableListOf<Long>()

        private val instructions = input().split("\r\n").map { line ->
            when {
                line.startsWith("cpy") -> CopyInstruction.from(line)
                line.startsWith("inc") -> IncrementInstruction.from(line)
                line.startsWith("dec") -> DecrementInstruction.from(line)
                line.startsWith("jnz") -> JumpInstruction.from(line)
                line.startsWith("out") -> OutInstruction.from(line)
                else -> throw IllegalArgumentException("Invalid instruction: $line")
            }
        }

        fun executeInstructions(maxOutputSize: Int): Boolean {
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

                    is OutInstruction -> {
                        val value = register.read(instruction.registerId)
                        // checking if output matches required pattern
                        val prevValue = output.lastOrNull() ?: 1L
                        if (prevValue + value == 1L) {
                            output.add(value)
                        } else {
                            return false
                        }

                        if (output.size == maxOutputSize) {
                            return true
                        } else {
                            instructionIndex++
                        }
                    }
                }
            }

            return false
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

    private data class JumpInstruction(val condition: Long?, val conditionFromRegisterId: Long?, val offset: Int?, val offsetFromRegisterId: Long?) : Instruction {
        companion object {
            fun from(text: String): JumpInstruction {
                val regex = Regex("""jnz (-?\d+|[a-d]) (-?\d+|[a-d])""")
                val result = regex.find(text) ?: throw IllegalArgumentException("Invalid instruction: $text")
                val conditionPart = result.groupValues[1]
                val condition = conditionPart.toLongOrNull()
                val conditionFromRegisterId = if (condition == null) Instruction.registerIdMapping[conditionPart[0]]!! else null
                val offsetPart = result.groupValues[2]
                val offset = offsetPart.toIntOrNull()
                val offsetFromRegisterId = if (offset == null) Instruction.registerIdMapping[offsetPart[0]]!! else null
                return JumpInstruction(condition, conditionFromRegisterId, offset, offsetFromRegisterId)
            }
        }
    }

    private data class OutInstruction(val registerId: Long) : Instruction {
        companion object {
            fun from(text: String): OutInstruction {
                val regex = Regex("""out ([a-d])""")
                val result = regex.find(text) ?: throw IllegalArgumentException("Invalid instruction: $text")
                return OutInstruction(Instruction.registerIdMapping[result.groupValues[1][0]]!!)
            }
        }
    }
}