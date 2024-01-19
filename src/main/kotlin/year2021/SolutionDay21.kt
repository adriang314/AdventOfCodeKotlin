package year2021

import common.BaseSolution

fun main() = println(SolutionDay21().result())

class SolutionDay21 : BaseSolution() {

    override val day = 21
    override val year = 2021

    override fun task1(): String {
        val dice = Dice(100)
        val players = getPlayers()
        var gameOver = false

        while (!gameOver) {
            for (i in players.indices) {
                val player = players[i]
                player.score = player.score.update(dice.roll3Times())
                if (player.score.points >= 1000) {
                    gameOver = true
                    break
                }
            }
        }

        val result = players.first { it.score.points < 1000 }.score.points * dice.rolls
        return result.toString()
    }

    override fun task2(): String {
        val players = getPlayers()
        val player1GroupedScores = playerWinningScores(players[0])
            .groupBy { it.iteration }.map { grp -> grp.key to grp.value.sumOf { it.weight } }

        var totalWins = 0L
        player1GroupedScores.forEach { grp ->
            val iteration = grp.first
            val weight = grp.second
            val player2Looses = playerLoosingScores(players[1], iteration - 1)
            if (player2Looses.isEmpty())
                totalWins += weight
            else {
                player2Looses.forEach { score2 -> totalWins += weight * score2.weight }
            }
        }

        return totalWins.toString()
    }

    private fun playerWinningScores(player: Player): List<Score> {
        val dice = DiracDice()
        val result = dice.roll()
        var currScores = listOf(player.score)

        while (true) {
            val winningScores = currScores.filter { it.isFinal() }
            val notWinningScores = currScores.filter { !it.isFinal() }
            val newScores = notWinningScores.map { score ->
                result.map { result -> score.update(result.value, result.weight) }
            }.flatten().plus(winningScores)

            currScores = normalizeScores(newScores)
            if (currScores.all { it.isFinal() })
                break
        }

        return currScores
    }

    private fun playerLoosingScores(player: Player, iteration: Int): List<Score> {
        val dice = DiracDice()
        val result = dice.roll()
        var currScores = listOf(player.score)

        for (i in 0..iteration) {
            val newScores = currScores.map { score ->
                result.map { result -> score.update(result.value, result.weight) }
            }.flatten().filter { !it.isFinal() }

            currScores = normalizeScores(newScores)

            if (currScores.all { it.iteration == iteration })
                break
        }

        return currScores.filter { !it.isFinal() }
    }

    private fun normalizeScores(scores: List<Score>): List<Score> {
        return scores.groupBy { Triple(it.position, it.points, it.iteration) }
            .map { grp -> Score(grp.key.first, grp.key.second, grp.key.third, grp.value.sumOf { it.weight }) }
    }

    private fun getPlayers(): List<Player> {
        val regex = Regex("Player (\\d+) starting position: (\\d+)")
        return input().split("\r\n").map {
            val (id, position) = regex.find(it)!!.destructured
            Player(id.toInt(), Score(position.toInt(), 0, 0, 1))
        }
    }

    data class Player(val id: Int, var score: Score)

    data class Score(val position: Int, val points: Int, val iteration: Int, val weight: Long) {

        fun isFinal() = points >= 21

        fun update(positionMoveBy: Int, weight: Int = 1): Score {
            var newPosition = (position + positionMoveBy) % 10
            if (newPosition == 0)
                newPosition = 10
            return Score(newPosition, points + newPosition, this.iteration + 1, this.weight * weight)
        }
    }

    class DiracDice {
        private val result = listOf(
            Result(3, 1),
            Result(4, 3),
            Result(5, 6),
            Result(6, 7),
            Result(7, 6),
            Result(8, 3),
            Result(9, 1),
        )

        fun roll() = result

        data class Result(val value: Int, val weight: Int = 1)
    }

    data class Dice(val sideCount: Int, var currentSideIdx: Int = 0) {
        private val sides = (1..sideCount).map { it }
        var rolls = 0

        fun roll3Times() = (1..3).sumOf { roll() }

        private fun roll(): Int {
            rolls++
            currentSideIdx %= sideCount
            return sides[currentSideIdx++]
        }
    }
}
