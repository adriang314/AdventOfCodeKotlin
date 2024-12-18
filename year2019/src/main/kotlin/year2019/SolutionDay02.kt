package year2019

import common.BaseSolution

fun main() = println(SolutionDay02().result())

class SolutionDay02 : BaseSolution() {

    override val day = 2
    
    private val initInput = input().split(",").map { it.toLong() }

    override fun task1(): String {
        val input = initInput.toMutableList()
        input[1] = 12
        input[2] = 2
        val result = IntCodeProgram(input).execute()
        return result.toString()
    }

    override fun task2(): String {
        val expected = 19690720L
        (0L..99L).map { noun ->
            (0L..99L).map { verb ->
                val input = initInput.toMutableList()
                input[1] = noun
                input[2] = verb
                runCatching {
                    if (IntCodeProgram(input).execute() == expected) {
                        val result = 100 * noun + verb
                        return result.toString()
                    }
                }
            }
        }

        return "not found"
    }

    private class IntCodeProgram(private val input: MutableList<Long>) {

        fun execute(): Long {
            var currOpCodeIdx = 0
            do {
                val operation = when (OpCode.from(input[currOpCodeIdx])) {
                    OpCode.CODE_1 -> Operation1.instance
                    OpCode.CODE_2 -> Operation2.instance
                    OpCode.CODE_99 -> Operation99.instance
                }

                operation.execute(currOpCodeIdx, input)
                if (operation.terminal)
                    break

                currOpCodeIdx += operation.size
            } while (true)

            return input[0]
        }
    }

    private interface Operation {
        val size: Int
        val terminal: Boolean
        fun execute(index: Int, input: MutableList<Long>)
    }

    private class Operation1 : Operation {
        override val size: Int = 4
        override val terminal: Boolean = false
        override fun execute(index: Int, input: MutableList<Long>) {
            val param1 = input[index + 1].toInt()
            val param2 = input[index + 2].toInt()
            val param3 = input[index + 3].toInt()
            input[param3] = input[param1] + input[param2]
        }

        companion object {
            val instance = Operation1()
        }
    }

    private class Operation2 : Operation {
        override val size: Int = 4
        override val terminal: Boolean = false
        override fun execute(index: Int, input: MutableList<Long>) {
            val param1 = input[index + 1].toInt()
            val param2 = input[index + 2].toInt()
            val param3 = input[index + 3].toInt()
            input[param3] = input[param1] * input[param2]
        }

        companion object {
            val instance = Operation2()
        }
    }

    private class Operation99 : Operation {
        override val size: Int = 1
        override val terminal: Boolean = true
        override fun execute(index: Int, input: MutableList<Long>) {
            // nothing
        }

        companion object {
            val instance = Operation99()
        }
    }

    private enum class OpCode(val value: Long) {
        CODE_99(99L), CODE_1(1L), CODE_2(2L);

        companion object {
            fun from(value: Long) = OpCode.values().first { it.value == value }
        }
    }
}