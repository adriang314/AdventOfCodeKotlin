package year2021

import common.BaseSolution

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16
    override val year = 2021

    override fun task1(): String {
        val outerPacket = BinaryPacketReader(binaryInput, readExtra = true).packets.first()
        val result = outerPacket.versionSum()
        return result.toString()
    }

    override fun task2(): String {
        val outerPacket = BinaryPacketReader(binaryInput, readExtra = true).packets.first()
        outerPacket.calculateValue()
        return outerPacket.value.toString()
    }

    private val binaryInput = HexToBinary(input()).binary

    class HexToBinary(hex: String) {
        val binary = hex.toList().joinToString("") { it.digitToInt(16).toString(2).padStart(4, '0') }
    }

    data class ReadIndex(var value: Int = 0) {
        fun increase(by: Int): Int {
            value += by
            return value
        }
    }

    data class BinaryPacketReader(
        val binary: String,
        val currIdx: ReadIndex = ReadIndex(),
        val maxCurrIdxToRead: Int? = null,
        val maxPacketsToRead: Int = Int.MAX_VALUE,
        val readExtra: Boolean = false,
    ) {
        val packets = mutableListOf<Packet>()

        init {
            while ((currIdx.value < (maxCurrIdxToRead ?: binary.length)) && packets.size < maxPacketsToRead) {
                val version = readBits(3).toInt(2)
                val typeId = PacketType.from(readBits(3).toInt(2))

                if (typeId == PacketType.Literal) {
                    val readLiteralValues = mutableListOf<String>()
                    do {
                        val literalValue = readBits(5)
                        readLiteralValues.add(literalValue.substring(1))
                        val isFinal = literalValue.first() == '0'
                    } while (!isFinal)

                    val literalValueDecimal = readLiteralValues.joinToString("").toLong(2)
                    packets.add(Packet(version, typeId, value = literalValueDecimal))
                } else {
                    val lengthTypeId = readBits(1)
                    if (lengthTypeId == "1") {
                        val numberOfSubPackets = readBits(11).toInt(2)
                        val subPackets =
                            BinaryPacketReader(binary, currIdx, maxPacketsToRead = numberOfSubPackets).packets
                        packets.add(Packet(version, typeId, subPackets = subPackets))
                    } else {
                        val totalLength = readBits(15).toInt(2)
                        val subPackets =
                            BinaryPacketReader(binary, currIdx, maxCurrIdxToRead = currIdx.value + totalLength).packets
                        packets.add(Packet(version, typeId, subPackets = subPackets))
                    }
                }

                if (readExtra) {
                    val extraBitsToRead = 4 - currIdx.value % 4
                    readBits(extraBitsToRead)
                }
            }
        }

        fun versionSum(): Int = packets.first().versionSum()

        private fun readBits(length: Int) = binary.substring(currIdx.value, currIdx.increase(length))
    }

    enum class PacketType(val typeId: Int) {
        Sum(0),
        Product(1),
        Min(2),
        Max(3),
        Literal(4),
        Greater(5),
        Less(6),
        Equal(7);

        companion object {
            fun from(id: Int) = entries.first { it.typeId == id }
        }
    }

    data class Packet(
        val version: Int,
        val typeId: PacketType,
        var value: Long? = null,
        val subPackets: List<Packet> = emptyList()
    ) {
        fun versionSum(): Int = version + subPackets.sumOf { it.versionSum() }

        fun calculateValue() {
            if (value != null)
                return
            value = if (allSubPacketsHasValue()) {
                calculatePacketValue()
            } else {
                subPackets.forEach { it.calculateValue() }
                if (allSubPacketsHasValue())
                    calculatePacketValue()
                else
                    throw Exception()
            }
        }

        private fun allSubPacketsHasValue() = subPackets.all { it.value != null }

        private fun calculatePacketValue(): Long {
            return when (typeId) {
                PacketType.Sum -> subPackets.sumOf { it.value!! }
                PacketType.Product -> subPackets.fold(1L) { acc: Long, packet: Packet -> acc * packet.value!! }
                PacketType.Min -> subPackets.minOf { it.value!! }
                PacketType.Max -> subPackets.maxOf { it.value!! }
                PacketType.Greater -> if (subPackets[0].value!! > subPackets[1].value!!) 1L else 0L
                PacketType.Less -> if (subPackets[0].value!! < subPackets[1].value!!) 1L else 0L
                PacketType.Equal -> if (subPackets[0].value!! == subPackets[1].value!!) 1L else 0L
                else -> throw Exception()
            }
        }

        override fun toString(): String {
            return when (typeId) {
                PacketType.Sum -> subPackets.joinToString("+") { "(${it})" }
                PacketType.Product -> subPackets.joinToString("*") { "($it)" }
                PacketType.Min -> "min(" + subPackets.joinToString(",") { "$it" } + ")"
                PacketType.Max -> "max(" + subPackets.joinToString(",") { "$it" } + ")"
                PacketType.Literal -> value!!.toString()
                PacketType.Greater -> "(${subPackets[0]} > ${subPackets[1]})"
                PacketType.Less -> "(${subPackets[0]} < ${subPackets[1]})"
                PacketType.Equal -> "(${subPackets[0]} == ${subPackets[1]})"
            }
        }
    }
}