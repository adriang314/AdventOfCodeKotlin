package year2017

import common.*

fun main() = println(SolutionDay22().result())

class SolutionDay22 : BaseSolution() {

    override val day = 22

    override fun task1(): String {
        val gridComputingCluster = GridComputingCluster(input())
        repeat(10_000) { gridComputingCluster.burstVirusCarrier() }

        return gridComputingCluster.virusCarrier.infectedNodes.toString()
    }

    override fun task2(): String {
        val gridComputingCluster = GridComputingCluster(input())
        repeat(10_000_000) { gridComputingCluster.burstEvolvedVirusCarrier() }
        return gridComputingCluster.evolvedVirusCarrier.infectedNodes.toString()
    }

    private class GridComputingCluster(input: String) {
        private val areaBuilder = Grid.Builder(0..400, 0..400) { _ -> '.' }
        private val area = Grid(areaBuilder) { c, position -> Point(position, c) }
        private val infectionMap = Grid(input) { c, position -> Point(position, c) }
        val virusCarrier: VirusCarrier
        val evolvedVirusCarrier: EvolvedVirusCarrier

        init {
            // offset to put infection map in the middle of area
            val offset = (area.width / 2) - (infectionMap.width / 2)
            val infectionMapCentralNode = Position(infectionMap.width / 2, infectionMap.height / 2)
            // copying infection map into area
            infectionMap.cells.forEach { cell -> area.getCell(cell.position.shift(offset, offset))!!.value = cell.value }
            virusCarrier = VirusCarrier(DirectedPosition(Direction.N, infectionMapCentralNode.shift(offset, offset)))
            evolvedVirusCarrier = EvolvedVirusCarrier(DirectedPosition(Direction.N, infectionMapCentralNode.shift(offset, offset)))
        }

        fun burstVirusCarrier() = run { virusCarrier.move(area) }

        fun burstEvolvedVirusCarrier() = run { evolvedVirusCarrier.move(area) }
    }

    private class Point(position: Position, c: Char) : Cell<Point>(position, c) {
        fun isInfected() = value == '#'
        fun isClean() = value == '.'
        fun isWeakened() = value == 'W'
        fun isFlagged() = value == 'F'
        fun clean() = run { value = '.' }
        fun weaken() = run { value = 'W' }
        fun flag() = run { value = 'F' }
        fun infect() = run { value = '#' }
    }

    private data class VirusCarrier(var virus: DirectedPosition, var infectedNodes: Long = 0L) {

        fun move(area: Grid<Point>) {
            val currentNode = area.getCell(virus.position)!!
            val nextDirection = if (currentNode.isInfected()) virus.direction.turnRight() else virus.direction.turnLeft()
            when {
                currentNode.isInfected() -> currentNode.clean()
                else -> currentNode.infect().also { infectedNodes++ }
            }

            virus = DirectedPosition(nextDirection, virus.position.next(nextDirection))
        }
    }

    private data class EvolvedVirusCarrier(var virus: DirectedPosition, var infectedNodes: Long = 0L) {

        fun move(area: Grid<Point>) {
            val currentNode = area.getCell(virus.position)!!
            val nextDirection = when {
                currentNode.isClean() -> virus.direction.turnLeft()
                currentNode.isWeakened() -> virus.direction
                currentNode.isInfected() -> virus.direction.turnRight()
                currentNode.isFlagged() -> virus.direction.turnBack()
                else -> throw IllegalStateException("Unknown node state")
            }
            when {
                currentNode.isClean() -> currentNode.weaken()
                currentNode.isWeakened() -> currentNode.infect().also { infectedNodes++ }
                currentNode.isInfected() -> currentNode.flag()
                currentNode.isFlagged() -> currentNode.clean()
            }

            virus = DirectedPosition(nextDirection, virus.position.next(nextDirection))
        }
    }
}