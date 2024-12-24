package year2024

import common.BaseSolution

fun main() = println(SolutionDay24().result())

class SolutionDay24 : BaseSolution() {

    override val day = 24

    private val initGateRegex = Regex("^(\\S+): (\\d+)$")
    private val gateOperationRegex = Regex("^(\\S+) (AND|OR|XOR) (\\S+) -> (\\S+)$")
    private val initialInput: List<Gate>
    private val operations: List<Operation>

    init {
        var readingInitGates = true
        val lines = input().split("\r\n")
        val tmpInput = mutableListOf<Gate>()
        val tmpOperationInput = mutableSetOf<Gate>()
        val tmpOperations = mutableListOf<Operation>()
        lines.forEach { line ->
            if (line.isEmpty()) {
                readingInitGates = false
            } else if (readingInitGates) {
                val (name, value) = initGateRegex.find(line)!!.destructured
                val gate = Gate(name).also { it.value = value == "1" }
                tmpInput.add(gate)
            } else {
                val (name1, op, name2, name3) = gateOperationRegex.find(line)!!.destructured

                var gate1 = tmpOperationInput.firstOrNull { it.name == name1 }
                var gate2 = tmpOperationInput.firstOrNull { it.name == name2 }
                var gate3 = tmpOperationInput.firstOrNull { it.name == name3 }

                if (gate1 == null) {
                    gate1 = Gate(name1)
                    tmpOperationInput.add(gate1)
                }
                if (gate2 == null) {
                    gate2 = Gate(name2)
                    tmpOperationInput.add(gate2)
                }

                if (gate3 == null) {
                    gate3 = Gate(name3)
                    tmpOperationInput.add(gate3)
                }

                val operation = Operation(gate1, gate2, gate3, OperationType.valueOf(op))
                tmpOperations.add(operation)
            }
        }

        initialInput = tmpInput
        operations = tmpOperations
    }

    override fun task1(): String {
        val result = OperationExecutor(operations, initialInput).execute()
        return result.toString()
    }

    override fun task2(): String {
        val graphvizInput = buildGraphvizInput()

        val incorrectOps = mutableSetOf<String>()

        val zOps = operations.filter { it.gate3.isZ }
        val xyXorOps = operations.filter {
            it.type == OperationType.XOR && it.gate1.number == it.gate2.number &&
                    (it.gate1.isX || it.gate1.isY) && (it.gate2.isX || it.gate2.isY)
        }

        // z** should be result of XOR operation as this is addition of two numbers, except of the last one which should be AND
        // finds three z17, z26, z39
        incorrectOps.addAll(zOps.filter { op -> op.type != OperationType.XOR && op.gate3.name != "z45" }
            .map { it.gate3.name })

        // from generated graph z17 should be swapped with wmp
        incorrectOps.add("wmp")
        swapOutput("z17", "wmp")

        // from generated graph z26 should be swapped with gvm
        incorrectOps.add("gvm")
        swapOutput("z26", "gvm")

        // from generated graph z39 should be swapped with qsb
        incorrectOps.add("qsb")
        swapOutput("z39", "qsb")

        // x** XOR y** should be used to calculate z**
        // finds gjc
        incorrectOps.addAll(xyXorOps.filter { op ->
            val number = op.gate1.number!!
            val zOp = operations.single { it.gate3.name == "z$number" }
            zOp.gate1.name != op.gate3.name && zOp.gate2.name != op.gate3.name && op.gate3.name != "z00"
        }.map { it.gate3.name })

        // from generated graph gjc should be swapped with qjj
        incorrectOps.add("qjj")
        swapOutput("qjj", "gjc")

        // test x + y = z
        val additionResult = OperationExecutor(operations, initialInput).execute()
        val x = initialInput.filter { it.isX }.sortedByDescending { it.name }
            .map { it.valueAsDigit() }.joinToString("").toLong(2)
        val y = initialInput.filter { it.isY }.sortedByDescending { it.name }
            .map { it.valueAsDigit() }.joinToString("").toLong(2)

        if (x + y != additionResult)
            throw RuntimeException("Not correct operations")

        val result = incorrectOps.sorted().joinToString(",")
        return result
    }

    private fun swapOutput(output1Name: String, output2Name: String) {
        val op1 = operations.single { it.gate3.name == output1Name }
        val op2 = operations.single { it.gate3.name == output2Name }

        val tmp = op1.gate3
        op1.gate3 = op2.gate3
        op2.gate3 = tmp
    }

    /**
     * Usage:
     * create an input file name and execute below command line to generate svg with graph visualization
     * dot -Tsvg input.txt > output.svg
     */
    private fun buildGraphvizInput(): String {
        val graphEdges = operations.flatMap { op ->
            listOf("${op.gate1.name} -> ${op.gate3.name};", "${op.gate2.name} -> ${op.gate3.name};")
        }.joinToString("\n")
        val input =
            """
               |digraph {
               |$graphEdges
               |}
            """
        return input.trimMargin()
    }

    private class OperationExecutor(val ops: List<Operation>, val input: List<Gate>) {

        fun execute(): Long {
            clearOperations()
            assignInput()
            calculateOperations()
            val result = buildResult()
            clearOperations()
            return result
        }

        private fun buildResult(): Long {
            val result = ops.asSequence()
                .filter { it.gate3.isZ }
                .map { it.gate3 }
                .sortedByDescending { it.name }
                .map { it.valueAsDigit() }
                .joinToString("")

            return result.toLong(2)
        }

        private fun calculateOperations() {
            var executableOps = ops.filter { it.canBeExecuted() }
            while (executableOps.isNotEmpty()) {
                executableOps.forEach { it.execute() }
                executableOps = ops.filter { it.canBeExecuted() }
            }
        }

        private fun assignInput() {
            ops.forEach { op ->
                input.firstOrNull { it.name == op.gate1.name }?.let { gateToSet ->
                    op.gate1.value = gateToSet.value
                }

                input.firstOrNull { it.name == op.gate2.name }?.let { gateToSet ->
                    op.gate2.value = gateToSet.value
                }
            }
        }

        private fun clearOperations() {
            ops.forEach {
                it.gate1.value = null
                it.gate2.value = null
                it.gate3.value = null
            }
        }
    }

    private class Operation(val gate1: Gate, val gate2: Gate, var gate3: Gate, val type: OperationType) {

        fun canBeExecuted() = gate1.hasValue() && gate2.hasValue() && !gate3.hasValue()

        fun execute() {
            gate3.value = when (type) {
                OperationType.AND -> gate1.value!! && gate2.value!!
                OperationType.OR -> gate1.value!! || gate2.value!!
                OperationType.XOR -> gate1.value!!.xor(gate2.value!!)
            }
        }

        override fun toString() = "${gate1.name} $type ${gate2.name} -> ${gate3.name}"
    }

    private class Gate(val name: String) {
        val isX = name.startsWith("x")
        val isY = name.startsWith("y")
        val isZ = name.startsWith("z")
        val number = if (isX || isY || isZ) name.substring(1) else null
        var value: Boolean? = null
        fun hasValue() = value != null

        fun valueAsDigit() = when (value) {
            null -> throw RuntimeException("Cannot get value")
            else -> if (value == true) '1' else '0'
        }

        override fun toString() = "$name $value"
    }

    private enum class OperationType { AND, OR, XOR }
}