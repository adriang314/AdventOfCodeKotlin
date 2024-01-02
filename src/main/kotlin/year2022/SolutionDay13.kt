package year2022

import common.BaseSolution
import kotlin.math.min
import kotlin.math.sign

fun main() = println(SolutionDay13().result())

private const val grpStart = '['
private const val grpEnd = ']'

class SolutionDay13 : BaseSolution() {
    override val day = 13
    override val year = 2022

    override fun task1(): String {
        val rightOrder = packets.filter { it.isRightOrder() }
        return rightOrder.sumOf { it.id }.toString()
    }

    override fun task2(): String {
        val divider1 = PacketList(listOf(PacketList(listOf(PacketValueItem(2)))))
        val divider2 = PacketList(listOf(PacketList(listOf(PacketValueItem(6)))))
        val packetsWithDividers = packets.plus(Packets(0, divider1, divider2))
        val packetsSplit = packetsWithDividers.map { listOf(it.left, it.right) }.flatten().sorted()
        val divider1Idx = packetsSplit.indexOf(divider1) + 1
        val divider2Idx = packetsSplit.indexOf(divider2) + 1
        return (divider1Idx * divider2Idx).toString()
    }

    private var packets: List<Packets>

    init {
        val groups = input().split("\r\n\r\n")
        packets = groups.indices.map {
            val packets = groups[it].split("\r\n")
            Packets(it + 1, PacketList(packetItems(packets[0])), PacketList(packetItems(packets[1])))
        }
    }

    data class Packets(val id: Int, val left: PacketList, val right: PacketList) {
        fun isRightOrder() = left <= right
    }

    private fun packetItems(text: String): List<PacketItem> {
        val items = mutableListOf<PacketItem>()
        val group = text.substring(1, text.length - 1) // removing [ ]
        var idx = 0
        while (idx < group.length) {
            if (group.getOrNull(idx) == grpStart) { // find subgroup
                val newGroupClosingIdx = findClosingGroupIndex(group, idx)
                val newGroup = group.substring(idx, newGroupClosingIdx + 1)
                items.add(PacketList(packetItems(newGroup)))
                idx = newGroupClosingIdx + 2
            } else { // find value item
                val nextComma = group.indexOf(',', idx)
                if (nextComma == -1) {
                    val value = group.substring(idx)
                    items.add(PacketValueItem(value.toInt()))
                    break
                } else {
                    val value = group.substring(idx, nextComma)
                    items.add(PacketValueItem(value.toInt()))
                    idx = nextComma + 1
                }
            }
        }

        return items
    }

    private fun findClosingGroupIndex(text: String, openPos: Int): Int {
        var closePos = openPos
        var counter = 1
        while (counter > 0) {
            val c = text[++closePos]
            if (c == grpStart)
                counter++
            else if (c == grpEnd)
                counter--
        }
        return closePos
    }

    interface PacketItem

    data class PacketList(val values: List<PacketItem>) : PacketItem, Comparable<PacketList> {
        override fun compareTo(other: PacketList): Int {
            val minSize: Int = min(values.size, other.values.size)
            for (i in 0..<minSize) {
                val leftValue = values[i]
                val rightValue = other.values[i]
                if (leftValue is PacketValueItem && rightValue is PacketValueItem) {
                    val compare = leftValue.compareTo(rightValue)
                    if (compare != 0) return compare
                } else if (leftValue is PacketValueItem && rightValue is PacketList) {
                    val compare = leftValue.toList().compareTo(rightValue)
                    if (compare != 0) return compare
                } else if (leftValue is PacketList && rightValue is PacketValueItem) {
                    val compare = leftValue.compareTo(rightValue.toList())
                    if (compare != 0) return compare
                } else if (leftValue is PacketList && rightValue is PacketList) {
                    val compare = leftValue.compareTo(rightValue)
                    if (compare != 0) return compare
                }
            }

            return (values.size - other.values.size).sign
        }
    }

    data class PacketValueItem(val value: Int) : PacketItem, Comparable<PacketValueItem> {
        fun toList() = PacketList(listOf(this))
        override fun compareTo(other: PacketValueItem): Int = value.compareTo(other.value)
    }
}