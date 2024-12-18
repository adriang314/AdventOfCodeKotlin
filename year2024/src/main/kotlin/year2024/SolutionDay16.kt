package year2024

import common.BaseSolution
import common.Direction
import common.Point
import common.PointMap
import java.util.LinkedList

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16
    
    private val pointMap: PointMap<Paths, PointType>
    private lateinit var startPoint: Point<Paths, PointType>
    private lateinit var endPoint: Point<Paths, PointType>
    private var bestPaths = LinkedList<Path>()

    init {
        pointMap = PointMap(input(), ::PointType) { Paths() }
        pointMap.points.flatten().forEach {
            if (it.value.isStartPoint) startPoint = it
            if (it.value.isEndPoint) endPoint = it

            it.state.canGoUp = it.up?.value?.isSpace == true
            it.state.canGoDown = it.down?.value?.isSpace == true
            it.state.canGoLeft = it.left?.value?.isSpace == true
            it.state.canGoRight = it.right?.value?.isSpace == true
        }

        // remove dead ends
        var changed = true
        while (changed) {
            changed = false
            pointMap.points.flatten().forEach {
                var moves = 0
                if (it.state.canGoUp) moves++
                if (it.state.canGoDown) moves++
                if (it.state.canGoLeft) moves++
                if (it.state.canGoRight) moves++

                if (!it.value.isEndPoint && !it.value.isStartPoint && moves == 1) {
                    changed = true
                    it.value.name = '#'
                    it.state.canGoRight = false
                    it.state.canGoLeft = false
                    it.state.canGoDown = false
                    it.state.canGoUp = false
                }
            }
        }

        findBestPaths()
    }

    private fun findBestPaths() {
        val queue = LinkedList<Path>()
        queue.add(Path(startPoint, setOf(startPoint.id), 0L, Direction.Right))

        val bestScores = mutableMapOf<PointWithDirection, Long>()
        while (queue.isNotEmpty()) {
            val currPath = queue.pop()

            if (currPath.currentPoint === endPoint) {
                if (bestPaths.isEmpty())
                    bestPaths.add(currPath)
                else {
                    val bestScore = bestPaths.first.score
                    if (bestScore > currPath.score) {
                        bestPaths = LinkedList(listOf(currPath))
                    } else if (bestScore == currPath.score) {
                        bestPaths.add(currPath)
                    }
                }
            } else {
                currPath.currentPoint.neighbours()
                    .filter { neighbour -> !neighbour.value.isWall && !currPath.visitedPoints.contains(neighbour.id) }
                    .forEach { neighbour ->
                        val neighbourDirection =
                            when {
                                currPath.currentPoint.up === neighbour -> Direction.Up
                                currPath.currentPoint.down === neighbour -> Direction.Down
                                currPath.currentPoint.left === neighbour -> Direction.Left
                                currPath.currentPoint.right === neighbour -> Direction.Right
                                else -> throw RuntimeException("Unknown direction")
                            }

                        val newScore = currPath.score + (1000 * currPath.direction.turnsTo(neighbourDirection)) + 1

                        // new path worth to check
                        val pointWithDirection = PointWithDirection(neighbour.id, neighbourDirection)
                        if (!bestScores.containsKey(pointWithDirection) || bestScores[pointWithDirection]!! >= newScore) {
                            bestScores[pointWithDirection] = newScore
                            queue.add(
                                Path(neighbour, currPath.visitedPoints.plus(neighbour.id), newScore, neighbourDirection)
                            )
                        }
                    }
            }
        }
    }

    override fun task1(): String {
        val result = bestPaths.first().score
        return result.toString()
    }

    override fun task2(): String {
        val result = bestPaths.map { it.visitedPoints }.flatten().toSet().size
        return result.toString()
    }

    private class Path(
        val currentPoint: Point<Paths, PointType>,
        val visitedPoints: Set<String>,
        val score: Long,
        val direction: Direction
    )

    private data class PointWithDirection(
        val pointId: String,
        val direction: Direction
    )

    data class PointType(var name: Char) {
        val isStartPoint = name == 'S'
        val isEndPoint = name == 'E'
        val isWall = name == '#'
        val isSpace = !isWall
    }

    private class Paths {
        var canGoUp = false
        var canGoDown = false
        var canGoLeft = false
        var canGoRight = false
    }
}