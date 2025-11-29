package year2017

import common.BaseSolution

fun main() = println(SolutionDay15().result())

class SolutionDay15 : BaseSolution() {

    override val day = 15

    private val generatorAInitialValue = input().lines().first().split(" ").last().toLong()
    private val generatorBInitialValue = input().lines().last().split(" ").last().toLong()
    private val generatorA = Generator(generatorAInitialValue, 16807L, 4L)
    private val generatorB = Generator(generatorBInitialValue, 48271L, 8L)
    private val judge = Judge(generatorA, generatorB)

    override fun task1(): String {
        val result = judge.judgePairs(40_000_000, generatorA::next, generatorB::next)
        return result.toString()
    }

    override fun task2(): String {
        val result = judge.judgePairs(5_000_000, generatorA::nextWithCriteria, generatorB::nextWithCriteria)
        return result.toString()
    }

    private class Generator(val initialValue: Long, private val factor: Long, private val multiple: Long) {
        fun next(prevValue: Long): Long = (prevValue * factor) % 2147483647L
        fun nextWithCriteria(prevValue: Long): Long {
            var nextValue = prevValue
            do {
                nextValue = next(nextValue)
            } while (nextValue % multiple != 0L)
            return nextValue
        }
    }

    private class Judge(private val genA: Generator, private val genB: Generator) {
        fun judgePairs(pairs: Int, nextA: (Long) -> Long, nextB: (Long) -> Long): Long {
            var count = 0L
            var valueA = nextA(genA.initialValue)
            var valueB = nextB(genB.initialValue)

            repeat(pairs) {
                valueA = nextA(valueA)
                valueB = nextB(valueB)

                if (valueA.toUShort() == valueB.toUShort()) {
                    count++
                }
            }

            return count
        }
    }
}