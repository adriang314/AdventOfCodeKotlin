package year2015

import common.BaseSolution
import common.Cell
import common.Grid
import common.Position

fun main() = println(SolutionDay06().result())

class SolutionDay06 : BaseSolution() {

    override val day = 6

    private val instructions = input().split("\r\n").map { Instruction.from(it) }

    override fun task1(): String {
        val map = LightOnOffMap()
        map.execute(instructions)
        return map.howManyLightsAreLit().toString()
    }

    override fun task2(): String {
        val map = BrightnessMap()
        map.execute(instructions)
        return map.totalBrightness().toString()
    }

    private class Point(position: Position, c: Char, var brightness: Long = 0) : Cell<Point>(position, c)

    private class LightOnOffMap {
        private val lightOn = '*'
        private val lightOff = ' '
        private val areaBuilder = Grid.Builder(0..<1000, 0..<1000) { _ -> lightOff }
        private val area = Grid(areaBuilder) { c, position -> Point(position, c) }

        fun howManyLightsAreLit() = area.cells.count { it.value == lightOn }

        fun execute(instructions: List<Instruction>) {
            instructions.forEach { instruction ->
                when (instruction.type) {
                    Instruction.Type.TurnOn -> area.fill(instruction.xRange, instruction.yRange, lightOn)
                    Instruction.Type.TurnOff -> area.fill(instruction.xRange, instruction.yRange, lightOff)
                    Instruction.Type.Toggle -> area.fill(instruction.xRange, instruction.yRange) { light ->
                        when (light) {
                            lightOn -> lightOff
                            lightOff -> lightOn
                            else -> throw IllegalStateException("Unknown light status")
                        }
                    }
                }
            }
        }
    }

    private class BrightnessMap {
        private val areaBuilder = Grid.Builder(0..<1000, 0..<1000) { _ -> '#' }
        private val area = Grid(areaBuilder) { c, position -> Point(position, c) }

        fun totalBrightness() = area.cells.sumOf { it.brightness }

        fun execute(instructions: List<Instruction>) {
            instructions.forEach { instruction ->
                when (instruction.type) {
                    Instruction.Type.TurnOn -> area.cells.filter { it.position.within(instruction.xRange, instruction.yRange) }.forEach { it.brightness++ }
                    Instruction.Type.TurnOff -> area.cells.filter { it.position.within(instruction.xRange, instruction.yRange) }.forEach { if (it.brightness > 0) it.brightness-- }
                    Instruction.Type.Toggle -> area.cells.filter { it.position.within(instruction.xRange, instruction.yRange) }.forEach { it.brightness += 2 }
                }
            }
        }
    }

    private class Instruction(val type: Type, topLeft: Position, bottomRight: Position) {
        val xRange = IntRange(topLeft.x, bottomRight.x)
        val yRange = IntRange(topLeft.y, bottomRight.y)

        enum class Type {
            TurnOn, TurnOff, Toggle;

            companion object {
                fun from(text: String): Type {
                    return when (text) {
                        "turn on" -> TurnOn
                        "turn off" -> TurnOff
                        "toggle" -> Toggle
                        else -> throw IllegalArgumentException("Unknown type")
                    }
                }
            }
        }

        companion object {
            private val regex = """(turn on|toggle|turn off) (\d+),(\d+) through (\d+),(\d+)""".toRegex()
            fun from(input: String): Instruction {
                val (type, x1, y1, x2, y2) = regex.matchEntire(input)!!.destructured
                return Instruction(Type.from(type), Position(x1.toInt(), y1.toInt()), Position(x2.toInt(), y2.toInt()))
            }
        }
    }
}