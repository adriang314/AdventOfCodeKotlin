package year2018

import common.BaseSolution

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
                before = Register(reg0.toInt(), reg1.toInt(), reg2.toInt(), reg3.toInt())
            } else if (line.startsWith("After:")) {
                val (reg0, reg1, reg2, reg3) = registryRegex.find(line)!!.destructured
                after = Register(reg0.toInt(), reg1.toInt(), reg2.toInt(), reg3.toInt())
                tmpExamples.add(Example(before!!, after!!, operationInfo!!))
                before = null
                operationInfo = null
                after = null
            } else if (line.isNotEmpty()) {
                val (opcode, inputA, inputB, outputC) = operationRegex.find(line)!!.destructured
                operationInfo = OperationInfo(opcode.toInt(), inputA.toInt(), inputB.toInt(), outputC.toInt())
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
        val operationsMap = (0..15).associateWith { operations }.toMutableMap()

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

        val register = Register()
        operationInfos.forEach { operationsMap[it.opcode]!!.single().execute(register, it) }

        val result = register.read(0)
        return result.toString()
    }

    private interface Operation {
        fun execute(register: Register, info: OperationInfo)
    }

    private object AddRegisterOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) =
            register.store(register.read(info.inputA) + register.read(info.inputB), info.outputC)
    }

    private object AddImmediateOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) {
            register.store(register.read(info.inputA) + info.inputB, info.outputC)
        }
    }

    private object MulRegisterOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) =
            register.store(register.read(info.inputA) * register.read(info.inputB), info.outputC)
    }

    private object MulImmediateOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) {
            register.store(register.read(info.inputA) * info.inputB, info.outputC)
        }
    }

    private object AndRegisterOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) =
            register.store(register.read(info.inputA).and(register.read(info.inputB)), info.outputC)
    }

    private object AndImmediateOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) {
            register.store(register.read(info.inputA).and(info.inputB), info.outputC)
        }
    }

    private object OrRegisterOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) =
            register.store(register.read(info.inputA).or(register.read(info.inputB)), info.outputC)
    }

    private object OrImmediateOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) {
            register.store(register.read(info.inputA).or(info.inputB), info.outputC)
        }
    }

    private object SetRegisterOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) =
            register.store(register.read(info.inputA), info.outputC)
    }

    private object SetImmediateOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) {
            register.store(info.inputA, info.outputC)
        }
    }

    private object GtImmediateRegisterOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) {
            register.store(if (info.inputA > register.read(info.inputB)) 1 else 0, info.outputC)
        }
    }

    private object GtRegisterImmediateOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) {
            register.store(if (register.read(info.inputA) > info.inputB) 1 else 0, info.outputC)
        }
    }

    private object GtRegisterRegisterOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) {
            register.store(if (register.read(info.inputA) > register.read(info.inputB)) 1 else 0, info.outputC)
        }
    }

    private object EqImmediateRegisterOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) {
            register.store(if (info.inputA == register.read(info.inputB)) 1 else 0, info.outputC)
        }
    }

    private object EqRegisterImmediateOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) {
            register.store(if (register.read(info.inputA) == info.inputB) 1 else 0, info.outputC)
        }
    }

    private object EqRegisterRegisterOperation : Operation {
        override fun execute(register: Register, info: OperationInfo) {
            register.store(if (register.read(info.inputA) == register.read(info.inputB)) 1 else 0, info.outputC)
        }
    }

    private data class Register(
        private var reg0: Int = 0,
        private var reg1: Int = 0,
        private var reg2: Int = 0,
        private var reg3: Int = 0
    ) {
        fun read(regId: Int): Int = when (regId) {
            0 -> reg0
            1 -> reg1
            2 -> reg2
            3 -> reg3
            else -> throw RuntimeException("Unknown register id")
        }

        fun store(value: Int, regId: Int) = when (regId) {
            0 -> reg0 = value
            1 -> reg1 = value
            2 -> reg2 = value
            3 -> reg3 = value
            else -> throw RuntimeException("Unknown register id")
        }
    }

    private data class OperationInfo(val opcode: Int, val inputA: Int, val inputB: Int, val outputC: Int)

    private data class Example(val regBefore: Register, val regAfter: Register, val operationInfo: OperationInfo)
}
