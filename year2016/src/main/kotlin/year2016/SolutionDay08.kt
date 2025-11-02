package year2016

import common.BaseSolution
import common.Cell
import common.Grid
import common.Position

fun main() = println(SolutionDay08().result())

class SolutionDay08 : BaseSolution() {

    override val day = 8

    private val commands = input().split("\r\n").map { Command.from(it) }
    private val screenBuilder = Grid.Builder(0..<50, 0..<6) { _ -> '.' }
    private val screen = Grid(screenBuilder) { c, position -> Point(position, c) }

    init {
        commands.forEach { command ->
            when (command.type) {
                Command.Type.RECT -> screen.fill(0..<command.index, 0..<command.shift, '#')
                Command.Type.ROTATE_ROW -> screen.shiftRow(command.index, command.shift)
                Command.Type.ROTATE_COLUMN -> screen.shiftColumn(command.index, command.shift)
            }
        }
    }

    override fun task1(): String {
        val result = screen.cells.count { it.value == '#' }
        return result.toString()
    }

    override fun task2(): String {
        screen.print()
        return "CFLELOYFCS"
    }

    private class Point(position: Position, c: Char) : Cell<Point>(position, c)

    private data class Command(val type: Type, val index: Int, val shift: Int) {
         enum class Type {
            RECT, ROTATE_ROW, ROTATE_COLUMN
        }

        companion object {
            private val regexRect = Regex("""rect (\d+)x(\d+)""")
            private val regexRotateRow = Regex("""rotate row y=(\d+) by (\d+)""")
            private val regexRotateColumn = Regex("""rotate column x=(\d+) by (\d+)""")

            fun from(line: String): Command {
                if (regexRect.matches(line)) {
                    val (width, height) = regexRect.find(line)!!.destructured
                    return Command(Type.RECT, width.toInt(), height.toInt())
                } else if (regexRotateRow.matches(line)) {
                    val (index, shift) = regexRotateRow.find(line)!!.destructured
                    return Command(Type.ROTATE_ROW, index.toInt(), shift.toInt())
                } else if (regexRotateColumn.matches(line)) {
                    val (index, shift) = regexRotateColumn.find(line)!!.destructured
                    return Command(Type.ROTATE_COLUMN, index.toInt(), shift.toInt())
                } else {
                    throw IllegalArgumentException("Invalid command: $line")
                }
            }
        }
    }
}