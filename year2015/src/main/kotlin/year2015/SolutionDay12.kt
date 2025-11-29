package year2015

import common.BaseSolution
import kotlinx.serialization.json.*

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {

    override val day = 12

    override fun task1(): String {
        val sum = "-?\\d+".toRegex().findAll(input()).sumOf { it.value.toInt() }
        return sum.toString()
    }

    override fun task2(): String {
        var sum = 0
        val jsonInput = Json.parseToJsonElement(input())
        val stack = ArrayDeque(listOf(jsonInput))
        while (stack.isNotEmpty()) {
            when (val element = stack.removeLast()) {
                is JsonObject -> if (!hasRedProperty(element)) element.values.forEach { stack.add(it) }
                is JsonArray -> element.forEach { stack.add(it) }
                is JsonPrimitive -> sum += element.intOrNull ?: 0
            }
        }

        return sum.toString()
    }

    private fun hasRedProperty(element: JsonObject): Boolean = element.values.any { it is JsonPrimitive && it.isString && it.content == "red" }
}