package year2017

import common.BaseSolution
import common.Register
import common.isPrime

fun main() = println(SolutionDay23().result())

class SolutionDay23 : BaseSolution() {

    override val day = 23

    private val instructions = input().split("\r\n")

    override fun task1(): String {
        val tablet = Tablet()
        tablet.execute(instructions)
        return tablet.mulInvoked.toString()
    }

    override fun task2(): String {
        val result = (106700L..123700L step 17).count { !it.isPrime() }
        return result.toString()
    }

//    private fun realProgram(): Long {
//        var b = 106700L
//        val c = 123700L
//        var d: Long
//        var e: Long
//        var f: Long
//        var h = 0L
//
//        do {
//            f = 1L
//            d = 2L
//            do {
//                e = 2L
//                do {
//                    if (d * e == b) // f is zero if b is not prime number
//                        f = 0L
//                    e++
//                } while (e != b)
//                d++
//            } while (d != b)
//
//            if (f == 0L) // h is increased if f == 0
//                h++
//            if (b == c) // checking all values form b to c using 17 as step
//                return h
//            b += 17
//        } while (true)
//    }

    private class Tablet() {
        val registers = Register('h'.code - 'a'.code + 1)
        private var instructionIdx = 0L
        var mulInvoked = 0L

        private val executors: List<InstructionExecutor> = listOf(
            SetInstructionExecutor(),
            SubInstructionExecutor(),
            MulInstructionExecutor(),
            JumpInstructionExecutor()
        )

        fun execute(instructions: List<String>) {
            while (instructionIdx in instructions.indices) {
                val instruction = instructions[instructionIdx.toInt()]
                executors.forEach { it.execute(instruction) }
            }
        }

        private fun getValue(x: String): Long = x.toLongOrNull() ?: registers.read(x[0])

        private interface InstructionExecutor {
            fun execute(instruction: String)
        }

        private inner class SetInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """set (-?\w+) (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x, y) = it.destructured
                    registers.store(getValue(y), x[0])
                    instructionIdx++
                }
            }
        }

        private inner class SubInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """sub (-?\w+) (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x, y) = it.destructured
                    registers.store(getValue(x) - getValue(y), x[0])
                    instructionIdx++
                }
            }
        }

        private inner class MulInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """mul (-?\w+) (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x, y) = it.destructured
                    registers.store(getValue(x) * getValue(y), x[0])
                    instructionIdx++
                    mulInvoked++
                }
            }
        }

        private inner class JumpInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """jnz (-?\w+) (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x, y) = it.destructured
                    instructionIdx += if (getValue(x) != 0L) getValue(y) else 1L
                }
            }
        }
    }
}