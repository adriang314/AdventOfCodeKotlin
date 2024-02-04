package year2020

import common.BaseSolution

fun main() = println(SolutionDay14().result())

class SolutionDay14 : BaseSolution() {

    override val day = 14
    override val year = 2020

    override fun task1(): String {
        val result = programs.map { it.maskValues() }.reduce { acc, map -> acc.plus(map) }.values.sum()
        return result.toString()
    }

    override fun task2(): String {
        val result = programs.map { it.maskAddress() }.reduce { acc, map -> acc.plus(map) }.values.sum()
        return result.toString()
    }

    private val regex = Regex("mem\\[(\\d+)] = (\\d+)")
    private var programs: List<Program>

    init {
        val parts = input().split("mask = ").filter { it.isNotBlank() }
        programs =
            parts.map { program ->
                val lines = program.split("\r\n").filter { it.isNotBlank() }
                val values = lines.drop(1).map { line ->
                    val (address, value) = regex.find(line)!!.destructured
                    Value(address.toLong(), value.toLong())
                }
                Program(Mask(lines[0]), values)
            }
    }

    data class Program(val mask: Mask, val values: List<Value>) {

        fun maskValues() = values
            .map { Pair(it.address, maskValue(it.value)) }
            .associateBy { it.first }
            .mapValues { it.value.second }

        fun maskAddress(): Map<Long, Long> {
            val valuesMap = mutableMapOf<Long, Long>()
            values.forEach {
                val maskedAddresses = maskAddress(it.address)
                maskedAddresses.forEach { address -> valuesMap[address] = it.value }
            }
            return valuesMap
        }

        private fun maskAddress(address: Long): List<Long> {
            val binary = address.toString(2).padStart(36, '0')
            val maskedBinary = binary.zip(mask.value) { number: Char, mask: Char ->
                if (mask == '0') number else if (mask == '1') '1' else 'X'
            }.joinToString("")
            var addresses = listOf("")
            maskedBinary.forEach { char ->
                addresses = if (char != 'X') {
                    addresses.map { it + if (char == '0') char else '1' }
                } else {
                    addresses.map { it + '0' }.plus(addresses.map { it + '1' })
                }
            }

            return addresses.map { it.toLong(2) }
        }

        private fun maskValue(value: Long): Long {
            val binary = value.toString(2).padStart(36, '0')
            val maskedBinary = binary.zip(mask.value) { number: Char, mask: Char -> if (mask == 'X') number else mask }
                .joinToString("")
            return maskedBinary.toLong(2)
        }
    }

    data class Value(val address: Long, val value: Long)

    data class Mask(val value: String)
}