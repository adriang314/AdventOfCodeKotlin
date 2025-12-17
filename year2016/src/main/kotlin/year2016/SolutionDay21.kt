package year2016

import common.BaseSolution
import common.allPermutations
import common.move
import common.reverse
import common.shiftLeft
import common.shiftRight
import common.swapAtIndex
import common.swapFirstLetters

fun main() = println(SolutionDay21().result())

class SolutionDay21 : BaseSolution() {

    override val day = 21

    private val commands = input().split("\r\n")
    private val scramblingFunction = ScramblingFunction(commands)

    override fun task1(): String {
        val result = scramblingFunction.execute("abcdefgh")
        return result
    }

    override fun task2(): String {
        val allInputs = "abcdefgh".toSet().allPermutations().map { it.joinToString("") }
        val result = allInputs.first { scramblingFunction.execute(it) == "fbgdceah" }
        return result
    }

    private class ScramblingFunction(private val commands: List<String>) {

        fun execute(input: String): String {
            var commandInput = input
            commands.forEach { command ->
                commandInput = when {
                    command.startsWith("swap position") -> SwapPosition.execute(commandInput, command)
                    command.startsWith("swap letter ") -> SwapLetter.execute(commandInput, command)
                    command.startsWith("rotate left") -> Rotate.execute(commandInput, command)
                    command.startsWith("rotate right") -> Rotate.execute(commandInput, command)
                    command.startsWith("rotate based") -> RotateBasedOnPosition.execute(commandInput, command)
                    command.startsWith("reverse positions ") -> Reverse.execute(commandInput, command)
                    command.startsWith("move position") -> Move.execute(commandInput, command)
                    else -> throw IllegalStateException("Unknown command")
                }
            }

            return commandInput
        }
    }

    object SwapPosition {
        private val regex = """swap position (\d+) with position (\d+)""".toRegex()

        fun execute(input: String, command: String): String {
            val (idx1, idx2) = regex.find(command)!!.destructured
            return input.swapAtIndex(idx1.toInt(), idx2.toInt())
        }
    }

    object SwapLetter {
        private val regex = """swap letter (\w) with letter (\w)""".toRegex()

        fun execute(input: String, command: String): String {
            val (ch1, ch2) = regex.find(command)!!.destructured
            return input.swapFirstLetters(ch1[0], ch2[0])
        }
    }

    object Rotate {
        private val regex = """rotate (left|right) (\d+) step""".toRegex()

        fun execute(input: String, command: String): String {
            val (direction, offset) = regex.find(command)!!.destructured
            return when (direction) {
                "left" -> input.shiftLeft(offset.toInt())
                "right" -> input.shiftRight(offset.toInt())
                else -> throw IllegalStateException("Unknown direction")
            }
        }
    }

    object RotateBasedOnPosition {
        private val regex = """rotate based on position of letter (\w)""".toRegex()

        fun execute(input: String, command: String): String {
            val (ch) = regex.find(command)!!.destructured
            val chIdx = input.indexOf(ch[0])
            return input.shiftRight(1 + chIdx + if (chIdx >= 4) 1 else 0)
        }
    }

    object Reverse {
        private val regex = """reverse positions (\d+) through (\d+)""".toRegex()

        fun execute(input: String, command: String): String {
            val (x, y) = regex.find(command)!!.destructured
            return input.reverse(x.toInt(), y.toInt())
        }
    }

    object Move {
        private val regex = """move position (\d+) to position (\d+)""".toRegex()

        fun execute(input: String, command: String): String {
            val (x, y) = regex.find(command)!!.destructured
            return input.move(x.toInt(), y.toInt())
        }
    }
}