package year2021

import common.BaseSolution
import year2021.SolutionDay08.Digit.*

fun main() = println(SolutionDay08().result())

class SolutionDay08 : BaseSolution() {

    override val day = 8
    
    override fun task1(): String {
        val result = lines.sumOf { it.count1478() }
        return result.toString()
    }

    override fun task2(): String {
        val result = lines.sumOf { it.decodeValue() }
        return result.toString()
    }

    private var lines = input().split("\r\n").map { line ->
        val parts = line.split(" | ")
        DigitMapping(
            parts[0].split(" ").map { it.toSet() },
            parts[1].split(" ").map { it.toSet() }
        )
    }

    data class DigitMapping(val map: List<Set<Char>>, val values: List<Set<Char>>) {

        private val decoded1 = map.first { it.size == One.length }.toSet()
        private val decoded4 = map.first { it.size == Four.length }.toSet()
        private val decoded7 = map.first { it.size == Seven.length }.toSet()
        private val decoded8 = map.first { it.size == Eight.length }.toSet()
        private val decodedBD = decoded4.minus(decoded1)
        private val decoded0 = map.first { it.size == Zero.length && !decodedBD.all { set -> it.contains(set) } }
        private val decodedABEG = decoded0.minus(decoded1)
        private val decoded9 = map.first { it.size == Nine.length && !decodedABEG.all { set -> it.contains(set) } }
        private val decoded6 = map.first { it.size == Six.length && it != decoded0 && it != decoded9 }
        private val decoded3 = map.first { it.size == Three.length && decoded7.intersect(it) == decoded7 }
        private val decoded5 = map.first { it.size == Five.length && decodedBD.intersect(it) == decodedBD }
        private val decoded2 = map.first { it.size == Two.length && it != decoded5 && it != decoded3 }
        private val listOf1478 = listOf(decoded1, decoded4, decoded7, decoded8)
        private val mapping = mapOf(
            decoded0 to Zero, decoded1 to One, decoded2 to Two, decoded3 to Three, decoded4 to Four,
            decoded5 to Five, decoded6 to Six, decoded7 to Seven, decoded8 to Eight, decoded9 to Nine,
        )

        fun count1478() = values.count { listOf1478.contains(it) }

        fun decodeValue() = values.map { mapping[it]!!.digit }.joinToString("").toInt()
    }

    enum class Digit(val set: Set<Char>, val digit: Char) {
        Zero(setOf('a', 'b', 'c', 'e', 'f', 'g'), '0'),          // 6
        One(setOf('c', 'f'), '1'),                               // 2
        Two(setOf('a', 'c', 'd', 'e', 'g'), '2'),                // 5
        Three(setOf('a', 'c', 'd', 'f', 'g'), '3'),              // 5
        Four(setOf('b', 'c', 'd', 'f'), '4'),                    // 4
        Five(setOf('a', 'b', 'd', 'f', 'g'), '5'),               // 5
        Six(setOf('a', 'b', 'd', 'e', 'f', 'g'), '6'),           // 6
        Seven(setOf('a', 'c', 'f'), '7'),                        // 3
        Eight(setOf('a', 'b', 'c', 'd', 'e', 'f', 'g'), '8'),    // 7
        Nine(setOf('a', 'b', 'c', 'd', 'f', 'g'), '9');          // 6

        val length = set.size
    }
}