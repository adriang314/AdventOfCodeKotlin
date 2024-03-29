package year2020

import common.BaseSolution
import java.util.LinkedList

fun main() = println(SolutionDay22().result())

class SolutionDay22 : BaseSolution() {

    override val day = 22
    override val year = 2020

    override fun task1(): String {
        val game = CombatGame(LinkedList(player1.deck), LinkedList(player2.deck))
        game.playRegular()
        val result = game.score()
        return result.toString()
    }

    override fun task2(): String {
        val game = CombatGame(LinkedList(player1.deck), LinkedList(player2.deck))
        game.playRecursive()
        val result = game.score()
        return result.toString()
    }

    private val player1: Player
    private val player2: Player

    init {
        val players = input().split("\r\n\r\n")
        val player0parts = players[0].split("\r\n")
        val player1parts = players[1].split("\r\n")
        player1 = Player(LinkedList(player0parts.drop(1).map { it.toInt() }))
        player2 = Player(LinkedList(player1parts.drop(1).map { it.toInt() }))
    }

    data class Player(val deck: List<Int>)

    private class CombatGame(val player1Deck: LinkedList<Int>, val player2Deck: LinkedList<Int>) {

        private val playedRecursive = HashSet<String>()
        private var isInfinite = false

        fun playRegular() {
            do {
                val player1Card = player1Deck.pop()
                val player2Card = player2Deck.pop()

                if (player1Card > player2Card) {
                    player1Deck.add(player1Card)
                    player1Deck.add(player2Card)
                } else {
                    player2Deck.add(player2Card)
                    player2Deck.add(player1Card)
                }
            } while (player1Deck.isNotEmpty() && player2Deck.isNotEmpty())
        }

        fun playRecursive() {
            do {
                val gameId = player1Deck.joinToString(",") + "#" + player2Deck.joinToString(",")
                if (playedRecursive.contains(gameId)) {
                    isInfinite = true
                    return
                }

                playedRecursive.add(gameId)
                val player1Card = player1Deck.pop()
                val player2Card = player2Deck.pop()

                val playRecursive = player1Deck.size >= player1Card && player2Deck.size >= player2Card
                val player1Wins = if (playRecursive) {
                    val subGame = CombatGame(
                        LinkedList(player1Deck.take(player1Card)),
                        LinkedList(player2Deck.take(player2Card))
                    )
                    subGame.playRecursive()
                    subGame.player1Wins()
                } else {
                    player1Card > player2Card
                }

                if (player1Wins) {
                    player1Deck.add(player1Card)
                    player1Deck.add(player2Card)
                } else {
                    player2Deck.add(player2Card)
                    player2Deck.add(player1Card)
                }
            } while (player1Deck.isNotEmpty() && player2Deck.isNotEmpty())
        }

        fun player1Wins(): Boolean {
            if (isInfinite)
                return true
            if (player1Deck.isNotEmpty() && player2Deck.isNotEmpty())
                throw RuntimeException("Game is not over")
            return player1Deck.isNotEmpty() && player2Deck.isEmpty()
        }

        fun score() =
            player1Deck.ifEmpty { player2Deck }.reversed().mapIndexed { index, card -> card * (index + 1L) }.sum()
    }
}