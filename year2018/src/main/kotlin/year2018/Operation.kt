package year2018

import common.Register

object OperationSelector {
    fun get(opcode: String): Operation = when (opcode) {
        "addr" -> AddRegisterOperation
        "addi" -> AddImmediateOperation
        "mulr" -> MulRegisterOperation
        "muli" -> MulImmediateOperation
        "banr" -> AndRegisterOperation
        "bani" -> AndImmediateOperation
        "borr" -> OrRegisterOperation
        "bori" -> OrImmediateOperation
        "setr" -> SetRegisterOperation
        "seti" -> SetImmediateOperation
        "gtir" -> GtImmediateRegisterOperation
        "gtri" -> GtRegisterImmediateOperation
        "gtrr" -> GtRegisterRegisterOperation
        "eqir" -> EqImmediateRegisterOperation
        "eqri" -> EqRegisterImmediateOperation
        "eqrr" -> EqRegisterRegisterOperation
        else -> throw IllegalArgumentException("Unknown opcode: $opcode")
    }
}

interface Operation {
    fun execute(register: Register, info: OperationInfo)
}

object AddRegisterOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        //println("addr: ${register.read(info.inputA)} + ${register.read(info.inputB)} -> ${info.outputC}")
        register.store(register.read(info.inputA) + register.read(info.inputB), info.outputC)
    }
}

object AddImmediateOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        //println("addi: ${register.read(info.inputA)} + ${info.inputB} -> ${info.outputC}")
        register.store(register.read(info.inputA) + info.inputB, info.outputC)
    }
}

object MulRegisterOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        //println("mulr: ${register.read(info.inputA)} * ${register.read(info.inputB)} -> ${info.outputC}")
        register.store(register.read(info.inputA) * register.read(info.inputB), info.outputC)
    }
}

object MulImmediateOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        //println("muli: ${register.read(info.inputA)} * ${info.inputB} -> ${info.outputC}")
        register.store(register.read(info.inputA) * info.inputB, info.outputC)
    }
}

object AndRegisterOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        val result = register.read(info.inputA).and(register.read(info.inputB))
        //println("banr: ${register.read(info.inputA)} and ${register.read(info.inputB)} == $result -> ${info.outputC}")
        register.store(result, info.outputC)
    }
}

object AndImmediateOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        val result = register.read(info.inputA).and(info.inputB)
        //println("bani: ${register.read(info.inputA)} and ${info.inputB} == $result -> ${info.outputC}")
        register.store(result, info.outputC)
    }
}

object OrRegisterOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        val result = register.read(info.inputA).or(register.read(info.inputB))
        //println("borr: ${register.read(info.inputA)} or ${register.read(info.inputB)} == $result -> ${info.outputC}")
        register.store(result, info.outputC)
    }
}

object OrImmediateOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        val result = register.read(info.inputA).or(info.inputB)
        //println("bori: ${register.read(info.inputA)} or ${info.inputB} == $result -> ${info.outputC}")
        register.store(result, info.outputC)
    }
}

object SetRegisterOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        //println("setr: ${register.read(info.inputA)} -> ${info.outputC}")
        register.store(register.read(info.inputA), info.outputC)
    }
}

object SetImmediateOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        //println("seti: ${info.inputA} -> ${info.outputC}")
        register.store(info.inputA, info.outputC)
    }
}

object GtImmediateRegisterOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        val result = if (info.inputA > register.read(info.inputB)) 1L else 0L
        //println("gtir: ${info.inputA} > ${register.read(info.inputB)}, $result -> ${info.outputC}")
        register.store(result, info.outputC)
    }
}

object GtRegisterImmediateOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        val result = if (register.read(info.inputA) > info.inputB) 1L else 0L
        //println("gtri: ${register.read(info.inputA)} > ${info.inputB}, $result -> ${info.outputC}")
        register.store(result, info.outputC)
    }
}

object GtRegisterRegisterOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        val result = if (register.read(info.inputA) > register.read(info.inputB)) 1L else 0L
        //println("gtrr: ${register.read(info.inputA)} > ${register.read(info.inputB)}, $result -> ${info.outputC}")
        register.store(result, info.outputC)
    }
}

object EqImmediateRegisterOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        val result = if (info.inputA == register.read(info.inputB)) 1L else 0L
        //println("eqir: ${info.inputA} == ${register.read(info.inputB)}, $result -> ${info.outputC}")
        register.store(result, info.outputC)
    }
}

object EqRegisterImmediateOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        val result = if (register.read(info.inputA) == info.inputB) 1L else 0L
        //println("eqri: ${register.read(info.inputA)} == ${info.inputB}, $result -> ${info.outputC}")
        register.store(result, info.outputC)
    }
}

object EqRegisterRegisterOperation : Operation {
    override fun execute(register: Register, info: OperationInfo) {
        val result = if (register.read(info.inputA) == register.read(info.inputB)) 1L else 0L
        //println("eqrr: ${register.read(info.inputA)} == ${register.read(info.inputB)} == $result -> ${info.outputC}")
        register.store(result, info.outputC)
    }
}

data class InstructionPointer(val regId: Long) {
    fun readValue(register: Register): Long = register.read(regId)
    fun storeValue(register: Register, value: Long) = register.store(value, this.regId)
}

data class OperationInfo(val opcode: String, val inputA: Long, val inputB: Long, val outputC: Long)