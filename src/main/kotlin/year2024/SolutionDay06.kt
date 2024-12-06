package year2024

import common.BaseSolution

fun main() = println(SolutionDay06().result())

class SolutionDay06 : BaseSolution() {

    override val day = 6
    override val year = 2024

    private lateinit var initGuardLocation: GuardLocation
    private val laboratoryMap = input().split("\r\n").mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            val position = Position(x, y)
            val type = if (c == '#') PositionType.Obstacle else PositionType.Normal
            when (c) {
                '^' -> initGuardLocation = GuardLocation(position, Direction.Up)
                'v' -> initGuardLocation = GuardLocation(position, Direction.Down)
                '>' -> initGuardLocation = GuardLocation(position, Direction.Right)
                '<' -> initGuardLocation = GuardLocation(position, Direction.Left)
            }
            position to type
        }
    }.flatten().toMap()

    override fun task1(): String {
        val result = Laboratory(initGuardLocation, laboratoryMap).patrol()
        return result.toString()
    }

    override fun task2(): String {
        val result = Laboratory(initGuardLocation, laboratoryMap).findObstructions()
        return result.toString()
    }

    private class Laboratory(
        private val initGuardLocation: GuardLocation,
        private val map: Map<Position, PositionType>
    ) {
        fun findObstructions(): Int {
            val guardLocation = initGuardLocation.copy()
            val obstructionPositions = mutableSetOf<Position>()

            while (true) {
                val nextPosition = guardLocation.nextPosition()
                if (!map.containsKey(nextPosition)) {
                    return obstructionPositions.size
                } else if (map[nextPosition]!! == PositionType.Obstacle) {
                    guardLocation.turn90()
                } else {
                    if (map.containsKey(nextPosition) && !obstructionPositions.contains(nextPosition)) {
                        val newMap = map.toMutableMap()
                        newMap[nextPosition] = PositionType.Obstacle

                        if (!checkIfPatrolFinishes(newMap))
                            obstructionPositions.add(nextPosition)
                    }
                    guardLocation.moveToNextPosition()
                }
            }
        }

        fun patrol(): Int {
            val visitedPositions = mutableMapOf<Position, MutableSet<Direction>>()
            val guardLocation = initGuardLocation.copy()
            visitedPositions[guardLocation.position] = mutableSetOf(guardLocation.direction)

            while (true) {
                val nextPosition = guardLocation.nextPosition()
                if (!map.containsKey(nextPosition)) {
                    return visitedPositions.size
                } else if (map[nextPosition]!! == PositionType.Obstacle) {
                    guardLocation.turn90()
                } else {
                    visitedPositions.compute(nextPosition) { _, directions ->
                        if (directions == null) {
                            mutableSetOf(guardLocation.direction)
                        } else {
                            if (directions.contains(guardLocation.direction))
                                throw RuntimeException("Already there")
                            directions.also { it.add(guardLocation.direction) }
                        }
                    }

                    guardLocation.moveToNextPosition()
                }
            }
        }

        private fun checkIfPatrolFinishes(map: Map<Position, PositionType>) = runCatching {
            Laboratory(initGuardLocation, map).patrol()
            true
        }.getOrDefault(false)
    }

    private data class GuardLocation(var position: Position, var direction: Direction) {
        fun nextPosition() = position.next(direction)

        fun moveToNextPosition() {
            position = position.next(direction)
        }

        fun turn90() {
            direction = direction.turn90()
        }
    }

    private enum class PositionType {
        Obstacle, Normal;
    }

    private data class Position(val x: Int, val y: Int) {
        fun next(direction: Direction): Position {
            return when (direction) {
                Direction.Up -> Position(x, y - 1)
                Direction.Down -> Position(x, y + 1)
                Direction.Left -> Position(x - 1, y)
                Direction.Right -> Position(x + 1, y)
            }
        }
    }

    private enum class Direction {
        Up, Down, Left, Right;

        fun turn90(): Direction {
            return when (this) {
                Up -> Right
                Down -> Left
                Left -> Up
                Right -> Down
            }
        }
    }
}