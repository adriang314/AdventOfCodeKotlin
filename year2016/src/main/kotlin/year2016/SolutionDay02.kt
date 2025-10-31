package year2016

import common.BaseSolution
import common.Direction
import common.Position

fun main() = println(SolutionDay02().result())

class SolutionDay02 : BaseSolution() {

    override val day = 2

    private val commands = input().split("\r\n").map { line ->
        line.map {
            when (it) {
                'U' -> Direction.N
                'D' -> Direction.S
                'L' -> Direction.W
                'R' -> Direction.E
                else -> throw IllegalArgumentException("Unknown direction: $it")
            }
        }
    }

    override fun task1(): String {
        return StandardKeyboard().execute(commands)
    }

    override fun task2(): String {
        return ComplexKeyboard().execute(commands)
    }

    private interface Keyboard {
        val buttons: List<Button>
        var currentButton: Button

        fun execute(commands: List<List<Direction>>): String {
            currentButton = buttons.first { it.key == '5' }
            val buttonsToPress = commands.map { directions ->
                directions.forEach { direction ->
                    val newPosition = currentButton.position.next(direction)
                    buttons.firstOrNull { it.position == newPosition }?.let { currentButton = it }
                }
                currentButton.key
            }
            return buttonsToPress.fold("", String::plus)
        }
    }

    private class StandardKeyboard() : Keyboard {
        // 1 2 3
        // 4 5 6
        // 7 8 9
        override val buttons = listOf(
            Button('1', Position(0, 0)),
            Button('2', Position(1, 0)),
            Button('3', Position(2, 0)),
            Button('4', Position(0, 1)),
            Button('5', Position(1, 1)),
            Button('6', Position(2, 1)),
            Button('7', Position(0, 2)),
            Button('8', Position(1, 2)),
            Button('9', Position(2, 2)),
        )

        override lateinit var currentButton: Button
    }

    private class ComplexKeyboard() : Keyboard {
        //     1
        //   2 3 4
        // 5 6 7 8 9
        //   A B C
        //     D
        override val buttons = listOf(
            Button('1', Position(2, 0)),
            Button('2', Position(1, 1)),
            Button('3', Position(2, 1)),
            Button('4', Position(3, 1)),
            Button('5', Position(0, 2)),
            Button('6', Position(1, 2)),
            Button('7', Position(2, 2)),
            Button('8', Position(3, 2)),
            Button('9', Position(4, 2)),
            Button('A', Position(1, 3)),
            Button('B', Position(2, 3)),
            Button('C', Position(3, 3)),
            Button('D', Position(2, 4)),
        )

        override lateinit var currentButton: Button
    }

    private data class Button(var key: Char, val position: Position)
}