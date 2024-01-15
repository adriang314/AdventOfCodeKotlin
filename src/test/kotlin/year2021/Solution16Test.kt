package year2021

import year2021.SolutionDay16.*
import kotlin.test.Test
import kotlin.test.assertEquals

class Solution16Test {

    @Test
    fun binaryTest1() {
        val input = BinaryPacketReader(HexToBinary("D2FE28").binary, readExtra = true)
        val packet = input.packets.first()
        assertEquals(Packet(6, PacketType.Literal, 2021), packet)
    }

    @Test
    fun binaryTest2() {
        val input = BinaryPacketReader(HexToBinary("38006F4529120").binary, readExtra = true)
        val packet = input.packets.first()
        assertEquals(PacketType.Less, packet.typeId)
        assertEquals(1, packet.version)
        assertEquals(Packet(6, PacketType.Literal, 10), packet.subPackets[0])
        assertEquals(Packet(2, PacketType.Literal, 20), packet.subPackets[1])
    }

    @Test
    fun binaryTest3() {
        val input = BinaryPacketReader(HexToBinary("EE00D40C82306").binary, readExtra = true)
        val packet = input.packets.first()
        assertEquals(PacketType.Max, packet.typeId)
        assertEquals(7, packet.version)
        assertEquals(Packet(2, PacketType.Literal, 1), packet.subPackets[0])
        assertEquals(Packet(4, PacketType.Literal, 2), packet.subPackets[1])
        assertEquals(Packet(1, PacketType.Literal, 3), packet.subPackets[2])
    }

    @Test
    fun binaryTest4() {
        val input = BinaryPacketReader(HexToBinary("8A004A801A8002F478").binary, readExtra = true)
        assertEquals(16, input.versionSum())
        val packet = input.packets.first()
        assertEquals(PacketType.Min, packet.typeId)
        assertEquals(4, packet.version)
        val subPacket = packet.subPackets[0]
        assertEquals(PacketType.Min, subPacket.typeId)
        assertEquals(1, subPacket.version)
        val subSubPacket = subPacket.subPackets[0]
        assertEquals(PacketType.Min, subSubPacket.typeId)
        assertEquals(5, subSubPacket.version)
        val subSubSubPacket = subSubPacket.subPackets[0]
        assertEquals(PacketType.Literal, subSubSubPacket.typeId)
        assertEquals(6, subSubSubPacket.version)
        assertEquals(15, subSubSubPacket.value)
    }

    @Test
    fun binaryTest5() {
        val input = BinaryPacketReader(HexToBinary("620080001611562C8802118E34").binary, readExtra = true)
        assertEquals(12, input.versionSum())
    }

    @Test
    fun binaryTest6() {
        val input = BinaryPacketReader(HexToBinary("C0015000016115A2E0802F18234").binary, readExtra = true)
        assertEquals(23, input.versionSum())
    }

    @Test
    fun binaryTest7() {
        val input = BinaryPacketReader(HexToBinary("A0016C880162017C3686B18A3D478").binary, readExtra = true)
        assertEquals(31, input.versionSum())
    }
}