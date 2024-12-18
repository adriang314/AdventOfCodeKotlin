package year2020

import common.BaseSolution
import kotlin.math.max
import kotlin.math.min

fun main() = println(SolutionDay23().result())

class SolutionDay23 : BaseSolution() {

    override val day = 23
    
    override fun task1(): String {
        val game = GameOfCups(cups.toIntArray(), 9)
        repeat(100) {
            game.playRound()
        }
        return game.result1().toString()
    }

    override fun task2(): String {
        val game = GameOfCups(cups.plus(10..1_000_000).toIntArray(), 1_000_000)
        repeat(10_000_000) {
            if (it % 100_000 == 0)
                println("Iteration $it")
            game.playRound()
        }
        return game.result2().toString()
    }

    private val cups = input().map { it.digitToInt() }

    private class GameOfCups(private val array: IntArray, private val maxValue: Int) {

        private var removed1: Int = 0
        private var removed2: Int = 0
        private var removed3: Int = 0
        private var zeroIdx: Int = 0
        private var currentCupIdx = 0
        private var destinationCupIdx = 0

        override fun toString() = array.joinToString(",") + " [${array[currentCupIdx]}]"

        fun result1(): Long {
            val oneIdx = array.indexOf(1)
            return (1..8).map { array[(oneIdx + it) % array.size] }.joinToString("").toLong()
        }

        fun result2(): Long {
            val oneIdx = array.indexOf(1)
            val part1 = array[(oneIdx + 1) % array.size]
            val part2 = array[(oneIdx + 2) % array.size]
            return 1L * part1 * part2
        }

        fun playRound() {
            removeThreeAfterIdx(currentCupIdx)
            val currentCup = array[currentCupIdx]
            destinationCupIdx = findDestinationCup(if (currentCup - 1 == 0) maxValue else currentCup - 1)
            addRemovedBack()
            currentCupIdx = (currentCupIdx + 1) % array.size
        }

        private fun addRemovedBack() {
            // total numbers to be shifted left to make space for removed ones
            val numbersToShift = if (destinationCupIdx > zeroIdx) destinationCupIdx - (zeroIdx + 3) + 1
            else destinationCupIdx + array.size - (zeroIdx + 3) + 1

            if (destinationCupIdx > zeroIdx) {
                // shift right block to left by 3
                val shiftRightBlockLength = destinationCupIdx - (zeroIdx + 3) + 1
                System.arraycopy(array, zeroIdx + 3, array, zeroIdx, shiftRightBlockLength)
            } else {
                // shift right block to left by 3
                val shiftRightBlockLength = array.size - (zeroIdx + 3)
                if (shiftRightBlockLength > 0)
                    System.arraycopy(array, zeroIdx + 3, array, zeroIdx, shiftRightBlockLength)

                // number of zeros at the end
                val endingZeros = array.size - zeroIdx - max(shiftRightBlockLength, 0)
                // number of zeros at the start
                val startingZeros = 3 - endingZeros

                // shift starting numbers (max 3) to the end to fill ending zeros
                val shiftStartingBlockToEnd = min(endingZeros, destinationCupIdx + 1 - startingZeros)
                if (shiftStartingBlockToEnd > 0)
                    System.arraycopy(array, startingZeros, array, array.size - endingZeros, shiftStartingBlockToEnd)

                // shift left block to left by 3
                val shiftLeftBlockLength = destinationCupIdx + 1 - startingZeros - shiftStartingBlockToEnd
                if (shiftLeftBlockLength > 0)
                    System.arraycopy(array, destinationCupIdx - shiftLeftBlockLength + 1, array, 0, shiftLeftBlockLength)
            }

            // fill back removed numbers
            array[normalizeIdx(zeroIdx + numbersToShift)] = removed1
            array[normalizeIdx(zeroIdx + numbersToShift + 1)] = removed2
            array[normalizeIdx(zeroIdx + numbersToShift + 2)] = removed3
        }

        private fun removeThreeAfterIdx(idx: Int) {
            val idx1 = normalizeIdx(idx + 1)
            val idx2 = normalizeIdx(idx + 2)
            val idx3 = normalizeIdx(idx + 3)
            removed1 = array[idx1]
            removed2 = array[idx2]
            removed3 = array[idx3]
            array[idx1] = 0
            array[idx2] = 0
            array[idx3] = 0
            zeroIdx = idx1
        }

        private fun findDestinationCup(value: Int): Int {
            val idx = array.indexOf(value)
            if (idx < 0)
                return findDestinationCup(if (value - 1 == 0) maxValue else value - 1)
            return idx
        }

        private fun normalizeIdx(idx: Int) = if (idx >= 0) idx % array.size else array.size + idx
    }
}