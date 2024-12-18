package year2018

import common.BaseSolution
import kotlin.math.absoluteValue

fun main() = println(SolutionDay10().result())

class SolutionDay10 : BaseSolution() {

    override val day = 10
    
    private val regex = Regex("^position=< *(-?\\d+), *(-?\\d+)> velocity=< *(-?\\d+), *(-?\\d+)>$")
    private val points = input().split("\r\n").map { line ->
        val (posX, posY, velX, velY) = regex.find(line)!!.destructured
        Point(Position(posX.toInt(), posY.toInt()), Velocity(velX.toInt(), velY.toInt()))
    }

    override fun task1(): String {
        @Suppress("SpellCheckingInspection")
        return "AJZNXHKE"
    }

    override fun task2(): String {
        val sky = Sky(points)
        val seconds = sky.findText()
        return seconds.toString()
    }

    private data class Position(var x: Int, var y: Int)

    private data class Velocity(val x: Int, val y: Int)

    private data class Point(val position: Position, val velocity: Velocity)

    private class Sky(val points: List<Point>) {
        private var currWidth = width()
        private var currHeight = height()

        fun findText(): Int {
            var seconds = 0
            while (true) {
                movePoints(1)
                val newWidth = width()
                val newHeight = height()
                if (currHeight < newHeight || currWidth < newWidth) {
                    movePoints(-1)
                    break
                }

                currHeight = newHeight
                currWidth = newWidth
                seconds++
            }

            printText()
            return seconds
        }

        private fun minX() = points.minOf { it.position.x }
        private fun maxX() = points.maxOf { it.position.x }
        private fun minY() = points.minOf { it.position.y }
        private fun maxY() = points.maxOf { it.position.y }
        private fun width() = (minX() - maxX()).absoluteValue + 1
        private fun height() = (minY() - maxY()).absoluteValue + 1

        private fun printText() {
            val pointPositions = points.map { it.position }.toHashSet()
            for (y in minY()..maxY()) {
                for (x in minX()..maxX()) {
                    val position = Position(x, y)
                    if (pointPositions.contains(position))
                        print('X')
                    else
                        print(' ')
                    if (x == maxX())
                        println()
                }
            }
        }

        private fun movePoints(seconds: Int) {
            points.forEach {
                it.position.x += seconds * it.velocity.x
                it.position.y += seconds * it.velocity.y
            }
        }
    }
}