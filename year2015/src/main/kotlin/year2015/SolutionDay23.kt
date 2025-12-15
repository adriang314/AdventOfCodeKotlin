package year2015

import common.BaseSolution

fun main() = println(SolutionDay23().result())

class SolutionDay23 : BaseSolution() {

    override val day = 23

    private val commands = input().split("\r\n")

    override fun task1(): String {
        val program = Program(commands, 0L)
        program.execute()
        return program.registerB.value.toString()
    }

    override fun task2(): String {
        val program = Program(commands, 1L)
        program.execute()
        return program.registerB.value.toString()
    }

    private class Program(private val commands: List<String>, registerAValue: Long) {
        val registerA = Register("a", registerAValue)
        val registerB = Register("b", 0L)
        val half = Half(registerA, registerB)
        val triple = Triple(registerA, registerB)
        val increment = Increment(registerA, registerB)
        val jump = Jump(registerA, registerB)
        val jumpIfEven = JumpIfEven(registerA, registerB)
        val jumpIfOne = JumpIfOne(registerA, registerB)

        var commandIdx = 0

        fun execute() {
            while (commandIdx in (0 until commands.size)) {
                val command = commands[commandIdx]
                val operation = getOperation(command)
                val input = command.substringAfter(" ")
                commandIdx += operation.execute(input)
            }
        }

        private fun getOperation(command: String): Operation = when {
            command.startsWith(half.name) -> half
            command.startsWith(triple.name) -> triple
            command.startsWith(increment.name) -> increment
            command.startsWith(jump.name) -> jump
            command.startsWith(jumpIfEven.name) -> jumpIfEven
            command.startsWith(jumpIfOne.name) -> jumpIfOne
            else -> throw IllegalStateException("Unknown command")
        }
    }

    private data class Register(val name: String, var value: Long)

    private interface Operation {
        val name: String
        val registerA: Register
        val registerB: Register

        fun execute(input: String): Int

        fun register(name: String): Register = when (name) {
            registerA.name -> registerA
            registerB.name -> registerB
            else -> throw IllegalStateException("Cannot find register")
        }
    }

    private class Half(override val registerA: Register, override val registerB: Register) : Operation {
        override val name = "hlf"
        override fun execute(input: String): Int {
            register(input).value /= 2
            return 1
        }
    }

    private class Triple(override val registerA: Register, override val registerB: Register) : Operation {
        override val name = "tpl"
        override fun execute(input: String): Int {
            register(input).value *= 3
            return 1
        }
    }

    private class Increment(override val registerA: Register, override val registerB: Register) : Operation {
        override val name = "inc"
        override fun execute(input: String): Int {
            register(input).value += 1
            return 1
        }
    }

    private class Jump(override val registerA: Register, override val registerB: Register) : Operation {
        override val name = "jmp"
        override fun execute(input: String): Int {
            val offset = input.replace("+", "").toInt()
            return offset
        }
    }

    private class JumpIfEven(override val registerA: Register, override val registerB: Register) : Operation {
        override val name = "jie"
        override fun execute(input: String): Int {
            val registerName = input.substringBefore(",")
            val offset = input.substringAfter(", ").replace("+", "").toInt()
            return if (register(registerName).value % 2L == 0L) offset else 1
        }
    }

    private class JumpIfOne(override val registerA: Register, override val registerB: Register) : Operation {
        override val name = "jio"
        override fun execute(input: String): Int {
            val registerName = input.substringBefore(",")
            val offset = input.substringAfter(", ").replace("+", "").toInt()
            return if (register(registerName).value == 1L) offset else 1
        }
    }
}