package year2020

import common.BaseSolution
import common.LinkedListWithCache

fun main() = println(SolutionDay23().result())

class SolutionDay23 : BaseSolution() {

    override val day = 23

    override fun task1(): String {
        val game = GameOfCups(LinkedListWithCache(cups), 9)
        repeat(100) {
            game.playRound()
        }
        return game.result1().toString()
    }

    override fun task2(): String {
        val game = GameOfCups(LinkedListWithCache(cups.plus(10..1_000_000)), 1_000_000)
        repeat(10_000_000) {
            game.playRound()
        }
        return game.result2().toString()
    }

    private val cups = input().map { it.digitToInt() }

    private class GameOfCups(private val cups: LinkedListWithCache<Int>, private val maxCup: Int) {
        private var removedCup1: Int = 0
        private var removedCup2: Int = 0
        private var removedCup3: Int = 0
        private var currentCup = cups.getFirst()!!
        private var destinationCup: Int = 0

        fun result1(): Long {
            return (1..8).map { cups.getAfter(1, it) }.joinToString("").toLong()
        }

        fun result2(): Long {
            val part1 = cups.getAfter(1, 1)
            val part2 = cups.getAfter(1, 2)
            return 1L * part1 * part2
        }

        fun playRound() {
            removeThreeAfterCurrentCup()
            setDestinationCup()
            addBackRemovedCups()
            setNewCurrentCup()
        }

        private fun addBackRemovedCups() {
            cups.addAfter(removedCup1, destinationCup)
            cups.addAfter(removedCup2, removedCup1)
            cups.addAfter(removedCup3, removedCup2)
        }

        private fun removeThreeAfterCurrentCup() {
            removedCup1 = cups.removeAfter(currentCup, 1)
            removedCup2 = cups.removeAfter(currentCup, 1)
            removedCup3 = cups.removeAfter(currentCup, 1)
        }

        private fun setNewCurrentCup() {
            currentCup = cups.getAfter(currentCup, 1)
        }

        private fun setDestinationCup() {
            destinationCup = currentCup - 1
            while (destinationCup <= 0 || destinationCup == removedCup1 || destinationCup == removedCup2 || destinationCup == removedCup3) {
                if (destinationCup == 0) {
                    destinationCup = maxCup
                } else {
                    destinationCup--
                }
            }
        }
    }
}