package year2019

import common.BaseSolution
import kotlinx.coroutines.*
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

fun main() = println(SolutionDay07().result())

class SolutionDay07 : BaseSolution() {

    override val day = 7
    override val year = 2019

    private val initProgramData = input().split(",").map { it.toLong() }

    override fun task1(): String {
        val phaseSettings = allPermutations(setOf(0L, 1L, 2L, 3L, 4L))
        val maxOutput = execute(phaseSettings)
        return maxOutput.toString()
    }

    override fun task2(): String {
        val phaseSettings = allPermutations(setOf(5L, 6L, 7L, 8L, 9L))
        val maxOutput = execute(phaseSettings)
        return maxOutput.toString()
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private fun execute(phaseSettings: Set<List<Long>>): Long {
        var lastOutput = 0L
        var maxOutput = 0L

        phaseSettings.forEach {
            val inputA = LinkedBlockingDeque(listOf(it[0], 0L))
            val inputB = LinkedBlockingDeque(listOf(it[1]))
            val inputC = LinkedBlockingDeque(listOf(it[2]))
            val inputD = LinkedBlockingDeque(listOf(it[3]))
            val inputE = LinkedBlockingDeque(listOf(it[4]))

            runBlocking {
                val runnerA = Runner(Program(initProgramData.toMutableList(), inputA) { output -> inputB.add(output) })
                val runnerB = Runner(Program(initProgramData.toMutableList(), inputB) { output -> inputC.add(output) })
                val runnerC = Runner(Program(initProgramData.toMutableList(), inputC) { output -> inputD.add(output) })
                val runnerD = Runner(Program(initProgramData.toMutableList(), inputD) { output -> inputE.add(output) })
                val runnerE = Runner(Program(initProgramData.toMutableList(), inputE) { output -> inputA.add(output) })

                launch(newSingleThreadContext("A")) { runnerA.execute() }
                launch(newSingleThreadContext("B")) { runnerB.execute() }
                launch(newSingleThreadContext("C")) { runnerC.execute() }
                launch(newSingleThreadContext("D")) { runnerD.execute() }
                launch(newSingleThreadContext("E")) {
                    val resultE = runnerE.execute()
                    lastOutput = resultE!!
                }.join()

                if (lastOutput > maxOutput)
                    maxOutput = lastOutput
            }
        }
        return maxOutput
    }

    private fun <T> allPermutations(set: Set<T>): Set<List<T>> {
        if (set.isEmpty())
            return emptySet()

        fun <T> allPermutationsInternal(list: List<T>): Set<List<T>> {
            if (list.isEmpty())
                return setOf(emptyList())
            val result: MutableSet<List<T>> = mutableSetOf()
            for (i in list.indices) {
                allPermutationsInternal(list - list[i]).forEach { item -> result.add(item + list[i]) }
            }
            return result
        }
        return allPermutationsInternal(set.toList())
    }

    private class Runner(val program: Program) {
        fun execute(): Long? {
            var currOpCodeIdx = 0
            var currOutput: Long? = null
            do {
                val opCode = program.getAt(currOpCodeIdx, ParamMode.IMMEDIATE)
                val operationInfo = OperationType.from(opCode)
                val result = operationInfo.execute(currOpCodeIdx, program)
                result.output?.let { currOutput = it }
                if (operationInfo.isTerminal())
                    break
                currOpCodeIdx = result.nextOperationIndex
            } while (true)

            return currOutput
        }
    }

    private data class Program(
        private val data: MutableList<Long>,
        private val input: BlockingDeque<Long>,
        private val outputHandler: (Long) -> Unit,
    ) {
        fun getInput(): Long = input.take()

        fun produceOutput(value: Long) = outputHandler(value)

        fun getAt(index: Int, mode: ParamMode = ParamMode.POSITION): Long {
            val param = data[index]
            return if (mode == ParamMode.IMMEDIATE) param else data[param.toInt()]
        }

        fun putAt(index: Int, value: Long) {
            data[index] = value
        }
    }

    private interface Operation {
        val length: Int
        fun execute(
            index: Int,
            program: Program,
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
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val param3 = program.getAt(index + 3, ParamMode.IMMEDIATE)
            program.putAt(param3.toInt(), param1 + param2)
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
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val param3 = program.getAt(index + 3, ParamMode.IMMEDIATE)
            program.putAt(param3.toInt(), param1 * param2)
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
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, ParamMode.IMMEDIATE)
            program.putAt(param1.toInt(), program.getInput())
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
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            program.produceOutput(param1)
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
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val nextOperationIndex: Int = if (param1 != 0L) param2.toInt() else (index + length)
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
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val nextOperationIndex = if (param1 == 0L) param2.toInt() else index + length
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
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val param3 = program.getAt(index + 3, ParamMode.IMMEDIATE)
            program.putAt(param3.toInt(), if (param1 < param2) 1 else 0)
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
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ): OperationResult {
            val param1 = program.getAt(index + 1, param1Mode)
            val param2 = program.getAt(index + 2, param2Mode)
            val param3 = program.getAt(index + 3, ParamMode.IMMEDIATE)
            program.putAt(param3.toInt(), if (param1 == param2) 1 else 0)
            return OperationResult(index + length)
        }

        companion object {
            val instance = Equals()
        }
    }

    private enum class ParamMode {
        POSITION, IMMEDIATE;

        companion object {
            fun from(value: Long): ParamMode {
                return when (value) {
                    0L -> POSITION
                    1L -> IMMEDIATE
                    else -> throw RuntimeException("Unknown mode")
                }
            }
        }
    }

    private enum class OperationType(val value: Int) {
        END(99), ADD(1), MULTIPLY(2), WRITE_INPUT(3), READ_OUTPUT(4),
        JUMP_IF_TRUE(5), JUMP_IF_FALSE(6), LESS_THAN(7), EQUALS(8);

        companion object {
            fun from(value: Long): OperationInfo {
                val opCode = value % 100
                val type = OperationType.values().first { it.value.toLong() == opCode }
                val mode1 = ParamMode.from((value / 100) % 10)
                val mode2 = ParamMode.from((value / 1000) % 10)
                val mode3 = ParamMode.from((value / 10000) % 10)
                return OperationInfo(type, mode1, mode2, mode3)
            }
        }
    }

    private data class OperationResult(
        val nextOperationIndex: Int,
        val output: Long? = null,
    )

    private data class OperationInfo(
        private val type: OperationType,
        val param1Mode: ParamMode,
        val param2Mode: ParamMode,
        val param3Mode: ParamMode,
    ) {
        override fun toString() =
            "${type.name} ${param1Mode.name} ${param2Mode.name} ${param3Mode.name}"

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

        fun execute(index: Int, program: Program): OperationResult =
            operation.execute(index, program, param1Mode, param2Mode, param3Mode)
    }
}