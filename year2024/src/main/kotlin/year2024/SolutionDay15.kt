package year2024

import common.BaseSolution
import common.Direction
import common.Position
import java.util.LinkedList

fun main() = println(SolutionDay15().result())

class SolutionDay15 : BaseSolution() {

    override val day = 15
    
    private val map1: MutableMap<Position, ObjectType>
    private val map2: MutableMap<Position, ObjectType>
    private val moves: List<Direction>
    private lateinit var initRobotPosition1: Position
    private lateinit var initRobotPosition2: Position

    init {
        val tmpMoves = mutableListOf<Direction>()
        val tmpMap1 = mutableMapOf<Position, ObjectType>()
        val tmpMap2 = mutableMapOf<Position, ObjectType>()
        var parseMoves = false

        input().split("\r\n").forEachIndexed { y, line ->
            if (line.isEmpty()) {
                parseMoves = true
            } else {
                line.forEachIndexed { x, c ->
                    if (parseMoves) {
                        tmpMoves.add(Direction.from(c))
                    } else {
                        // map 1
                        val position = Position(x, y)
                        val objectType = ObjectType.from(c)
                        if (c == '@') {
                            initRobotPosition1 = position
                        }
                        tmpMap1[position] = objectType

                        // map 2
                        val positionL = Position(2 * x, y)
                        val positionR = Position(2 * x + 1, y)

                        if (c == '@') {
                            initRobotPosition2 = positionL
                        }
                        tmpMap2[positionL] = objectType
                        tmpMap2[positionR] = objectType
                        if (objectType == ObjectType.Box) {
                            tmpMap2[positionL] = ObjectType.BoxLeft
                            tmpMap2[positionR] = ObjectType.BoxRight
                        }
                    }
                }
            }
        }

        map1 = tmpMap1
        map2 = tmpMap2
        moves = tmpMoves
    }

    override fun task1(): String {
        var currRobotPosition = initRobotPosition1
        val moves = LinkedList(moves)
        while (moves.isNotEmpty()) {
            val move = moves.pop()
            val nextPosition = currRobotPosition.next(move)
            val nextPositionType = map1[nextPosition]!!
            when (nextPositionType) {
                ObjectType.Space -> currRobotPosition = nextPosition
                ObjectType.Wall -> {} // no way to move
                ObjectType.Box, ObjectType.BoxLeft, ObjectType.BoxRight -> {
                    // try to move single box or boxes
                    val moved = tryToMoveSingleBox(nextPosition, move)
                    if (moved) {
                        currRobotPosition = nextPosition
                    }
                }
            }
        }

        val result = map1.filterValues { it == ObjectType.Box }.map { it.key }.sumOf { (100 * it.y) + it.x }
        return result.toString()
    }

    override fun task2(): String {
        var currRobotPosition = initRobotPosition2
        val moves = LinkedList(moves)
        while (moves.isNotEmpty()) {
            val move = moves.pop()
            val nextPosition = currRobotPosition.next(move)
            val nextPositionType = map2[nextPosition]!!
            when (nextPositionType) {
                ObjectType.Space -> currRobotPosition = nextPosition
                ObjectType.Wall -> {} // no way to move
                ObjectType.BoxLeft, ObjectType.BoxRight, ObjectType.Box -> {
                    // try to move double box or boxes
                    val canMove = canMoveDoubleBox(nextPosition, move)
                    if (canMove) {
                        moveDoubleBox(nextPosition, move)
                        currRobotPosition = nextPosition
                    }
                }
            }
        }

        val result = map2.filterValues { it == ObjectType.BoxLeft }.map { it.key }.sumOf { (100 * it.y) + it.x }
        return result.toString()
    }

    private fun tryToMoveSingleBox(from: Position, direction: Direction): Boolean {
        val to = from.next(direction)
        val toObjectType = map1[to]

        when (toObjectType) {
            ObjectType.Wall -> return false
            // if there is a space to move box, just moving it
            ObjectType.Space -> {
                map1[to] = ObjectType.Box
                map1[from] = ObjectType.Space
                return true
            }
            // if there is other box, current can be moved if next can be moved
            ObjectType.Box -> {
                if (tryToMoveSingleBox(to, direction)) {
                    map1[to] = ObjectType.Box
                    map1[from] = ObjectType.Space
                    return true
                }
                return false
            }

            else -> throw RuntimeException("Not supported object type")
        }
    }

    private fun canMoveDoubleBox(from: Position, direction: Direction): Boolean {
        val fromObjectType = map2[from]!!
        if (fromObjectType == ObjectType.Wall)
            return false
        if (fromObjectType == ObjectType.Space) {
            return true
        }

        val boxLPosition = if (fromObjectType == ObjectType.BoxLeft) from else from.w()
        val boxRPosition = if (fromObjectType == ObjectType.BoxRight) from else from.e()

        val boxLNextPosition = boxLPosition.next(direction)
        val boxRNextPosition = boxRPosition.next(direction)

        val boxLNextPositionType = map2[boxLNextPosition]!!
        val boxRNextPositionType = map2[boxRNextPosition]!!

        // no way to move if there is a wall
        if (boxLNextPositionType == ObjectType.Wall || boxRNextPositionType == ObjectType.Wall) {
            return false
        }

        // moving to left or right requires only one free space
        if ((direction == Direction.W || direction == Direction.E) &&
            (boxLNextPositionType == ObjectType.Space || boxRNextPositionType == ObjectType.Space)
        ) {
            return true
        }

        // moving up or down requires two empty spaces
        if ((direction == Direction.N || direction == Direction.S) &&
            (boxLNextPositionType == ObjectType.Space && boxRNextPositionType == ObjectType.Space)
        ) {
            return true
        }

        return when (direction) {
            Direction.W -> canMoveDoubleBox(boxLNextPosition, direction)
            Direction.E -> canMoveDoubleBox(boxRNextPosition, direction)
            else -> canMoveDoubleBox(boxLNextPosition, direction) && canMoveDoubleBox(boxRNextPosition, direction)
        }
    }

    private fun moveDoubleBox(from: Position, direction: Direction) {
        val fromObjectType = map2[from]!!
        if (fromObjectType == ObjectType.Wall || fromObjectType == ObjectType.Space) {
            return
        }

        val boxLPosition = if (fromObjectType == ObjectType.BoxLeft) from else from.w()
        val boxRPosition = if (fromObjectType == ObjectType.BoxRight) from else from.e()

        val boxLNextPosition = boxLPosition.next(direction)
        val boxRNextPosition = boxRPosition.next(direction)

        val boxLNextPositionType = map2[boxLNextPosition]!!
        val boxRNextPositionType = map2[boxRNextPosition]!!

        // no way to move if there is a wall
        if (boxLNextPositionType == ObjectType.Wall || boxRNextPositionType == ObjectType.Wall)
            return

        // moving to left or right requires only one free space
        if ((direction == Direction.E || direction == Direction.W) &&
            (boxLNextPositionType == ObjectType.Space || boxRNextPositionType == ObjectType.Space)
        ) {
            map2[boxLNextPosition] = ObjectType.BoxLeft
            map2[boxRNextPosition] = ObjectType.BoxRight
            if (direction == Direction.W) {
                map2[boxRPosition] = ObjectType.Space
            } else {
                map2[boxLPosition] = ObjectType.Space
            }
            return
        }

        // moving up or down requires two empty spaces
        if ((direction == Direction.N || direction == Direction.S) &&
            (boxLNextPositionType == ObjectType.Space && boxRNextPositionType == ObjectType.Space)
        ) {
            map2[boxLNextPosition] = ObjectType.BoxLeft
            map2[boxRNextPosition] = ObjectType.BoxRight
            map2[boxLPosition] = ObjectType.Space
            map2[boxRPosition] = ObjectType.Space
            return
        }

        when (direction) {
            Direction.W -> moveDoubleBox(boxLNextPosition, direction)
            Direction.E -> moveDoubleBox(boxRNextPosition, direction)
            else -> {
                moveDoubleBox(boxLNextPosition, direction)
                moveDoubleBox(boxRNextPosition, direction)
            }
        }

        if (direction == Direction.E || direction == Direction.W) {
            map2[boxLNextPosition] = ObjectType.BoxLeft
            map2[boxRNextPosition] = ObjectType.BoxRight
            if (direction == Direction.W) {
                map2[boxRPosition] = ObjectType.Space
            } else {
                map2[boxLPosition] = ObjectType.Space
            }
        } else {
            map2[boxLNextPosition] = ObjectType.BoxLeft
            map2[boxRNextPosition] = ObjectType.BoxRight
            map2[boxLPosition] = ObjectType.Space
            map2[boxRPosition] = ObjectType.Space
        }
    }

    private enum class ObjectType {
        Box, Wall, Space, BoxLeft, BoxRight;

        companion object {
            fun from(c: Char) = when (c) {
                '.' -> Space
                '@' -> Space
                '#' -> Wall
                'O' -> Box
                else -> throw RuntimeException("Unknown object type")
            }
        }
    }
}