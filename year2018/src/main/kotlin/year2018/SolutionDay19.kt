package year2018

import common.BaseSolution
import common.Register
import common.sumOfAllDividers

fun main() = println(SolutionDay19().result())

class SolutionDay19 : BaseSolution() {

    override val day = 19

    private val instructionPointer: InstructionPointer
    private val operationInfos: List<OperationInfo>

    init {
        val lines = input().split("\r\n")
        val instructionPointerRegex = Regex("#ip (\\d+)")
        val operationRegex = Regex("^(\\w+) (\\d+) (\\d+) (\\d+)$")

        val tmpOperationInfos = mutableListOf<OperationInfo>()

        val ipMatch = instructionPointerRegex.find(lines.first())!!
        instructionPointer = InstructionPointer(ipMatch.groupValues[1].toLong())

        lines.drop(1).forEach { line ->
            val (opcode, inputA, inputB, outputC) = operationRegex.find(line)!!.destructured
            tmpOperationInfos.add(OperationInfo(opcode, inputA.toLong(), inputB.toLong(), outputC.toLong()))
        }

        operationInfos = tmpOperationInfos
    }

    override fun task1(): String {
        val executor = OperationExecutor(instructionPointer, operationInfos)
        executor.execute()
        val result = executor.register.read(0)
        return result.toString()
    }

    override fun task2(): String {
//        printing registry, when registry zero changes value reveals pattern
//        r2 * r4 = r3
//        r4 - sequence of dividers of r3
//        r0 - sum of all 'current' dividers
//        [0 35 0 10551408 0 10550400]
//        [1 8 10551408 10551408 1 1]
//        [3 8 5275704 10551408 2 1]
//        [6 8 3517136 10551408 3 1]
//        [10 8 2637852 10551408 4 1]
//        [16 8 1758568 10551408 6 1]
//        [23 8 1507344 10551408 7 1]
//        [31 8 1318926 10551408 8 1]
//        [43 8 879284 10551408 12 1]
//        [57 8 753672 10551408 14 1]
//        [73 8 659463 10551408 16 1]
//        [94 8 502448 10551408 21 1]
//        [118 8 439642 10551408 24 1]
//        [146 8 376836 10551408 28 1]
//        [177 8 340368 10551408 31 1]
//        [219 8 251224 10551408 42 1]
//        [267 8 219821 10551408 48 1]
//        [323 8 188418 10551408 56 1]
//        [385 8 170184 10551408 62 1]
//        [469 8 125612 10551408 84 1]
//        [562 8 113456 10551408 93 1]
//        [674 8 94209 10551408 112 1]
//        [798 8 85092 10551408 124 1]
//        [966 8 62806 10551408 168 1]
//        [1152 8 56728 10551408 186 1]
//        [1369 8 48624 10551408 217 1]
//        [1617 8 42546 10551408 248 1]
//        [1953 8 31403 10551408 336 1]
//        [2325 8 28364 10551408 372 1]
//        [2759 8 24312 10551408 434 1]
        val result = 10551408L.sumOfAllDividers()
        return result.toString()
    }

    private class OperationExecutor(
        private val instructionPointer: InstructionPointer,
        private val operations: List<OperationInfo>,
        val register: Register = Register(6).also { it.store(0, 0) }
    ) {
        fun execute() {
//            var r0Value = register.read(0)
            while (true) {
                val ipValue = instructionPointer.readValue(register)
                if (ipValue < 0 || ipValue >= operations.size) {
                    break
                }

                val operationInfo = operations[ipValue.toInt()]
                val operation = OperationSelector.get(operationInfo.opcode)
                operation.execute(register, operationInfo)
                instructionPointer.storeValue(register, instructionPointer.readValue(register) + 1)

//                val newR0Value = register.read(0)
//                if (newR0Value != r0Value) {
//                    r0Value = newR0Value
//                    println(register)
//                }
            }
        }
    }
}
