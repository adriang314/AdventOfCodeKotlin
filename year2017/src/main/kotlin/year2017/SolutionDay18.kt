package year2017

import common.BaseSolution
import common.Register
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {

    override val day = 18

    private val instructions = input().split("\r\n")

    override fun task1(): String {
        val tablet1 = Tablet()
        tablet1.execute(instructions)
        return tablet1.firstRecovered().toString()
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    override fun task2(): String {
        val tablet1 = ExtendedTablet(0L)
        val tablet2 = ExtendedTablet(1L)
        tablet1.onOutput = tablet2.input::add
        tablet2.onOutput = tablet1.input::add

        runBlocking {
            launch(newSingleThreadContext("Tablet1")) {
                tablet1.execute(instructions)
            }

            launch(newSingleThreadContext("Tablet2")) {
                tablet2.execute(instructions)
            }
        }

        return tablet2.output.size.toString()
    }

    private class ExtendedTablet(id: Long) : Tablet() {
        val output = LinkedList<Long>()
        val input = ArrayBlockingQueue<Long>(10_000, true)
        private var terminate = false

        init {
            registers.store(id, 'p')
        }

        lateinit var onOutput: (Long) -> Unit

        override val executors: List<InstructionExecutor> = listOf(
            SendInstructionExecutor(),
            ReceiveInstructionExecutor(),
            SetInstructionExecutor(),
            AddInstructionExecutor(),
            MulInstructionExecutor(),
            ModInstructionExecutor(),
            JumpInstructionExecutor()
        )

        override fun execute(instructions: List<String>) {
            while (instructionIdx in instructions.indices && !terminate) {
                val instruction = instructions[instructionIdx.toInt()]
                executors.forEach { it.execute(instruction) }
            }
        }

        private inner class SendInstructionExecutor() : InstructionExecutor {
            override fun execute(instruction: String) {
                """snd (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x) = it.destructured
                    val outValue = getValue(x)
                    output.add(outValue)
                    onOutput(outValue)
                    instructionIdx++
                }
            }
        }

        private inner class ReceiveInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """rcv (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x) = it.destructured
                    val inValue = input.poll(1, TimeUnit.SECONDS)
                    if (inValue == null) {
                        terminate = true
                    } else {
                        registers.store(inValue, x[0])
                        instructionIdx++
                    }
                }
            }
        }
    }

    private open class Tablet() {
        protected val registers = Register('z'.code - 'a'.code + 1)
        protected var instructionIdx = 0L
        protected val sounds = LinkedList<Long>()
        protected val recovers = LinkedList<Long>()

        protected open val executors: List<InstructionExecutor> = listOf(
            SoundInstructionExecutor(),
            RecoverInstructionExecutor(),
            SetInstructionExecutor(),
            AddInstructionExecutor(),
            MulInstructionExecutor(),
            ModInstructionExecutor(),
            JumpInstructionExecutor()
        )

        open fun execute(instructions: List<String>) {
            while (instructionIdx in instructions.indices && recovers.isEmpty()) {
                val instruction = instructions[instructionIdx.toInt()]
                executors.forEach { it.execute(instruction) }
            }
        }

        fun firstRecovered() = recovers.first()

        protected fun getValue(x: String): Long = x.toLongOrNull() ?: registers.read(x[0])

        protected interface InstructionExecutor {
            fun execute(instruction: String)
        }

        protected inner class SoundInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """snd (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x) = it.destructured
                    sounds.add(getValue(x))
                    instructionIdx++
                }
            }
        }

        protected inner class SetInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """set (-?\w+) (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x, y) = it.destructured
                    registers.store(getValue(y), x[0])
                    instructionIdx++
                }
            }
        }

        protected inner class AddInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """add (-?\w+) (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x, y) = it.destructured
                    registers.store(getValue(x) + getValue(y), x[0])
                    instructionIdx++
                }
            }
        }

        protected inner class MulInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """mul (-?\w+) (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x, y) = it.destructured
                    registers.store(getValue(x) * getValue(y), x[0])
                    instructionIdx++
                }
            }
        }

        protected inner class ModInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """mod (-?\w+) (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x, y) = it.destructured
                    registers.store(getValue(x) % getValue(y), x[0])
                    instructionIdx++
                }
            }
        }

        protected inner class RecoverInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """rcv (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x) = it.destructured
                    if (getValue(x) > 0) {
                        recovers.add(sounds.last())
                    }
                    instructionIdx++
                }
            }
        }

        protected inner class JumpInstructionExecutor : InstructionExecutor {
            override fun execute(instruction: String) {
                """jgz (-?\w+) (-?\w+)""".toRegex().find(instruction)?.let {
                    val (x, y) = it.destructured
                    instructionIdx += if (getValue(x) > 0L) getValue(y) else 1L
                }
            }
        }
    }
}