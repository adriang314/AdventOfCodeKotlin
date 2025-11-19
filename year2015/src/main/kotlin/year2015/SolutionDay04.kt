package year2015

import common.BaseSolution
import common.Hash

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4

    override fun task1(): String {
        return PasswordFinder.findHash(input(), "00000").toString()
    }

    override fun task2(): String {
        return PasswordFinder.findHash(input(), "000000").toString()
    }

    private class PasswordFinder {
        companion object {
            fun findHash(input: String, hashPrefix: String): Int {
                var index = 0
                while (true) {
                    val toHash = input + index.toString()
                    val hash = Hash.md5(toHash)
                    if (hash.startsWith(hashPrefix)) {
                        return index
                    }
                    index++
                }
            }
        }
    }
}