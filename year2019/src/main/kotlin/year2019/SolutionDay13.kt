package year2019

import common.BaseSolution
import java.util.LinkedList
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

fun main() = println(SolutionDay13().result())

class SolutionDay13 : BaseSolution() {

    override val day = 13
    
    private val initProgramData = input().split(",").map { it.toLong() }

    override fun task1(): String {
        val tiles = play(initProgramData)
        val game = tiles.groupBy { it.y }
        game.keys.sorted().map { game[it]!! }.forEach { tilesInRow ->
            tilesInRow.sortedBy { it.x }.forEach {
                when (it.id) {
                    0L -> print(' ')    // EMPTY
                    1L -> print('.')    // WALL
                    2L -> print('#')    // BLOCK
                    3L -> print('_')    // PADDLE
                    4L -> print('o')    // BALL
                }
            }
            println()
        }

        val result = tiles.filter { it.id == 2L }.size
        return result.toString()
    }

//    ........................................
//    .                                      .
//    . #  ####  #       ##  ## ###   ## ### .
//    . ##    # ### #  # #  ##    ### # #    .
//    .    ##  ## ## ##  # # ##  ##   ##   # .
//    . ### ###   #       #   #   ##     # # .
//    .  #   #  ###  ###    #     ##  #      .
//    . ## #    #### ## #     #   ###      # .
//    . ## # ###### #   ## #   #   # # ### # .
//    .   # #  #       #   #      # #  # #   .
//    . ##      # ## ### # # #         #     .
//    .  ##  ##  ### #          # #      ### .
//    .    ## #  #  # ##     #  # # ##     # .
//    .  #  #   ##   # # ###    ##  ## ##    .
//    .                                      .
//    .                 O                    .
//    .                                      .
//    .                                      .
//    .                   _                  .
//    .                                      .

    override fun task2(): String {
        val input = LinkedBlockingDeque((0..10_000).map { 0L })
        val program = initProgramData.toMutableList().apply {
            this[0] = 2L
            // adding paddles to the bottom, joystick moves not required to end game
            for (i in 1360..1397)
                this[i] = 3L
        }

        val result = play(program, input)
        return result.last().id.toString()
    }

    private fun play(
        program: List<Long>,
        input: LinkedBlockingDeque<Long> = LinkedBlockingDeque()
    ): List<Tile> {
        val outputs = LinkedList<Long>()
        val tiles = LinkedList<Tile>()

        Runner(Program(program, input) {
            outputs.add(it)
            if (outputs.size == 3) {
                tiles.add(Tile(outputs[2], outputs[0], outputs[1]))
                outputs.clear()
            }
        }).execute()

        return tiles
    }

    private data class Tile(val id: Long, val x: Long, val y: Long)

    private class Runner(val program: Program) {
        fun execute(): Long? {
            do {
                val opCode = program.getNextOperation()
                val operationInfo = OperationType.from(opCode)
                operationInfo.execute(program)
                if (operationInfo.isTerminal())
                    break
            } while (true)

            return program.outputs.lastOrNull()
        }
    }

    private data class Program(
        private val dataInput: List<Long>,
        private val input: BlockingDeque<Long>,
        private val outputHandler: (Long) -> Unit,
    ) {
        val data = dataInput.mapIndexed { index, value -> index.toLong() to value }.toMap().toMutableMap()

        var operationIndex = 0L
        val outputs = LinkedList<Long>()
        var relativeBase: Long = 0L

        fun getInput(): Long = input.take()

        fun produceOutput(value: Long) = outputHandler(value)

        fun getNextOperation() = getAt(operationIndex, ParamMode.IMMEDIATE)

        fun getAt(index: Long, mode: ParamMode): Long {
            if (index < 0)
                throw RuntimeException("Negative memory")
            val param = data[index] ?: 0L
            return when (mode) {
                ParamMode.IMMEDIATE -> param
                ParamMode.RELATIVE -> data[param + relativeBase] ?: 0L
                ParamMode.POSITION -> data[param] ?: 0L
            }
        }

        fun putAt(index: Long, value: Long, mode: ParamMode) {
            val param = data[index] ?: 0L
            return when (mode) {
                ParamMode.IMMEDIATE -> throw RuntimeException("not possible")
                ParamMode.RELATIVE -> data[param + relativeBase] = value
                ParamMode.POSITION -> data[param] = value
            }
        }
    }

    private interface Operation {
        val length: Int
        fun execute(
            program: Program,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        )
    }

    private class Add : Operation {
        override val length: Int = 4
        override fun execute(
            program: Program,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ) {
            val param1 = program.getAt(program.operationIndex + 1, param1Mode)
            val param2 = program.getAt(program.operationIndex + 2, param2Mode)
            val result = param1 + param2
            program.putAt(program.operationIndex + 3, result, param3Mode)
            program.operationIndex += length
        }

        companion object {
            val instance = Add()
        }
    }

    private class Multiply : Operation {
        override val length: Int = 4
        override fun execute(
            program: Program,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ) {
            val param1 = program.getAt(program.operationIndex + 1, param1Mode)
            val param2 = program.getAt(program.operationIndex + 2, param2Mode)
            val result = param1 * param2
            program.putAt(program.operationIndex + 3, result, param3Mode)
            program.operationIndex += length
        }

        companion object {
            val instance = Multiply()
        }
    }

    private class WriteInput : Operation {
        override val length: Int = 2
        override fun execute(
            program: Program,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ) {
            val input = program.getInput()
            program.putAt(program.operationIndex + 1, input, param1Mode)
            program.operationIndex += length
        }

        companion object {
            val instance = WriteInput()
        }
    }

    private class ProduceOutput : Operation {
        override val length: Int = 2
        override fun execute(
            program: Program,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ) {
            val param1 = program.getAt(program.operationIndex + 1, param1Mode)
            program.outputs.add(param1)
            program.operationIndex += length
            program.produceOutput(param1)
        }

        companion object {
            val instance = ProduceOutput()
        }
    }

    private class Terminate : Operation {
        override val length: Int = 1
        override fun execute(
            program: Program,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ) {
            // termination
        }

        companion object {
            val instance = Terminate()
        }
    }

    private class JumpIfTrue : Operation {
        override val length: Int = 3
        override fun execute(
            program: Program,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ) {
            val param1 = program.getAt(program.operationIndex + 1, param1Mode)
            val param2 = program.getAt(program.operationIndex + 2, param2Mode)
            program.operationIndex = if (param1 != 0L) param2 else (program.operationIndex + length)
        }

        companion object {
            val instance = JumpIfTrue()
        }
    }

    private class JumpIfFalse : Operation {
        override val length: Int = 3
        override fun execute(
            program: Program,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ) {
            val param1 = program.getAt(program.operationIndex + 1, param1Mode)
            val param2 = program.getAt(program.operationIndex + 2, param2Mode)
            program.operationIndex = if (param1 == 0L) param2 else program.operationIndex + length
        }

        companion object {
            val instance = JumpIfFalse()
        }
    }

    private class LessThan : Operation {
        override val length: Int = 4
        override fun execute(
            program: Program,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ) {
            val param1 = program.getAt(program.operationIndex + 1, param1Mode)
            val param2 = program.getAt(program.operationIndex + 2, param2Mode)
            val result = if (param1 < param2) 1L else 0L
            program.putAt(program.operationIndex + 3, result, param3Mode)
            program.operationIndex += length
        }

        companion object {
            val instance = LessThan()
        }
    }

    private class Equals : Operation {
        override val length: Int = 4
        override fun execute(
            program: Program,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ) {
            val param1 = program.getAt(program.operationIndex + 1, param1Mode)
            val param2 = program.getAt(program.operationIndex + 2, param2Mode)
            val result = if (param1 == param2) 1L else 0L
            program.putAt(program.operationIndex + 3, result, param3Mode)
            program.operationIndex += length
        }

        companion object {
            val instance = Equals()
        }
    }

    private class AdjustRelativeBase : Operation {
        override val length: Int = 2
        override fun execute(
            program: Program,
            param1Mode: ParamMode,
            param2Mode: ParamMode,
            param3Mode: ParamMode,
        ) {
            val param1 = program.getAt(program.operationIndex + 1, param1Mode)
            program.relativeBase += param1
            program.operationIndex += length
        }

        companion object {
            val instance = AdjustRelativeBase()
        }
    }

    private enum class ParamMode {
        POSITION, IMMEDIATE, RELATIVE;

        companion object {
            fun from(value: Long): ParamMode {
                return when (value) {
                    0L -> POSITION
                    1L -> IMMEDIATE
                    2L -> RELATIVE
                    else -> throw RuntimeException("Unknown mode")
                }
            }
        }
    }

    private enum class OperationType(val value: Int) {
        END(99), ADD(1), MULTIPLY(2), WRITE_INPUT(3), PRODUCE_OUTPUT(4),
        JUMP_IF_TRUE(5), JUMP_IF_FALSE(6), LESS_THAN(7), EQUALS(8), ADJUST_RELATIVE_BASE(9);

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

    private data class OperationInfo(
        private val type: OperationType,
        val param1Mode: ParamMode,
        val param2Mode: ParamMode,
        val param3Mode: ParamMode,
    ) {
        override fun toString(): String {
            val params = when (operation.length - 1) {
                0 -> ""
                1 -> param1Mode.name
                2 -> "${param1Mode.name} ${param2Mode.name}"
                3 -> "${param1Mode.name} ${param2Mode.name} ${param3Mode.name}"
                else -> throw RuntimeException("No valid params")
            }

            return "${type.name} $params"
        }

        private val operation = when (type) {
            OperationType.ADD -> Add.instance
            OperationType.MULTIPLY -> Multiply.instance
            OperationType.END -> Terminate.instance
            OperationType.WRITE_INPUT -> WriteInput.instance
            OperationType.PRODUCE_OUTPUT -> ProduceOutput.instance
            OperationType.JUMP_IF_TRUE -> JumpIfTrue.instance
            OperationType.JUMP_IF_FALSE -> JumpIfFalse.instance
            OperationType.LESS_THAN -> LessThan.instance
            OperationType.EQUALS -> Equals.instance
            OperationType.ADJUST_RELATIVE_BASE -> AdjustRelativeBase.instance
        }

        fun isTerminal() = operation is Terminate

        fun execute(program: Program) =
            operation.execute(program, param1Mode, param2Mode, param3Mode)
    }
}