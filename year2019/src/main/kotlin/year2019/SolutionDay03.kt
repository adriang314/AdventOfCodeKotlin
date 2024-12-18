package year2019

import common.BaseSolution
import kotlin.math.absoluteValue

fun main() = println(SolutionDay03().result())

class SolutionDay03 : BaseSolution() {

    override val day = 3
    
    private val pathSteps = input().split("\r\n").map { it.split(",") }
    private val path1Steps = pathSteps[0]
    private val path2Steps = pathSteps[1]

    private val path1: Path
    private val path2: Path
    private val intersection: Set<Position>

    init {
        path1 = Path()
        path1Steps.forEach { path1.move(it) }

        path2 = Path()
        path2Steps.forEach { path2.move(it) }

        intersection = path1.intersect(path2)
    }

    override fun task1(): String {
        val best = intersection.minOfOrNull { it.colIdx.absoluteValue + it.rowIdx.absoluteValue }
        return best.toString()
    }

    override fun task2(): String {
        val best = intersection.minOfOrNull { path1.steps(it)!! + path2.steps(it)!! }
        return best.toString()
    }

    class Path {
        private val positions: MutableMap<Position, Int> = mutableMapOf()
        private var currentPosition = Position(0, 0)

        fun steps(position: Position) = positions[position]

        fun intersect(other: Path) = this.positions.keys.intersect(other.positions.keys)

        fun move(path: String) {
            val direction = path[0]
            val steps = path.substring(1).toInt()

            when (direction) {
                'D' -> (1..steps).map {
                    val newPosition = Position(currentPosition.rowIdx - it, currentPosition.colIdx).apply {
                        this.steps = currentPosition.steps + it
                    }
                    positions.computeIfAbsent(newPosition) { _ -> newPosition.steps }
                    if (it == steps)
                        currentPosition = newPosition
                }

                'U' -> (1..steps).map {
                    val newPosition = Position(currentPosition.rowIdx + it, currentPosition.colIdx).apply {
                        this.steps = currentPosition.steps + it
                    }
                    positions.computeIfAbsent(newPosition) { _ -> newPosition.steps }
                    if (it == steps)
                        currentPosition = newPosition
                }

                'L' -> (1..steps).map {
                    val newPosition = Position(currentPosition.rowIdx, currentPosition.colIdx - it).apply {
                        this.steps = currentPosition.steps + it
                    }
                    positions.computeIfAbsent(newPosition) { _ -> newPosition.steps }
                    if (it == steps)
                        currentPosition = newPosition
                }

                'R' -> (1..steps).map {
                    val newPosition = Position(currentPosition.rowIdx, currentPosition.colIdx + it).apply {
                        this.steps = currentPosition.steps + it
                    }
                    positions.computeIfAbsent(newPosition) { _ -> newPosition.steps }
                    if (it == steps)
                        currentPosition = newPosition
                }
            }
        }

    }

    data class Position(val rowIdx: Int, val colIdx: Int) {
        var steps: Int = 0
    }
}