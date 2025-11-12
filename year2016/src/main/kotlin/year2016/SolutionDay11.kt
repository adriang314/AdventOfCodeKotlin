package year2016

import common.BaseSolution
import common.pow

fun main() = println(SolutionDay11().result())

class SolutionDay11 : BaseSolution() {

    override val day = 11

    private val facility1 = input()

    private val facility2 = input().split("\r\n").mapIndexed { index, line ->
        if (index == 0) {
            "$line and an elerium generator, an elerium-compatible microchip, a dilithium generator, a dilithium-compatible microchip"
        } else {
            line
        }
    }.joinToString("\r\n")

    override fun task1(): String {
        val result = FacilityState.from(facility1).stepsToAssembleMachine()
        return result.toString()
    }

    override fun task2(): String {
        val result = FacilityState.from(facility2).stepsToAssembleMachine()
        return result.toString()
    }

    private class FacilityState(private val floors: List<Floor>, private var elevatorCurrentFloor: Int = 1, var iteration: Int = 0) {

        companion object {
            fun from(input: String): FacilityState {
                val floors = input.split("\r\n").mapIndexed { index, line -> Floor(number = index + 1, items = Generator.from(line).plus(Microchip.from(line)).toMutableList()) }
                return FacilityState(floors)
            }
        }

        fun stepsToAssembleMachine(): Int {
            val statesToTest = ArrayDeque<FacilityState>(listOf(this))
            val stateTested = mutableMapOf<Long, Int>()

            while (statesToTest.isNotEmpty()) {
                val currentState = statesToTest.removeFirst()
                if (currentState.readyToAssemble()) {
                    return currentState.iteration
                }

//                if (statesToTest.size % 1000 == 0) {
//                    println("States to test: ${statesToTest.size}, tested: ${stateTested.size}, current iteration: ${currentState.iteration}")
//                }

                for (move in currentState.availableElevatorMoves()) {
                    val nextState = currentState.clone()
                    nextState.executeElevatorMove(move)
                    val code = nextState.index()
                    val existingIteration = stateTested[code]
                    if (existingIteration != null && existingIteration <= nextState.iteration) {
                        continue
                    }
                    stateTested[code] = nextState.iteration
                    statesToTest.addLast(nextState)
                }
            }

            throw IllegalStateException("No solution found")
        }

        /**
         * A unique index for the current state of the facility
         * This index is calculated based on the current floor of the elevator and the items on each floor.
         * The index is used to identify unique states of the facility to avoid re-processing the same state.
         * Each item contributes to the index based on its type and the floor it is on.
         * The formula used ensures that different configurations of items and elevator positions yield different indices.
         * @see Item.index
         *
         * @return Long - unique index representing the current state of the facility
         */
        private fun index(): Long = elevatorCurrentFloor.toLong() + floors.sumOf { floor -> floor.items.sumOf { item -> item.index(floor) } }

        private fun readyToAssemble(): Boolean = floors.filter { !it.isLastFloor }.all { it.items.isEmpty() }

        private fun availableElevatorMoves(): List<ElevatorMove> {
            val moves = mutableListOf<ElevatorMove>()
            val directions = mutableListOf<ElevatorMove.Direction>()
            val currentFloor = currentElevatorFloor()
            if (!currentFloor.isLastFloor) directions.add(ElevatorMove.Direction.Up)
            if (!currentFloor.isFirstFloor) directions.add(ElevatorMove.Direction.Down)

            val itemsOnCurrentFloor = currentFloor.items
            for (direction in directions) {
                // single item moves
                for (item in itemsOnCurrentFloor) {
                    if (direction == ElevatorMove.Direction.Down && lowerElevatorFloor().items.isEmpty())
                        continue

                    if (isMoveSafe(direction, item))
                        moves.add(ElevatorMove(direction, setOf(item)))
                }
                // two items move
                for (i in itemsOnCurrentFloor.indices) {
                    for (j in i + 1 until itemsOnCurrentFloor.size) {
                        if (direction == ElevatorMove.Direction.Down && lowerElevatorFloor().items.isEmpty())
                            continue

                        if (isMoveSafe(direction, itemsOnCurrentFloor.elementAt(i), itemsOnCurrentFloor.elementAt(j)))
                            moves.add(ElevatorMove(direction, setOf(itemsOnCurrentFloor.elementAt(i), itemsOnCurrentFloor.elementAt(j))))
                    }
                }
            }

            return moves
        }

        private fun executeElevatorMove(move: ElevatorMove) {
            // remove items from current floor
            currentElevatorFloor().items.removeAll(move.items)

            // move elevator to target floor
            when (move.direction) {
                ElevatorMove.Direction.Up -> elevatorCurrentFloor += 1
                ElevatorMove.Direction.Down -> elevatorCurrentFloor -= 1
            }

            // add items to target floor
            currentElevatorFloor().items.addAll(move.items)

            iteration++
        }

        private fun clone(): FacilityState {
            return FacilityState(floors.map { it.clone() }, elevatorCurrentFloor, iteration)
        }

        private fun currentElevatorFloor(): Floor = floors[elevatorCurrentFloor - 1]

        private fun upperElevatorFloor(): Floor = floors[elevatorCurrentFloor]

        private fun lowerElevatorFloor(): Floor = floors[elevatorCurrentFloor - 2]

        private fun isMoveSafe(direction: ElevatorMove.Direction, itemToMove: Item): Boolean {
            val isCurrentFloorSafe = Items.areSafe(currentElevatorFloor().items.minus(itemToMove))
            if (!isCurrentFloorSafe)
                return false

            return when (direction) {
                ElevatorMove.Direction.Up -> Items.areSafe(upperElevatorFloor().items.plus(itemToMove))
                ElevatorMove.Direction.Down -> Items.areSafe(lowerElevatorFloor().items.plus(itemToMove))
            }
        }

        private fun isMoveSafe(direction: ElevatorMove.Direction, itemToMove1: Item, itemToMove2: Item): Boolean {
            val isCurrentFloorSafe = Items.areSafe(currentElevatorFloor().items.minus(itemToMove1).minus(itemToMove2))
            if (!isCurrentFloorSafe)
                return false

            return when (direction) {
                ElevatorMove.Direction.Up -> Items.areSafe(upperElevatorFloor().items.plus(itemToMove1).plus(itemToMove2))
                ElevatorMove.Direction.Down -> Items.areSafe(lowerElevatorFloor().items.plus(itemToMove1).plus(itemToMove2))
            }
        }
    }

    private class Floor(val number: Int, val items: MutableList<Item> = mutableListOf()) {
        val isFirstFloor = number == 1
        val isLastFloor = number == 4

        fun clone(): Floor = Floor(number, items.toMutableList())
    }

    private data class ElevatorMove(val direction: Direction, val items: Set<Item>) {
        enum class Direction { Up, Down }
    }

    private interface Item {
        val id: Int
        val name: String
        val isGenerator: Boolean
        val isMicrochip: Boolean
        val type: String

        /**
         * Calculate a unique index for the item based on its type, id, and the floor it is on.
         * This index is used to uniquely identify the item's contribution to the facility's state.
         * The formula ensures that different items on different floors yield different indices.
         *
         * @param floor Floor - the floor on which the item is located
         * @return Long - unique index representing the item's contribution to the facility's state
         */
        fun index(floor: Floor): Long {
            return floor.number * 10L.pow(this.id)
        }
    }

    private interface Items {
        companion object {
            var nextId: Int = 1

            fun areSafe(items: List<Item>): Boolean {
                for (item in items) {
                    if (item.isMicrochip) {
                        val hasGenerator = items.any { it.name == item.name && it.isGenerator }
                        if (hasGenerator)
                            continue

                        val hasOtherGenerators = items.any { it.name != item.name && it.isGenerator }
                        if (hasOtherGenerators)
                            return false
                    }
                }

                return true
            }
        }
    }

    private data class Generator(override val name: String, override val id: Int) : Item {
        override val isGenerator = true
        override val isMicrochip = false
        override val type = "generator"

        companion object {
            fun from(input: String): List<Generator> {
                val regex = Regex("([a-z]+) generator")
                return regex.findAll(input).map { match ->
                    Generator(match.groupValues[1], Items.nextId++)
                }.toList()
            }
        }
    }

    private data class Microchip(override val name: String, override val id: Int) : Item {
        override val isGenerator = false
        override val isMicrochip = true
        override val type = "microchip"

        companion object {
            fun from(input: String): List<Microchip> {
                val regex = Regex("([a-z]+)-compatible microchip")
                return regex.findAll(input).map { match ->
                    Microchip(match.groupValues[1], Items.nextId++)
                }.toList()
            }
        }
    }
}