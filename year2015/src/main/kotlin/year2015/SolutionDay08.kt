package year2015

import common.BaseSolution

fun main() = println(SolutionDay08().result())

class SolutionDay08 : BaseSolution() {

    override val day = 8

    private val codedStrings = input().split("\r\n").map { CodedString(it) }

    override fun task1(): String {
        val result = codedStrings.sumOf { it.raw.length - it.inMemory.length }
        return result.toString()
    }

    override fun task2(): String {
        val result = codedStrings.sumOf { it.encoded.length - it.raw.length }
        return result.toString()
    }

    private class CodedString(val raw: String) {
        val inMemory = buildString {
            var i = 1 // skip starting quote
            while (i < raw.length - 1) { // skip ending quote
                when {
                    raw[i] == '\\' && i + 1 < raw.length - 1 -> {
                        when (raw[i + 1]) {
                            '\\', '"' -> append(raw[i + 1]).also { i += 2 }
                            'x' -> if (i + 3 < raw.length - 1) append(raw.substring(i + 2, i + 4).toInt(16).toChar()).also { i += 4 } else append(raw[i++])
                            else -> append(raw[i++])
                        }
                    }

                    else -> append(raw[i++])
                }
            }
        }

        val encoded = buildString {
            append('"')
            for (c in raw) {
                when (c) {
                    '\\', '"' -> append('\\', c)
                    else -> append(c)
                }
            }
            append('"')
        }
    }
}