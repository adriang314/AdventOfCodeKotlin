package year2015

import com.google.common.math.LongMath.pow
import common.BaseSolution

fun main() = println(SolutionDay17().result())

class SolutionDay17 : BaseSolution() {

    override val day = 17

    private val containers = input().split("\r\n").map { it.toInt() }

    override fun task1(): String {
        return CodedContainers(containers).count(150).toString()
    }

    override fun task2(): String {
        return CodedContainers(containers).countWithMin(150).toString()
    }

    private class CodedContainers(private val sizes: List<Int>) {
        private val codedContainers = 0L until pow(2, sizes.size)
        private val decodedContainers = codedContainers.map { decode(it) }

        fun count(totalSize: Int): Int = decodedContainers.count { it.totalSize == totalSize }

        fun countWithMin(totalSize: Int): Int {
            val minUsed = decodedContainers.filter { it.totalSize == totalSize }.minOf { it.used }
            return decodedContainers.count { it.totalSize == totalSize && it.used == minUsed }
        }

        fun decode(codedContainer: Long): Containers = sizes.foldIndexed(Containers()) { index, containers, size ->
            if (codedContainer.and(pow(2L, index)) > 0L) {
                containers.totalSize += size
                containers.used += 1
            }
            containers
        }
    }

    private data class Containers(var totalSize: Int = 0, var used: Int = 0)
}