package year2016

import common.*
import java.util.*

fun main() = println(SolutionDay17().result())

class SolutionDay17 : BaseSolution() {

    override val day = 17

    override fun task1(): String {
        val path = PathFinder().findShortestPath()!!
        val result = path.displayName()
        return result
    }

    override fun task2(): String {
        val path = PathFinder().findLongestPath()!!
        val result = path.size().toString()
        return result
    }

    private inner class PathFinder() {
        private val path = Path(input(), emptyList())
        private val areaBuilder = Grid.Builder(0..<4, 0..<4) { _ -> ' ' }
        private val area = Grid(areaBuilder) { char, position -> Point(position, char) }
        private val startCell = area.getCell(Position(0, 0))!!
        private val endCell = area.getCell(Position(3, 3))!!

        fun findShortestPath(): Path? {
            val queue = LinkedList(listOf(Pair(startCell, path)))
            while (queue.isNotEmpty()) {
                val (currentCell, currentPath) = queue.poll()
                if (currentCell == endCell) {
                    return currentPath
                }

                currentCell.neighbours().forEach { evaluateNextStep(currentCell, currentPath, it, queue) }
            }

            return null
        }

        fun findLongestPath(): Path? {
            val queue = LinkedList(listOf(Pair(startCell, path)))
            var longestPath: Path? = null
            while (queue.isNotEmpty()) {
                val (currentCell, currentPath) = queue.poll()
                if (currentCell == endCell) {
                    if (longestPath == null || currentPath.size() > longestPath.size()) {
                        longestPath = currentPath
                    }
                    continue
                }

                currentCell.neighbours().forEach { evaluateNextStep(currentCell, currentPath, it, queue) }
            }

            return longestPath
        }

        private fun evaluateNextStep(currentCell: Point, currentPath: Path, neighbor: Point, queue: LinkedList<Pair<Point, Path>>) {
            if (neighbor === currentCell.n && currentPath.doorStatus.northDoorIsOpen) {
                queue.add(Pair(neighbor, currentPath.withNextStep(Direction.N)))
            } else if (neighbor === currentCell.s && currentPath.doorStatus.southDoorIsOpen) {
                queue.add(Pair(neighbor, currentPath.withNextStep(Direction.S)))
            } else if (neighbor === currentCell.w && currentPath.doorStatus.westDoorIsOpen) {
                queue.add(Pair(neighbor, currentPath.withNextStep(Direction.W)))
            } else if (neighbor === currentCell.e && currentPath.doorStatus.eastDoorIsOpen) {
                queue.add(Pair(neighbor, currentPath.withNextStep(Direction.E)))
            }
        }
    }

    private class Point(position: Position, c: Char) : Cell<Point>(position, c)

    private data class Path(private val passcode: String, private val steps: List<Direction>) {
        val doorStatus = run {
            val toHash = passcode + steps.joinToString("") { directionToName(it) }
            val hash = Hash.md5(toHash).substring(0, 4)
            DoorStatus(
                northDoorIsOpen = hash[0] in 'b'..'f',
                southDoorIsOpen = hash[1] in 'b'..'f',
                westDoorIsOpen = hash[2] in 'b'..'f',
                eastDoorIsOpen = hash[3] in 'b'..'f'
            )
        }

        fun size() = steps.size

        fun withNextStep(direction: Direction): Path = this.copy(steps = steps + direction)

        fun displayName() = steps.joinToString("") { directionToName(it) }

        private fun directionToName(direction: Direction): String = when (direction) {
            Direction.N -> "U"
            Direction.S -> "D"
            Direction.W -> "L"
            Direction.E -> "R"
        }
    }

    private data class DoorStatus(val northDoorIsOpen: Boolean, val southDoorIsOpen: Boolean, val westDoorIsOpen: Boolean, val eastDoorIsOpen: Boolean)
}