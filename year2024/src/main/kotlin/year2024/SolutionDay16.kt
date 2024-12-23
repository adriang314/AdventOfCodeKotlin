package year2024

import common.*
import java.util.LinkedList

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16

    private val map = Grid(input()) { c, position -> Place(position, c) }
        .also { grid ->
            grid.cells.values.forEach {
                if (it.isStartPoint()) startPlace = it
                if (it.isEndPoint()) endPlace = it

                it.canGoN = grid.getCell(it.position.n())?.isSpace() == true
                it.canGoS = grid.getCell(it.position.s())?.isSpace() == true
                it.canGoW = grid.getCell(it.position.w())?.isSpace() == true
                it.canGoE = grid.getCell(it.position.e())?.isSpace() == true
            }
        }

    private lateinit var startPlace: Place
    private lateinit var endPlace: Place
    private var bestPaths = LinkedList<Path>()

    init {
        findBestPaths()
    }

    private fun findBestPaths() {
        val pathsToCheck = LinkedList<Path>()
        pathsToCheck.add(Path(startPlace, setOf(startPlace.position), 0L, Direction.Right))

        val bestScores = mutableMapOf<DirectedPosition, Long>()
        while (pathsToCheck.isNotEmpty()) {
            val currPath = pathsToCheck.pop()

            if (currPath.currentPlace === endPlace) {
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
                currPath.currentPlace.neighbours()
                    .map { map.getCell(it.position)!! }
                    .filter { neighbour -> !neighbour.isWall() && !currPath.visitedPositions.contains(neighbour.position) }
                    .forEach { neighbour ->
                        val neighbourDirection = currPath.currentPlace.findNeighbourDirection(neighbour)
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
        val currentPlace: Place,
        val visitedPositions: Set<Position>,
        val score: Long,
        val direction: Direction
    )

    private class Place(position: Position, c: Char) : Cell(position, c) {
        fun isStartPoint() = value == 'S'
        fun isEndPoint() = value == 'E'
        fun isWall() = value == '#'
        fun isSpace() = !isWall()
    }
}