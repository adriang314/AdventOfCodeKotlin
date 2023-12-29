package year2022

import common.BaseSolution

fun main() {
    println("${SolutionDay04()}")
}

class SolutionDay04 : BaseSolution() {
    override val day = 4
    override val year = 2022

    override fun task1(): String {
        return assignments.filter { it.rangeFullOverlap }.size.toString()
    }

    override fun task2(): String {
        return assignments.filter { it.rangeOverlap }.size.toString()
    }

    private var assignments: List<Assignment> = input().split("\r\n").map { Assignment(it) }

    class Assignment(l: String) {
        private val range1: IntRange
        private val range2: IntRange
        val rangeFullOverlap: Boolean
        val rangeOverlap: Boolean

        init {
            val match = regex.find(l)!!

            range1 = IntRange(match.groupValues[1].toInt(), match.groupValues[2].toInt())
            range2 = IntRange(match.groupValues[3].toInt(), match.groupValues[4].toInt())
            rangeFullOverlap = range1.contains(range2)
            rangeOverlap = range1.overlap(range2)
        }

        private companion object {
            private fun IntRange.contains(other: IntRange): Boolean {
                val size = this.last - this.first + 1
                val otherSize = other.last - other.first + 1
                if (size > otherSize)
                    return other.all { this.contains(it) }
                return this.all { other.contains(it) }
            }

            private fun IntRange.overlap(other: IntRange): Boolean {
                val size = this.last - this.first + 1
                val otherSize = other.last - other.first + 1
                if (size > otherSize)
                    return other.any { this.contains(it) }
                return this.any { other.contains(it) }
            }

            private val regex = Regex("(\\d+)-(\\d+),(\\d+)-(\\d+)")
        }
    }
}