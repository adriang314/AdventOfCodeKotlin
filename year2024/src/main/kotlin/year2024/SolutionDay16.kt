package year2024

import common.*
import java.util.LinkedList

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16

    private val map = Grid(input()) { c, position -> Point(position, c) }
    private val startPoint = map.cells.single { it.isStartPoint() }
    private val endPoint = map.cells.single { it.isEndPoint() }
    private var bestPaths = LinkedList<Path>()

    init {
        findBestPaths()
    }

    private fun findBestPaths() {
        val pathsToCheck = LinkedList<Path>()
        pathsToCheck.add(Path(startPoint, setOf(startPoint.position), 0L, Direction.E))

        val bestScores = mutableMapOf<DirectedPosition, Long>()
        while (pathsToCheck.isNotEmpty()) {
            val currPath = pathsToCheck.pop()

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
                    .map { map.getCell(it.position)!! }
                    .filter { neighbour -> !neighbour.isWall() && !currPath.visitedPositions.contains(neighbour.position) }
                    .forEach { neighbour ->
                        val neighbourDirection = currPath.currentPoint.findNeighbourDirection(neighbour)
                        val newScore = currPath.score + (1000 * currPath.direction.turnsTo(neighbourDirection)) + 1

                        // new path worth to check
                        val directedPosition = DirectedPosition(neighbourDirection, neighbour.position)
                        if (!bestScores.containsKey(directedPosition) || bestScores[directedPosition]!! >= newScore) {
                            bestScores[directedPosition] = newScore
                            pathsToCheck.add(
                                Path(
                                    neighbour,
                                    currPath.visitedPositions.plus(neighbour.position),
                                    newScore,
                                    neighbourDirection
                                )
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
        val result = bestPaths.map { it.visitedPositions }.flatten().toSet().size
        return result.toString()
    }

    private class Path(
        val currentPoint: Point,
        val visitedPositions: Set<Position>,
        val score: Long,
        val direction: Direction
    )

    private class Point(position: Position, c: Char) : Cell<Point>(position, c) {
        fun isStartPoint() = value == 'S'
        fun isEndPoint() = value == 'E'
        fun isWall() = value == '#'
        fun isSpace() = !isWall()

        override fun canGoN() = n?.isSpace() == true
        override fun canGoS() = s?.isSpace() == true
        override fun canGoW() = w?.isSpace() == true
        override fun canGoE() = e?.isSpace() == true
    }
}