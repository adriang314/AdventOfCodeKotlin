package year2022

import common.BaseSolution

fun main() = println(SolutionDay10().result())

class SolutionDay10 : BaseSolution() {

    override val day = 10
    
    override fun task1(): String {
        val result = mapOf(
            20 to register[20]!!,
            60 to register[60]!!,
            100 to register[100]!!,
            140 to register[140]!!,
            180 to register[180]!!,
            220 to register[220]!!,
        ).map { it.key * it.value }.sum()
        return result.toString()
    }

    override fun task2(): String {
        val image = StringBuffer()
        for (clock in 1..240) {
            val spritePosition = register[clock]!!
            val crtPosition = ((clock - 1) % 40)
            if (crtPosition in spritePosition - 1..spritePosition + 1)
                image.append("#")
            else
                image.append(".")
        }

        println(image.substring(0, 40))
        println(image.substring(40, 80))
        println(image.substring(80, 120))
        println(image.substring(120, 160))
        println(image.substring(160, 200))
        println(image.substring(200, 240))

        return "EHZFZHCZ"
    }

    private val register: MutableMap<Int, Int> = mutableMapOf()

    init {
        val operations = input().split("\r\n").map {
            if (it == "noop") NoOperation.instance
            else AddXOperation(it.split(" ").last().toInt())
        }

        executeOperations(operations)
    }

    private fun executeOperations(operations: List<Operation>) {
        val operationStack = ArrayDeque(operations)
        var currentOperation = operationStack.removeFirstOrNull()
        var value = 1
        var operationTime = 0
        for (clock in 1..240) {
            if (operationTime >= currentOperation!!.cyclesToExecute) {
                if (currentOperation is AddXOperation)
                    value += currentOperation.x
                currentOperation = operationStack.removeFirstOrNull()
                register[clock] = value
                operationTime = 1
            } else {
                register[clock] = value
                operationTime++
            }
        }
    }

    interface Operation {
        val cyclesToExecute: Int
    }

    data class AddXOperation(val x: Int) : Operation {
        override val cyclesToExecute = 2
    }

    class NoOperation : Operation {
        override val cyclesToExecute = 1

        companion object {
            val instance = NoOperation()
        }
    }
}