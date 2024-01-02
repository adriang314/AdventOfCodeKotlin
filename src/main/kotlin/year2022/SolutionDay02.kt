package year2022

import common.BaseSolution

fun main() = println(SolutionDay02().result())

class SolutionDay02 : BaseSolution() {
    override val day = 2
    override val year = 2022

    override fun task1(): String {
        val games = input().split("\r\n").map {
            Game(Choice.from(it[2]), Choice.from(it[0]))
        }
        val totalScore = games.sumOf { it.score }
        return totalScore.toString()
    }

    override fun task2(): String {
        val games = input().split("\r\n").map {
            Game(Choice.from(Choice.from(it[0]), Result.from(it[2])), Choice.from(it[0]))
        }
        val totalScore = games.sumOf { it.score }
        return totalScore.toString()
    }

    data class Game(private val you: Choice, private val opponent: Choice) {
        private val result: Result = when (you) {
            opponent -> Result.Draw
            Choice.Rock -> if (opponent == Choice.Paper) Result.Lost else Result.Won
            Choice.Paper -> if (opponent == Choice.Scissors) Result.Lost else Result.Won
            Choice.Scissors -> if (opponent == Choice.Rock) Result.Lost else Result.Won
        }

        val score: Int = result.points + you.points
    }

    enum class Result(val points: Int) {
        Won(6), Draw(3), Lost(0);

        companion object {
            fun from(c: Char): Result {
                return when (c) {
                    'X' -> Lost
                    'Y' -> Draw
                    'Z' -> Won
                    else -> throw Exception("unknown")
                }
            }
        }
    }

    enum class Choice(val points: Int) {
        Rock(1), Paper(2), Scissors(3);

        companion object {
            fun from(opponent: Choice, expectedResult: Result) = when (expectedResult) {
                Result.Draw -> opponent
                Result.Lost -> {
                    when (opponent) {
                        Rock -> Scissors
                        Paper -> Rock
                        else -> Paper
                    }
                }

                else -> {
                    when (opponent) {
                        Rock -> Paper
                        Paper -> Scissors
                        else -> Rock
                    }
                }
            }

            fun from(c: Char) = when (c) {
                'A', 'X' -> Rock
                'B', 'Y' -> Paper
                'C', 'Z' -> Scissors
                else -> throw Exception("unknown")
            }
        }
    }
}
