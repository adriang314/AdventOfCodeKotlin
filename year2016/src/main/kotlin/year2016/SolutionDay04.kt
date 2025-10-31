package year2016

import common.BaseSolution

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4

    private val encryptedRoomNames = input().split("\r\n").map { EncryptedRoomName.from(it) }

    override fun task1(): String {
        val result = encryptedRoomNames.filter { it.isReal }.sumOf { it.sectorId }
        return result.toString()
    }

    override fun task2(): String {
        val result = encryptedRoomNames.map { it.decrypt() }.single { it.isNorthPoleStorage }.sectorId
        return result.toString()
    }

    private data class EncryptedRoomName(val name: String, val sectorId: Int, val checksum: String) {
        companion object {
            private val regex = """([a-z-]+)-(\d+)\[([a-z]+)]""".toRegex()

            fun from(input: String): EncryptedRoomName {
                val matchResult = regex.matchEntire(input) ?: throw IllegalArgumentException("Invalid input: $input")
                val (name, sectorId, checksum) = matchResult.destructured
                return EncryptedRoomName(name, sectorId.toInt(), checksum)
            }
        }

        val isReal = run {
            val letterCounts = name.filter { it != '-' }
                .groupingBy { it }
                .eachCount()
            val sortedLetters = letterCounts.entries
                .sortedWith(compareByDescending<Map.Entry<Char, Int>> { it.value }.thenBy { it.key })
                .map { it.key }
                .take(5)
                .joinToString("")
            sortedLetters == checksum
        }

        fun decrypt(): DecryptedRoomName {
            val shift = sectorId % 26
            return DecryptedRoomName(name.map { char ->
                when (char) {
                    '-' -> ' '
                    else -> {
                        val base = 'a'.code
                        val shifted = (char.code - base + shift) % 26 + base
                        shifted.toChar()
                    }
                }
            }.joinToString(""), sectorId)
        }
    }

    private data class DecryptedRoomName(val name: String, val sectorId: Int) {
        val isNorthPoleStorage = name.contains("north")
    }
}