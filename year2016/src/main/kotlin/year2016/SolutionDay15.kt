package year2016

import common.BaseSolution

fun main() = println(SolutionDay15().result())

class SolutionDay15 : BaseSolution() {

    override val day = 15

    private val disks = input().split("\r\n").map { line ->
        val regex = Regex("""Disc #(\d+) has (\d+) positions; at time=0, it is at position (\d+).""")
        val matchResult = regex.find(line) ?: throw IllegalArgumentException("Invalid input line: $line")
        val (discNumber, positions, startPosition) = matchResult.destructured
        Disk(positions.toInt(), startPosition.toInt())
    }

    override fun task1(): String {
        val result = Sculpture(disks).whenToPressButton()
        return result.toString()
    }

    override fun task2(): String {
        val result = Sculpture(disks + Disk(11, 0)).whenToPressButton()
        return result.toString()
    }

    private class Sculpture(private val disks: List<Disk>) {
        fun whenToPressButton(): Int {
            var time = 0
            while (true) {
                if (disks.withIndex().all { (index, disk) -> disk.positionAt(time + index + 1) == 0 }) {
                    return time
                }
                time++
            }
        }
    }

    private data class Disk(val positions: Int, val startPosition: Int) {
        fun positionAt(time: Int): Int = (startPosition + time) % positions
    }
}