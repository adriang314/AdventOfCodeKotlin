package year2018

import common.BaseSolution

fun main() = println(SolutionDay13().result())

class SolutionDay13 : BaseSolution() {

    override val day = 13
    
    private val initialCartPositions = mutableMapOf<Position, Direction>()
    private val mapElements = input().split("\r\n").mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            val position = Position(x, y)
            if (c == 'v' || c == '^' || c == '<' || c == '>')
                initialCartPositions[position] = Direction.from(c)
            MapElementType.from(c)?.let { MapElement(position, it) }
        }.filterNotNull()
    }.flatten()

    override fun task1(): String {
        val map = MineMap(mapElements, initialCartPositions.map { Cart(it.value) to it.key }.toMap())
        var firstCrashPosition: Position?
        while (true) {
            map.moveCarts()
            firstCrashPosition = map.crashPosition()
            if (firstCrashPosition != null)
                break
        }

        return firstCrashPosition.toString()
    }

    override fun task2(): String {
        val map = MineMap(mapElements, initialCartPositions.map { Cart(it.value) to it.key }.toMap())
        val lastCartPosition: Position
        while (true) {
            map.moveCarts()
            val cartsRemoved = map.removeCrushedCarts()
            if (cartsRemoved && map.cartCount() == 1) {
                lastCartPosition = map.lastCartPosition()
                break
            }
        }

        return lastCartPosition.toString()
    }

    private class MineMap(elements: List<MapElement>, carts: Map<Cart, Position>) {
        private val map = elements.associateBy { it.position }
        private var cartPositions = carts.toMutableMap()

        fun cartCount() = cartPositions.size

        fun lastCartPosition() = cartPositions.entries.single().value

        fun removeCrushedCarts() = crashedCarts().let { crashedCarts ->
            if (crashedCarts.isEmpty()) {
                false
            } else {
                crashedCarts.forEach { cartPositions.remove(it) }
                true
            }
        }

        fun moveCarts() {
            for (movingCart in cartsMoveOrder()) {
                val cart = movingCart.key
                val position = movingCart.value

                // if cart crashed, it no longer moves
                if (isCartCrashAt(position))
                    continue

                val newPosition = position.next(cart.moveDirection)
                when (map[newPosition]!!.type) {
                    MapElementType.CROSSOVER -> cart.moveDirection = cart.moveDirection.turn(cart.nextTurn())
                    MapElementType.TURN_NW_OR_SE -> cart.moveDirection = when (cart.moveDirection) {
                        Direction.N -> Direction.E
                        Direction.S -> Direction.W
                        Direction.W -> Direction.S
                        Direction.E -> Direction.N
                    }

                    MapElementType.TURN_SW_OR_NE -> cart.moveDirection = when (cart.moveDirection) {
                        Direction.N -> Direction.W
                        Direction.S -> Direction.E
                        Direction.W -> Direction.N
                        Direction.E -> Direction.S
                    }

                    MapElementType.ROAD_NS -> {}
                    MapElementType.ROAD_WE -> {}
                }

                cartPositions[cart] = newPosition
            }
        }

        fun crashPosition() = crashInfo().map { it.key }.firstOrNull()

        private fun crashedCarts() = crashInfo().map { it.value }.flatten().map { it.key }

        private fun crashInfo() =
            cartPositions.entries.groupBy { it.value }.filter { it.value.size > 1 }

        private fun isCartCrashAt(position: Position) =
            cartPositions.entries.count { it.value == position } > 1

        private fun cartsMoveOrder() =
            cartPositions.entries.sortedWith(compareBy({ it.value.x }, { it.value.y }))
    }

    private data class MapElement(val position: Position, val type: MapElementType)

    private data class Position(val x: Int, val y: Int) {
        fun north() = Position(x, y - 1)
        fun south() = Position(x, y + 1)
        fun west() = Position(x - 1, y)
        fun east() = Position(x + 1, y)

        fun next(direction: Direction) = when (direction) {
            Direction.N -> north()
            Direction.S -> south()
            Direction.W -> west()
            Direction.E -> east()
        }

        override fun toString() = "$x,$y"
    }

    private class Cart(var moveDirection: Direction, var lastTurn: TurnType = TurnType.NONE) {
        fun nextTurn(): TurnType {
            lastTurn = lastTurn.next()
            return lastTurn
        }
    }

    private enum class Direction {
        N, S, W, E;

        fun turn(type: TurnType): Direction {
            return when (type) {
                TurnType.NONE, TurnType.GO_STRAIGHT -> this
                TurnType.TURN_L -> when (this) {
                    N -> W
                    S -> E
                    W -> S
                    E -> N
                }

                TurnType.TURN_R -> when (this) {
                    N -> E
                    S -> W
                    W -> N
                    E -> S
                }
            }
        }

        companion object {
            fun from(c: Char): Direction {
                return when (c) {
                    '<' -> W
                    '>' -> E
                    'v' -> S
                    '^' -> N
                    else -> throw IllegalArgumentException("Unknown direction")
                }
            }
        }
    }

    private enum class TurnType {
        NONE, TURN_L, TURN_R, GO_STRAIGHT;

        fun next(): TurnType {
            return when (this) {
                NONE -> TURN_L
                TURN_L -> GO_STRAIGHT
                TURN_R -> TURN_L
                GO_STRAIGHT -> TURN_R
            }
        }
    }

    private enum class MapElementType(val labels: List<Char>) {
        ROAD_NS(listOf('|', '^', 'v')),
        ROAD_WE(listOf('-', '<', '>')),
        CROSSOVER(listOf('+')),
        TURN_NW_OR_SE(listOf('/')),
        TURN_SW_OR_NE(listOf('\\'));

        companion object {
            fun from(c: Char): MapElementType? = MapElementType.values().firstOrNull { it.labels.contains(c) }
        }
    }
}
