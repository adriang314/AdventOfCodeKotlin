package year2016

import common.BaseSolution
import common.Hash

fun main() = println(SolutionDay14().result())

class SolutionDay14 : BaseSolution() {

    override val day = 14

    override fun task1(): String {
        val result = KeyFinder(input()).find(63, false)
        return result.toString()
    }

    override fun task2(): String {
        val result = KeyFinder(input()).find(63, true)
        return result.toString()
    }

    private class KeyFinder(val salt: String) {

        fun find(keyIndex: Int, stretchHash: Boolean): Int {
            var foundKeys = 0
            var index = 0
            val hashCache = mutableMapOf<Int, String>()
            while (true) {
                val hash = getHash(index, stretchHash, hashCache)
                findTripletChar(hash)?.let { tripletChar ->
                    if (hasQuintupletInNextThousandHashes(tripletChar, index, stretchHash, hashCache)) {
                        if (++foundKeys == keyIndex + 1) {
                            return index
                        }
                    }
                }
                index++
            }
        }

        private fun hasQuintupletInNextThousandHashes(char: Char, currentIndex: Int, stretchHash: Boolean, hashCache: MutableMap<Int, String>): Boolean {
            val quintuplet = "$char$char$char$char$char"
            for (i in 1..1000) {
                val hash = getHash(currentIndex + i, stretchHash, hashCache)
                if (hash.contains(quintuplet)) {
                    return true
                }
            }
            return false
        }

        private fun getHash(index: Int, stretchHash: Boolean, hashCache: MutableMap<Int, String>): String {
            if (hashCache.containsKey(index)) {
                return hashCache[index]!!
            }
            var hash = Hash.md5(salt + index)
            if (stretchHash) {
                hash = Hash.md5(hash, 2016)
            }
            hashCache[index] = hash
            return hash
        }

        private fun findTripletChar(hash: String): Char? {
            for (i in 0 until hash.length - 2) {
                if (hash[i] == hash[i + 1] && hash[i] == hash[i + 2]) {
                    return hash[i]
                }
            }
            return null
        }
    }
}