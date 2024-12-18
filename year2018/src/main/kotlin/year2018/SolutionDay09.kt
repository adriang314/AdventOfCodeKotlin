package year2018

import common.BaseSolution
import common.LinkedListWithCache

fun main() = println(SolutionDay09().result())

class SolutionDay09 : BaseSolution() {

    override val day = 9
    
    private val gameConfigRegex = Regex("^(\\d+) players; last marble is worth (\\d+) points$")
    private val gameConfig1 = input().let {
        val (players, marbles) = gameConfigRegex.find(it)!!.destructured
        GameConfig(
            (1..players.toInt()).map { id -> Player(id) },
            (1..marbles.toInt()).map { id -> Marble(id) })
    }

    private val gameConfig2 = input().let {
        val (players, marbles) = gameConfigRegex.find(it)!!.destructured
        GameConfig(
            (1..players.toInt()).map { id -> Player(id) },
            (1..marbles.toInt() * 100).map { id -> Marble(id) })
    }

    override fun task1(): String {
        return playGame(gameConfig1).toString()
    }

    override fun task2(): String {
        return playGame(gameConfig2).toString()
    }

    private fun playGame(config: GameConfig): Long {
        Game(config).play()
        return config.players.maxOf { it.score }
    }

    private data class Game(val config: GameConfig) {
        private val state = LinkedListWithCache<Marble>()
        private val marble0 = Marble(0)
        private var currentMarble = marble0

        init {
            state.add(marble0)
        }

        fun play() {
            for (turn in 1..config.marbles.size) {
                val marble = pickMarble(turn)
                val player = pickPlayer(turn)

                if (marble.isSpecial) {
                    collectPoints(marble, player)
                } else {
                    addMarble(marble)
                }
            }
        }

        private fun collectPoints(marble: Marble, player: Player) {
            player.score += marble.value
            val marbleToRemove = state.getBefore(currentMarble, 7)
            currentMarble = state.getAfter(marbleToRemove, 1)
            state.remove(marbleToRemove)
            player.score += marbleToRemove.value
        }

        private fun addMarble(marble: Marble) {
            if (state.size() == 1) {
                state.add(marble)
            } else {
                state.addBefore(marble, state.getAfter(currentMarble, 2))
            }
            currentMarble = marble
        }

        private fun pickPlayer(turn: Int) = config.players[(turn - 1) % config.players.size]

        private fun pickMarble(turn: Int) = config.marbles[turn - 1]
    }

    private data class Marble(val value: Int) {
        val isSpecial = value > 0 && value % 23 == 0
        override fun toString() = "[$value]"
    }

    private data class Player(val id: Int, var score: Long = 0)

    private data class GameConfig(val players: List<Player>, val marbles: List<Marble>)
}