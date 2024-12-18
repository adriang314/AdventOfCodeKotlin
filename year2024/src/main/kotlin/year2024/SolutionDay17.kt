package year2024

import common.BaseSolution
import common.pow
import java.util.*

fun main() = println(SolutionDay17().result())

class SolutionDay17 : BaseSolution() {

    override val day = 17
    
    private val program: Program

    init {
        val parts = input().split("\r\n")
        val registry = Registry().also {
            it.a = parts[0].split(":").last().trim().toLong()
            it.b = parts[1].split(":").last().trim().toLong()
            it.c = parts[2].split(":").last().trim().toLong()
        }
        program = Program(
            registry,
            parts[4].split(":").last().split(",").map { it.trim().toLong() }
        )
    }

    override fun task1(): String {
        val runner = Runner(program)
        runner.execute()
        val out = program.outputs.joinToString(",") { it.toString() }
        return out
    }

    override fun task2(): String {
        var aValues = listOf(0L)

        // composing A registry value from backward, using last 3 bits
        val expectedOutputs = LinkedList(program.dataInput.reversed())
        while (expectedOutputs.isNotEmpty()) {
            val expectedOutput = expectedOutputs.pop()

            aValues = aValues.map { aValue ->
                val aCurrentValue = aValue * 2.pow(3)
                (0..7)
                    .mapNotNull { aNextPart ->
                        val b1 = aNextPart % 8
                        val b2 = b1.xor(4)
                        val c = (aCurrentValue + aNextPart) / 2.pow(b2)
                        val b3 = b2.toLong().xor(c)
                        val b4 = b3.xor(4)
                        val b5 = b4 % 8
                        if (b5 == expectedOutput) aNextPart else null
                    }
                    .mapNotNull {
                        if (aCurrentValue != 0L || it != 0) aCurrentValue + it else null
                    }
            }.flatten()
        }

        val result = aValues.min()
        return result.toString()
    }

    private class Registry {
        var a: Long = 0L
        var b: Long = 0L
        var c: Long = 0L
    }

    private class Runner(val program: Program) {
        fun execute() {
            do {
                val operationInfo = program.getNextOperation() ?: break
                operationInfo.execute(program)
            } while (true)
        }
    }

    private data class Program(val registry: Registry, val dataInput: List<Long>) {
        var operationIndex = 0
        val outputs = mutableListOf<Long>()

        fun getNextOperation(): OperationInfo? {
            if (operationIndex >= dataInput.size)
                return null
            return OperationType.from(getAt(operationIndex, ParamMode.LITERAL))
        }

        fun getAt(index: Int, mode: ParamMode): Long {
            require(index >= 0) { "Index cannot be negative" }

            val param = dataInput[index]
            return when (mode) {
                ParamMode.LITERAL -> param
                ParamMode.COMBO -> {
                    when (param) {
                        in 0L..3L -> param
                        4L -> registry.a
                        5L -> registry.b
                        6L -> registry.c
                        else -> throw RuntimeException("Unknown param")
                    }
                }
            }
        }
    }

    private interface Operation {
        val length: Int
        fun execute(program: Program)
    }

    private enum class ParamMode {
        LITERAL, COMBO;
    }

    private enum class OperationType(val value: Long) {
        ADV(0), BXL(1), BST(2), JNZ(3), BXC(4),
        OUT(5), BDV(6), CDV(7);

        companion object {
            fun from(value: Long): OperationInfo {
                val type = OperationType.values().first { it.value == value }
                return OperationInfo(type)
            }
        }
    }

    private class Adv : Operation {
        override val length: Int = 2
        override fun execute(program: Program) {
            val param = program.getAt(program.operationIndex + 1, ParamMode.COMBO)
            val result = program.registry.a / 2.pow(param.toInt())
            program.registry.a = result

            program.operationIndex += length
        }

        companion object {
            val instance = Adv()
        }
    }

    private class Bdv : Operation {
        override val length: Int = 2
        override fun execute(program: Program) {
            val param = program.getAt(program.operationIndex + 1, ParamMode.COMBO)
            val result = program.registry.a / 2.pow(param.toInt())
            program.registry.b = result

            program.operationIndex += length
        }

        companion object {
            val instance = Bdv()
        }
    }

    private class Cdv : Operation {
        override val length: Int = 2
        override fun execute(program: Program) {
            val param = program.getAt(program.operationIndex + 1, ParamMode.COMBO)
            val result = program.registry.a / 2.pow(param.toInt())
            program.registry.c = result

            program.operationIndex += length
        }

        companion object {
            val instance = Cdv()
        }
    }

    private class Bxl : Operation {
        override val length: Int = 2
        override fun execute(program: Program) {
            val param1 = program.getAt(program.operationIndex + 1, ParamMode.LITERAL)
            val param2 = program.registry.b
            val result = param1.xor(param2)
            program.registry.b = result

            program.operationIndex += length
        }

        companion object {
            val instance = Bxl()
        }
    }

    private class Bst : Operation {
        override val length: Int = 2
        override fun execute(program: Program) {
            val param1 = program.getAt(program.operationIndex + 1, ParamMode.COMBO)
            val result = param1 % 8L
            program.registry.b = result

            program.operationIndex += length
        }

        companion object {
            val instance = Bst()
        }
    }

    private class Bxc : Operation {
        override val length: Int = 2
        override fun execute(program: Program) {
            val param1 = program.registry.b
            val param2 = program.registry.c
            val result = param1.xor(param2)
            program.registry.b = result

            program.operationIndex += length
        }

        companion object {
            val instance = Bxc()
        }
    }

    private class Out : Operation {
        override val length: Int = 2
        override fun execute(program: Program) {
            val param1 = program.getAt(program.operationIndex + 1, ParamMode.COMBO)
            val result = param1 % 8L

            program.outputs.add(result)
            program.operationIndex += length
        }

        companion object {
            val instance = Out()
        }
    }

    private class Jnz : Operation {
        override val length: Int = 2
        override fun execute(program: Program) {
            if (program.registry.a != 0L) {
                val param1 = program.getAt(program.operationIndex + 1, ParamMode.LITERAL)
                program.operationIndex = param1.toInt()

            } else {
                program.operationIndex += length
            }
        }

        companion object {
            val instance = Jnz()
        }
    }

    private data class OperationInfo(private val type: OperationType) {
        override fun toString() = type.name

        private val operation = when (type) {
            OperationType.ADV -> Adv.instance
            OperationType.BXL -> Bxl.instance
            OperationType.BST -> Bst.instance
            OperationType.JNZ -> Jnz.instance
            OperationType.BXC -> Bxc.instance
            OperationType.OUT -> Out.instance
            OperationType.BDV -> Bdv.instance
            OperationType.CDV -> Cdv.instance
        }

        fun execute(program: Program) = operation.execute(program)
    }
}