package year2018

import common.BaseSolution
import common.Register

fun main() = println(SolutionDay21().result())

class SolutionDay21 : BaseSolution() {

    override val day = 21

    private val executor: OperationExecutor

    init {
        val lines = input().split("\r\n")
        val instructionPointerRegex = Regex("#ip (\\d+)")
        val operationRegex = Regex("^(\\w+) (\\d+) (\\d+) (\\d+)$")

        val operationInfos = mutableListOf<OperationInfo>()

        val ipMatch = instructionPointerRegex.find(lines.first())!!
        val instructionPointer = InstructionPointer(ipMatch.groupValues[1].toLong())

        lines.drop(1).forEach { line ->
            val (opcode, inputA, inputB, outputC) = operationRegex.find(line)!!.destructured
            operationInfos.add(OperationInfo(opcode, inputA.toLong(), inputB.toLong(), outputC.toLong()))
        }

        executor = OperationExecutor(instructionPointer, operationInfos)
        executor.execute()
    }

    override fun task1(): String {
        return executor.lowestReg0MinOperationsToStop.toString()
    }

    override fun task2(): String {
        return executor.lowestReg0AllOperationsToStop.toString()
    }

    private class OperationExecutor(
        private val instructionPointer: InstructionPointer,
        private val operations: List<OperationInfo>,
        val register: Register = Register(6).also { it.store(0, 0) }
    ) {
        var lowestReg0MinOperationsToStop: Long? = null
        var lowestReg0AllOperationsToStop: Long? = null

        fun execute() {
            val values = mutableListOf<Long>()
            while (true) {
                val ipValue = instructionPointer.readValue(register)
                if (ipValue < 0 || ipValue >= operations.size) {
                    break
                }

                val operationInfo = operations[ipValue.toInt()]
                val operation = OperationSelector.get(operationInfo.opcode)

                // program will terminate if reg5 == reg0
                // so finding the first value of reg5 to terminate it asap
                // and finding the last value of reg5 that will cause all operation to be executed
                if (ipValue == 28L) {
                    val reg5Value = register.read(5L)
                    if (lowestReg0MinOperationsToStop == null) {
                        lowestReg0MinOperationsToStop = reg5Value
                    } else if (values.contains(reg5Value)) {
                        lowestReg0AllOperationsToStop = values.last()
                        break
                    } else {
                        values.add(reg5Value)
                    }
                }

                operation.execute(register, operationInfo)
                instructionPointer.storeValue(register, instructionPointer.readValue(register) + 1)
            }
        }
    }
}
