package year2018

import common.BaseSolution
import common.Register

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16

    private val examples: List<Example>
    private val operationInfos: List<OperationInfo>
    private val operations = listOf(
        AddRegisterOperation, AddImmediateOperation, MulRegisterOperation, MulImmediateOperation,
        AndRegisterOperation, AndImmediateOperation, OrRegisterOperation, OrImmediateOperation,
        SetRegisterOperation, SetImmediateOperation,
        GtRegisterRegisterOperation, GtRegisterImmediateOperation, GtImmediateRegisterOperation,
        EqRegisterRegisterOperation, EqRegisterImmediateOperation, EqImmediateRegisterOperation
    )

    init {
        val lines = input().split("\r\n")
        val registryRegex = Regex("^(?:Before:|After: ) \\[(\\d+), (\\d+), (\\d+), (\\d+)]$")
        val operationRegex = Regex("^(\\d+) (\\d+) (\\d+) (\\d+)$")

        var before: Register? = null
        var after: Register?
        var operationInfo: OperationInfo? = null

        val tmpOperationInfos = mutableListOf<OperationInfo>()
        val tmpExamples = mutableListOf<Example>()

        lines.forEach { line ->
            if (line.startsWith("Before:")) {
                val (reg0, reg1, reg2, reg3) = registryRegex.find(line)!!.destructured
                before = Register(reg0.toLong(), reg1.toLong(), reg2.toLong(), reg3.toLong())
            } else if (line.startsWith("After:")) {
                val (reg0, reg1, reg2, reg3) = registryRegex.find(line)!!.destructured
                after = Register(reg0.toLong(), reg1.toLong(), reg2.toLong(), reg3.toLong())
                tmpExamples.add(Example(before!!, after!!, operationInfo!!))
                before = null
                operationInfo = null
                after = null
            } else if (line.isNotEmpty()) {
                val (opcode, inputA, inputB, outputC) = operationRegex.find(line)!!.destructured
                operationInfo = OperationInfo(opcode, inputA.toLong(), inputB.toLong(), outputC.toLong())
                if (before == null) {
                    tmpOperationInfos.add(operationInfo!!)
                    operationInfo = null
                }
            }
        }

        examples = tmpExamples
        operationInfos = tmpOperationInfos
    }

    override fun task1(): String {
        val result = examples.associateWith { example ->
            operations.map { operation ->
                val register = example.regBefore.copy()
                operation.execute(register, example.operationInfo)
                register == example.regAfter
            }.count { it }
        }.filterValues { it >= 3 }.count()

        return result.toString()
    }

    override fun task2(): String {
        val operationsMap = (0..15).map { it.toString() }.associateWith { operations }.toMutableMap()

        examples.forEach { example ->
            operations.map { operation ->
                val register = example.regBefore.copy()
                operation.execute(register, example.operationInfo)
                val currentOperations = operationsMap[example.operationInfo.opcode]!!
                if (register != example.regAfter) {
                    operationsMap[example.operationInfo.opcode] = currentOperations.minus(operation)
                }
            }
        }

        while (operationsMap.any { it.value.size > 1 }) {
            val matchedOperations = operationsMap.filter { it.value.size == 1 }.map { it.value.single() }.toSet()
            operationsMap.filter { it.value.size > 1 }.forEach {
                operationsMap[it.key] = it.value.minus(matchedOperations)
            }
        }

        val register = Register(4)
        operationInfos.forEach { operationsMap[it.opcode]!!.single().execute(register, it) }

        val result = register.read(0)
        return result.toString()
    }

    private data class Example(val regBefore: Register, val regAfter: Register, val operationInfo: OperationInfo)
}
