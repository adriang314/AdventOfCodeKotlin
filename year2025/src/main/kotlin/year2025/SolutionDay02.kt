package year2025

import common.BaseSolution

fun main() = println(SolutionDay02().result())

class SolutionDay02 : BaseSolution() {

    override val day = 2

    private val ids = input().split(",").map {
        if (!it.contains("-")) {
            val id = it.toLong()
            return@map ID(id..id)
        }

        val parts = it.split("-")
        ID(parts[0].toLong()..parts[1].toLong())
    }

    override fun task1(): String {
        val validCount = ids.sumOf { it.validCount1() }
        return validCount.toString()
    }

    override fun task2(): String {
        val validCount = ids.sumOf { it.validCount2() }
        return validCount.toString()
    }

    private data class ID(val range: LongRange) {

        fun validCount2(): Long {
            var count = 0L
            range.forEach { value ->
                val valueAsString = value.toString()
                for (i in 1..valueAsString.length / 2) {
                    val valueChunked = valueAsString.chunked(i)
                    val allChunksSame = valueChunked.all { it == valueChunked[0] }
                    if (allChunksSame) {
                        count += value
                        break
                    }
                }
            }

            return count
        }


        fun validCount1(): Long {
            return range.sumOf { value ->
                val valueAsString = value.toString()
                val firstPart = valueAsString.take(valueAsString.length / 2)
                val secondPart = valueAsString.substring(valueAsString.length / 2)
                if (valueAsString.length % 2 == 0 && firstPart == secondPart) {
                    value
                } else {
                    0L
                }
            }
        }
    }
}