package year2019

import common.BaseSolution

fun main() = println(SolutionDay05().result())

class SolutionDay05 : BaseSolution() {

    override val day = 5
    override val year = 2019

    private val initProgramData = input().split(",").map { it.toInt() }

    override fun task1(): String {
        val program = Program(initProgramData.toMutableList())
        val result = Runner(program).execute(1)
        return result.toString()
    }

    override fun task2(): String {
        val program = Program(initProgramData.toMutableList())
        val result = Runner(program).execute(5)
        return result.toString()
    }

    private class Runner(private val program: Program) {

        fun execute(input: Int): Int? {
            var currOpCodeIdx = 0
            var currOutput: Int? = null
            do {
                val opCode = program.getAt(currOpCodeIdx, ParamMode.IMMEDIATE)
                val operationInfo = OperationType.from(opCode)
                val result = operationInfo.execute(currOpCodeIdx, program, input)
                result.output?.let { currOutput = it }
                if (operationInfo.isTerminal())
                    break
                currOpCodeIdx = result.nextOperationIndex
            } while (true)

            return currOutput
        }
    }

    private data class Program(private val data: MutableList<Int>) {
        fun getAt(index: Int, mode: ParamMode = ParamMode.POSITION): Int {
            val param = data[index]
            return if (mode == ParamMode.IMMEDIATE) param else data[param]
        }

        fun putAt(index: Int, value: Int) {
            data[index] = value
        }
    }

    private interface Operation {
        val length: Int
        fun execute(
            index: Int,
            program: Program,
            input: Int,
            param1Mode: ParamMode = ParamMode.POSITION,
            param2Mode: ParamMode = ParamMode.POSITION,
            param3Mode: ParamMode = ParamMode.POSITION,
        ): OperationResult
    }

    private class Add : Operation {
        override val length: Int = 4
        override fun execute(
            index: Int,
            program: Program,
            input: Int,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val param3 = program.getAt(index + 3, ParamMode.IMMEDIATE)
            program.putAt(param3, param1 + param2)
            return OperationResult(index + length)
        }

        companion object {
            val instance = Add()
        }
    }

    private class Multiply : Operation {
        override val length: Int = 4
        override fun execute(
            index: Int,
            program: Program,
            input: Int,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val param3 = program.getAt(index + 3, ParamMode.IMMEDIATE)
            program.putAt(param3, param1 * param2)
            return OperationResult(index + length)
        }

        companion object {
            val instance = Multiply()
        }
    }

    private class WriteInput : Operation {
        override val length: Int = 2
        override fun execute(
            index: Int,
            program: Program,
            input: Int,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, ParamMode.IMMEDIATE)
            program.putAt(param1, input)
            return OperationResult(index + length)
        }

        companion object {
            val instance = WriteInput()
        }
    }

    private class ReadOutput : Operation {
        override val length: Int = 2
        override fun execute(
            index: Int,
            program: Program,
            input: Int,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            return OperationResult(index + length, param1)
        }

        companion object {
            val instance = ReadOutput()
        }
    }

    private class Terminate : Operation {
        override val length: Int = 1
        override fun execute(
            index: Int,
            program: Program,
            input: Int,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            return OperationResult(index)
        }

        companion object {
            val instance = Terminate()
        }
    }

    private class JumpIfTrue : Operation {
        override val length: Int = 3
        override fun execute(
            index: Int,
            program: Program,
            input: Int,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val nextOperationIndex = if (param1 != 0) param2 else index + length
            return OperationResult(nextOperationIndex)
        }

        companion object {
            val instance = JumpIfTrue()
        }
    }

    private class JumpIfFalse : Operation {
        override val length: Int = 3
        override fun execute(
            index: Int,
            program: Program,
            input: Int,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val nextOperationIndex = if (param1 == 0) param2 else index + length
            return OperationResult(nextOperationIndex)
        }

        companion object {
            val instance = JumpIfFalse()
        }
    }

    private class LessThan : Operation {
        override val length: Int = 4
        override fun execute(
            index: Int,
            program: Program,
            input: Int,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val param3 = program.getAt(index + 3, ParamMode.IMMEDIATE)
            program.putAt(param3, if (param1 < param2) 1 else 0)
            return OperationResult(index + length)
        }

        companion object {
            val instance = LessThan()
        }
    }

    private class Equals : Operation {
        override val length: Int = 4
        override fun execute(
            index: Int,
            program: Program,
            input: Int,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val param3 = program.getAt(index + 3, ParamMode.IMMEDIATE)
            program.putAt(param3, if (param1 == param2) 1 else 0)
            return OperationResult(index + length)
        }

        companion object {
            val instance = Equals()
        }
    }

    private enum class ParamMode {
        POSITION, IMMEDIATE;

        companion object {
            fun from(value: Int): ParamMode {
                return when (value) {
                    0 -> POSITION
                    1 -> IMMEDIATE
                    else -> throw RuntimeException("Unknown mode")
                }
            }
        }
    }

    private enum class OperationType(val value: Int) {
        END(99), ADD(1), MULTIPLY(2), WRITE_INPUT(3), READ_OUTPUT(4),
        JUMP_IF_TRUE(5), JUMP_IF_FALSE(6), LESS_THAN(7), EQUALS(8);

        companion object {
            fun from(value: Int): OperationInfo {
                val opCode = value % 100
                val type = OperationType.values().first { it.value == opCode }
                val mode1 = ParamMode.from((value / 100) % 10)
                val mode2 = ParamMode.from((value / 1000) % 10)
                val mode3 = ParamMode.from((value / 10000) % 10)
                return OperationInfo(type, mode1, mode2, mode3)
            }
        }
    }

    private data class OperationResult(
        val nextOperationIndex: Int,
        val output: Int? = null,
    )

    private data class OperationInfo(
        private val type: OperationType,
        val param1Mode: ParamMode,
        val param2Mode: ParamMode,
        val param3Mode: ParamMode,
    ) {
        private val operation = when (type) {
            OperationType.ADD -> Add.instance
            OperationType.MULTIPLY -> Multiply.instance
            OperationType.END -> Terminate.instance
            OperationType.WRITE_INPUT -> WriteInput.instance
            OperationType.READ_OUTPUT -> ReadOutput.instance
            OperationType.JUMP_IF_TRUE -> JumpIfTrue.instance
            OperationType.JUMP_IF_FALSE -> JumpIfFalse.instance
            OperationType.LESS_THAN -> LessThan.instance
            OperationType.EQUALS -> Equals.instance
        }

        fun isTerminal() = operation is Terminate

        fun execute(index: Int, program: Program, input: Int): OperationResult =
            operation.execute(index, program, input, param1Mode, param2Mode, param3Mode)
    }
}