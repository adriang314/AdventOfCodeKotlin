package year2021

import common.BaseSolution

fun main() = println(SolutionDay10().result())

class SolutionDay10 : BaseSolution() {

    override val day = 10
    override val year = 2021

    override fun task1(): String {
        val result = chunks.mapNotNull { it.firstCorruptedSign }.sumOf { it.corruptedValue }
        return result.toString()
    }

    override fun task2(): String {
        val results = chunks.map { it.completion }.filter { it.isNotEmpty() }.map { signs ->
            signs.fold(0L) { acc: Long, sign: Sign -> 5 * acc + sign.incompleteValue }
        }.sorted()
        val middleResult = results[(results.size - 1) / 2]
        return middleResult.toString()
    }

    private val chunks = input().split("\r\n").map { Chunk(it) }

    data class Chunk(val value: String) {
        private val cleanedValueSigns = cleanup().map { Sign.from(it) }
        private val isIncomplete = cleanedValueSigns.all { it.type == Type.Opening }
        val firstCorruptedSign = cleanedValueSigns.firstOrNull { it.type == Type.Closing }
        val completion = if (isIncomplete) cleanedValueSigns.map { it.opposite() }.reversed() else emptyList()

        private fun cleanup(): String {
            var cleanedValue = value
            do {
                val newCleanedValue = cleanedValue
                    .replace("[]", "")
                    .replace("<>", "")
                    .replace("()", "")
                    .replace("{}", "")
                if (newCleanedValue == cleanedValue)
                    break
                cleanedValue = newCleanedValue
            } while (true)

            return cleanedValue
        }
    }

    enum class Sign(val char: Char, val type: Type, val corruptedValue: Int, val incompleteValue: Int) {
        T1('[', Type.Opening, 57, 2),
        T2(']', Type.Closing, 57, 2),
        T3('(', Type.Opening, 3, 1),
        T4(')', Type.Closing, 3, 1),
        T5('{', Type.Opening, 1197, 3),
        T6('}', Type.Closing, 1197, 3),
        T7('<', Type.Opening, 25137, 4),
        T8('>', Type.Closing, 25137, 4);

        fun opposite() = when (this) {
            T1 -> T2
            T2 -> T1
            T3 -> T4
            T4 -> T3
            T5 -> T6
            T6 -> T5
            T7 -> T8
            T8 -> T7
        }

        companion object {
            fun from(c: Char) = when (c) {
                '[' -> T1
                ']' -> T2
                '(' -> T3
                ')' -> T4
                '{' -> T5
                '}' -> T6
                '<' -> T7
                '>' -> T8
                else -> throw Exception()
            }
        }
    }

    enum class Type { Opening, Closing }
}