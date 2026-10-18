package year2019

import common.BaseSolution
import kotlin.math.abs

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16

    private val numberOfPhases = 100
    private val signalRepeatCount = 10_000
    private val msgLength = 8
    private val initialSignal = Signal(input().map { it.digitToInt() }.toIntArray())
    private val repeatedSignal: Signal
    private val messageOffset = input().take(7).toInt()

    init {
        val totalLength = initialSignal.length * signalRepeatCount
        repeatedSignal = Signal(IntArray(totalLength - messageOffset) { index -> initialSignal.digits[(messageOffset + index) % initialSignal.length] })
    }

    override fun task1(): String {
        var signal = initialSignal

        repeat(numberOfPhases) {
            signal = signal.executePhase()
        }

        return signal.firstDigits(msgLength)
    }

    override fun task2(): String {
        var signal = repeatedSignal

        repeat(numberOfPhases) {
            signal = signal.executePhase2()
        }

        return signal.firstDigits(msgLength)
    }

    private data class Block(val startIdx: Int, val endIdxExclusive: Int, val value: Int)

    private class AscendingSums(private val digits: IntArray) {
        private val sums: IntArray = IntArray(digits.size + 1)

        init {
            for (index in digits.indices) {
                sums[index + 1] = sums[index] + digits[index]
            }
        }

        fun sumOf(block: Block): Int {
            val end = minOf(block.endIdxExclusive, digits.size)
            if (block.startIdx >= end)
                return 0

            return (sums[end] - sums[block.startIdx]) * block.value
        }
    }

    private class Signal(val digits: IntArray) {
        // base pattern 0,1,0,-1
        private val basePatternLength = 4
        // when applying the pattern, skip the very first value exactly once
        private val negativeBlockOffset = 2

        val length: Int get() = digits.size

        fun firstDigits(count: Int): String = digits.take(count).joinToString("")

        fun executePhase(): Signal {
            val sums = AscendingSums(digits)
            val nextSignalDigits = IntArray(length)

            for (outputPosition in digits.indices) {
                val blockLength = outputPosition + 1
                var sum = 0
                var blockStart = outputPosition

                while (blockStart < length) {
                    val positiveBlock = Block(blockStart, blockStart + blockLength, 1)
                    sum += sums.sumOf(positiveBlock)

                    val negativeBlockStart = blockStart + negativeBlockOffset * blockLength
                    if (negativeBlockStart < length) {
                        val negativeBlock = Block(negativeBlockStart, negativeBlockStart + blockLength, -1)
                        sum += sums.sumOf(negativeBlock)
                    }

                    blockStart += basePatternLength * blockLength
                }

                nextSignalDigits[outputPosition] = abs(sum) % 10
            }

            return Signal(nextSignalDigits)
        }

        fun executePhase2(): Signal {
            // Optimized phase for a signal suffix whose original offset is in the second half of the complete signal.
            var cumulativeSum = 0
            for (index in digits.lastIndex downTo 0) {
                cumulativeSum = (cumulativeSum + digits[index]) % 10
                digits[index] = cumulativeSum
            }

            return this
        }
    }
}