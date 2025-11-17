package year2016

import common.BaseSolution
import common.Hash

fun main() = println(SolutionDay05().result())

class SolutionDay05 : BaseSolution() {

    override val day = 5

    override fun task1(): String {
        return PasswordFinder.findWithEightCharsUsingPosition(input(), false)
    }

    override fun task2(): String {
        return PasswordFinder.findWithEightCharsUsingPosition(input(), true)
    }

    private class PasswordFinder {
        companion object {
            fun findWithEightCharsUsingPosition(input: String, positionFromHash: Boolean): String {
                val passwordChars = mutableMapOf<Int, Char>()
                var searchIndex = 0
                var foundPasswordChars = 0
                while (foundPasswordChars < 8) {
                    val (index, hash) = findHashStartingWithFiveZeros(searchIndex, input)
                    searchIndex = index + 1
                    if (positionFromHash) {
                        val passwordPosition = hash[5].digitToIntOrNull()
                        if (passwordPosition in (0..7) && !passwordChars.containsKey(passwordPosition)) {
                            passwordChars[passwordPosition!!] = hash[6]
                            foundPasswordChars++
                        }
                    } else {
                        passwordChars[foundPasswordChars] = hash[5]
                        foundPasswordChars++
                    }
                }

                return passwordChars.toSortedMap().values.joinToString("")
            }

            private fun findHashStartingWithFiveZeros(initialSuffix: Int, input: String): Pair<Int, String> {
                var index = initialSuffix
                while (true) {
                    val toHash = input + index.toString()
                    val hash = Hash.md5(toHash)
                    if (hash.startsWith("00000")) {
                        return Pair(index, hash)
                    }
                    index++
                }
            }
        }
    }
}