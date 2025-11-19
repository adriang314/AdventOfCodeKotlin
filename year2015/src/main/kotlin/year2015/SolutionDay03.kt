package year2015

import common.BaseSolution
import common.Direction
import common.Position

fun main() = println(SolutionDay03().result())

class SolutionDay03 : BaseSolution() {

    override val day = 3

    private val path = Path(input().map { Direction.from(it) })

    override fun task1(): String {
        return path.visitedHousesBySanta().toString()
    }

    override fun task2(): String {
        return path.visitedHousesBySantaAndRoboSanta().toString()
    }

    private class Path(private val directions: List<Direction>) {
        private val startPosition = Position(0, 0)

        fun visitedHousesBySanta(): Int {
            return visitedHouses(directions).count()
        }

        fun visitedHousesBySantaAndRoboSanta(): Int {
            val santaDirections = directions.filterIndexed { index, _ -> index % 2 == 0 }
            val roboSantaDirections = directions.filterIndexed { index, _ -> index % 2 == 1 }
            return visitedHouses(santaDirections).plus(visitedHouses(roboSantaDirections)).count()
        }

        private fun visitedHouses(directions: List<Direction>): Set<Position> {
            val visitedPositions = mutableMapOf(startPosition to 1)
            var currentPosition = startPosition

            directions.forEach {
                currentPosition = currentPosition.next(it)
                visitedPositions.compute(currentPosition) { _, visited -> if (visited == null) 1 else visited + 1 }
            }
            return visitedPositions.keys
        }
    }
}