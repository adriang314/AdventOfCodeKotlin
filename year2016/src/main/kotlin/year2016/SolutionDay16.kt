package year2016

import common.BaseSolution

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16

    override fun task1(): String {
        val disk = Disk(272, input()).fill()
        val checksum = disk.checksum()
        return checksum
    }

    override fun task2(): String {
        val disk = Disk(35651584, input()).fill()
        val checksum = disk.checksum()
        return checksum
    }

    private data class Disk(val length: Int, val data: String) {
        fun fill(): Disk {
            var currentData = data
            while (currentData.length < length) {
                val updatedData = currentData.reversed().map { if (it == '0') '1' else '0' }.joinToString("")
                currentData = currentData + "0" + updatedData
            }
            return Disk(length, currentData.take(length))
        }

        fun checksum(): String {
            var currentChecksum = data
            while (currentChecksum.length % 2 == 0) {
                currentChecksum = currentChecksum.chunked(2).map { if (it[0] == it[1]) '1' else '0' }.joinToString("")
            }
            return currentChecksum
        }
    }
}