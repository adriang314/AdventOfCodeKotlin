package year2022

import common.BaseSolution
import java.util.LinkedList
import kotlin.math.abs

fun main() = println(SolutionDay20().result())

class SolutionDay20 : BaseSolution() {
    override val day = 20
    override val year = 2022

    override fun task1(): String {
        val decryptedFile = DecryptedFile(LinkedList(encryptedFile))
        encryptedFile.forEach { number -> decryptedFile.move(number) }
        val result = readResult(decryptedFile)
        return result.toString()
    }

    override fun task2(): String {
        val decryptionKey = 811589153L
        val encryptedFileWithDecryptionKey = encryptedFile.map { Number(it.idx, it.value * decryptionKey) }
        val decryptedFile =DecryptedFile(LinkedList(encryptedFileWithDecryptionKey))

        repeat(10) {
            encryptedFileWithDecryptionKey.forEach { number -> decryptedFile.move(number) }
        }

        val result = readResult(decryptedFile)
        return result.toString()
    }

    private fun readResult(decryptedFile: DecryptedFile): Long {
        val zeroNumberIdx = decryptedFile.indexOf(zeroNumber)
        val numbers = decryptedFile.extendFileToSize(3000, zeroNumberIdx)
        val item1000 = numbers[zeroNumberIdx + 1000].value
        val item2000 = numbers[zeroNumberIdx + 2000].value
        val item3000 = numbers[zeroNumberIdx + 3000].value
        return item1000 + item2000 + item3000
    }

    private val encryptedFile = input().split("\r\n").mapIndexed { index, s -> Number(index, s.toLong()) }
    private val zeroNumber = encryptedFile.first { it.value == 0L }

    data class DecryptedFile(val items: LinkedList<Number>) {
        private val length = items.size

        fun indexOf(number: Number) = items.indexOf(number)

        fun move(number: Number) {
            val shiftBy = (abs(number.value) % (length - 1)).toInt()
            val currNumberIdx = items.indexOf(number)

            if (shiftBy == 0)
                return

            if (number.value > 0) {
                items.remove(number)
                if (currNumberIdx + shiftBy < length) {
                    items.add(currNumberIdx + shiftBy, number)
                } else {
                    items.add(shiftBy - (length - currNumberIdx - 1), number)
                }
            } else if (number.value < 0) {
                items.remove(number)
                if (currNumberIdx - shiftBy > 0) {
                    items.add(currNumberIdx - shiftBy, number)
                } else {
                    items.add(length - 1 - (shiftBy - currNumberIdx), number)
                }
            }
        }

        fun extendFileToSize(extension: Int, fromIdx: Int): List<Number> {
            val requiredSize = fromIdx + extension
            var newFile = items.toList()
            while (newFile.size < requiredSize)
                newFile = newFile.plus(items)

            return newFile
        }

        override fun toString(): String {
            return items.mapIndexed { index, number -> Pair(index, number.value) }
                .sortedBy { it.first }
                .map { it.second }
                .joinToString(" ")
        }
    }

    data class Number(val idx: Int, val value: Long)
}