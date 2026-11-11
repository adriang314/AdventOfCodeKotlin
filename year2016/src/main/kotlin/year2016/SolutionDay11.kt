package year2016

import common.BaseSolution
import common.Combinatorics

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
                val floors = input.split("\r\n").mapIndexed { index, line -> Floor(number = index + 1, items = Items(Generator.from(line).plus(Microchip.from(line)).toMutableList())) }
                return FacilityState(floors)
            }
        }

        fun stepsToAssembleMachine(): Int {
            val statesToTest = ArrayDeque(listOf(this))
            val visited = mutableSetOf(code())

            while (statesToTest.isNotEmpty()) {
                val currentState = statesToTest.removeFirst()
                if (currentState.readyToAssemble()) {
                    return currentState.iteration
                }

                for (move in currentState.availableElevatorMoves()) {
                    val nextState = currentState.clone()
                    nextState.executeElevatorMove(move)
                    val code = nextState.code()

                    if (visited.add(code))
                        statesToTest.addLast(nextState)
                }
            }

            throw IllegalStateException("No solution found")
        }

        /**
         * Element names are ignored because swapping element names does not change the problem.
         */
        private fun code(): String {
            val positions = mutableMapOf<String, GeneratorAndMicrochipPosition>()

            floors.forEach { floor ->
                floor.items.forEach { item ->
                    val position = positions.getOrPut(item.name) { GeneratorAndMicrochipPosition(item.name) }
                    if (item.isGenerator) {
                        position.generatorFloor = floor.number
                    } else {
                        position.microchipFloor = floor.number
                    }
                }
            }

            return "$elevatorCurrentFloor|" + positions.values.sorted().joinToString(",") { "${it.generatorFloor}${it.microchipFloor}" }
        }

        private fun readyToAssemble(): Boolean = floors.filter { !it.isLastFloor }.all { it.items.isEmpty() }

        private fun availableElevatorMoves(): List<ElevatorMove> {
            val moves = mutableListOf<ElevatorMove>()
            val directions = mutableListOf<ElevatorMove.Direction>()
            val currentFloor = currentElevatorFloor()
            if (!currentFloor.isLastFloor) directions.add(ElevatorMove.Direction.Up)
            if (!currentFloor.isFirstFloor) directions.add(ElevatorMove.Direction.Down)

            for (direction in directions) {
                if (direction == ElevatorMove.Direction.Down && lowerElevatorFloor().items.isEmpty())
                    continue

                for (items in currentFloor.items.combinations()) {
                    val isSafe = when (direction) {
                        ElevatorMove.Direction.Up -> currentFloor.items.minus(items).areSafe() && upperElevatorFloor().items.plus(items).areSafe()
                        ElevatorMove.Direction.Down -> currentFloor.items.minus(items).areSafe() && lowerElevatorFloor().items.plus(items).areSafe()
                    }

                    if (isSafe)
                        moves.add(ElevatorMove(direction, items))
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

        private fun clone(): FacilityState = FacilityState(floors.map { it.clone() }, elevatorCurrentFloor, iteration)

        private fun currentElevatorFloor(): Floor = floors[elevatorCurrentFloor - 1]

        private fun upperElevatorFloor(): Floor = floors[elevatorCurrentFloor]

        private fun lowerElevatorFloor(): Floor = floors[elevatorCurrentFloor - 2]
    }

    private class Floor(val number: Int, val items: Items = Items()) {
        val isFirstFloor = number == 1
        val isLastFloor = number == 4

        fun clone(): Floor = Floor(number, items.clone())
    }

    private data class ElevatorMove(val direction: Direction, val items: Set<Item>) {
        enum class Direction { Up, Down }
    }

    private data class GeneratorAndMicrochipPosition(val name: String, var generatorFloor: Int = 0, var microchipFloor: Int = 0) : Comparable<GeneratorAndMicrochipPosition> {
        override fun compareTo(other: GeneratorAndMicrochipPosition): Int {
            val generatorComparison = generatorFloor.compareTo(other.generatorFloor)
            if (generatorComparison != 0)
                return generatorComparison

            return microchipFloor.compareTo(other.microchipFloor)
        }
    }

    private interface Item {
        val id: Int
        val name: String
        val isGenerator: Boolean
        val isMicrochip: Boolean
    }

    private data class Items(val list: MutableList<Item> = mutableListOf()) : Iterable<Item> {

        override fun iterator(): Iterator<Item> = list.iterator()

        fun combinations(): List<Set<Item>> = Combinatorics.combinations(list, 1) + Combinatorics.combinations(list, 2)

        fun isEmpty(): Boolean = list.isEmpty()

        fun addAll(items: Collection<Item>) = list.addAll(items)

        fun removeAll(items: Collection<Item>) = list.removeAll(items.toSet())

        fun minus(items: Set<Item>): Items = Items(list.filterNot { it in items }.toMutableList())

        fun plus(items: Set<Item>): Items = Items((list + items).toMutableList())

        fun clone(): Items = Items(list.toMutableList())

        fun areSafe(): Boolean {
            for (item in list) {
                if (item.isMicrochip) {
                    val hasGenerator = list.any { it.name == item.name && it.isGenerator }
                    if (hasGenerator)
                        continue

                    val hasOtherGenerators = list.any { it.name != item.name && it.isGenerator }
                    if (hasOtherGenerators)
                        return false
                }
            }

            return true
        }

        companion object {
            var nextId: Int = 1
        }
    }

    private data class Generator(override val name: String, override val id: Int) : Item {
        override val isGenerator = true
        override val isMicrochip = false

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