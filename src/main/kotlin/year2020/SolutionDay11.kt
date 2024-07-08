package year2020

import common.BaseSolution

fun main() = println(SolutionDay11().result())

class SolutionDay11 : BaseSolution() {

    override val day = 11
    override val year = 2020

    override fun task1(): String {
        return execute(4, Location::neighbours)
    }

    override fun task2(): String {
        return execute(5, Location::longNeighbours)
    }

    private val locations: List<List<Location>> = input().split("\r\n")
        .mapIndexed { x, line -> line.toList().mapIndexed { y, c -> Location(x, y, c == 'L', false) } }

    init {
        val height: Int = locations.size
        val length: Int = locations.first().size
        for (i in 0 until height) {
            for (j in 0 until length) {
                val current = locations[i][j]
                val upLeft = locations.getOrNull(i - 1)?.getOrNull(j - 1)
                val up = locations.getOrNull(i - 1)?.getOrNull(j)
                val upRight = locations.getOrNull(i - 1)?.getOrNull(j + 1)
                val downLeft = locations.getOrNull(i + 1)?.getOrNull(j - 1)
                val down = locations.getOrNull(i + 1)?.getOrNull(j + 1)
                val downRight = locations.getOrNull(i + 1)?.getOrNull(j)
                val left = locations.getOrNull(i)?.getOrNull(j - 1)
                val right = locations.getOrNull(i)?.getOrNull(j + 1)

                current.upLeft = upLeft
                current.up = up
                current.upRight = upRight
                current.downLeft = downLeft
                current.downRight = downRight
                current.down = down
                current.left = left
                current.right = right

                current.neighbours = listOfNotNull(left, right, upLeft, up, upRight, downLeft, down, downRight)
                    .filter { it.isSeat }
            }
        }

        for (i in 0 until height) {
            for (j in 0 until length) {
                val current = locations[i][j]
                current.longNeighbours = listOfNotNull(
                    current.left?.findLongNeighbour { it.left },
                    current.right?.findLongNeighbour { it.right },
                    current.upLeft?.findLongNeighbour { it.upLeft },
                    current.up?.findLongNeighbour { it.up },
                    current.upRight?.findLongNeighbour { it.upRight },
                    current.downLeft?.findLongNeighbour { it.downLeft },
                    current.down?.findLongNeighbour { it.down },
                    current.downRight?.findLongNeighbour { it.downRight }
                )
            }
        }
    }

    private fun execute(minOccupied: Int, neighbours: (location: Location) -> List<Location>): String {
        reset()
        do {
            val change = iterate(minOccupied, neighbours)
        } while (change)
        val occupiedLocations = locations.flatten().count { it.occupied }
        return occupiedLocations.toString()
    }

    private fun reset() = locations.flatten().forEach { it.reset() }

    private fun iterate(minOccupied: Int, neighbours: (location: Location) -> List<Location>): Boolean {
        locations.flatten().filter { it.isSeat }.forEach { location ->
            if (!location.occupied && neighbours(location).all { !it.occupied })
                location.nextOccupied = true
            else if (location.occupied && neighbours(location).count { it.occupied } >= minOccupied)
                location.nextOccupied = false
        }

        var anyChange = false
        locations.flatten().filter { it.isSeat }.forEach { location ->
            if (location.occupied != location.nextOccupied) {
                location.occupied = location.nextOccupied
                anyChange = true
            }
        }

        return anyChange
    }

    class Location(
        val rowIdx: Int,
        val colIdx: Int,
        val isSeat: Boolean,
        var occupied: Boolean,
        var nextOccupied: Boolean = false
    ) {
        val id = "[$rowIdx,$colIdx]"
        var left: Location? = null
        var right: Location? = null
        var upLeft: Location? = null
        var up: Location? = null
        var upRight: Location? = null
        var downLeft: Location? = null
        var down: Location? = null
        var downRight: Location? = null

        var neighbours: List<Location> = emptyList()
        var longNeighbours: List<Location> = emptyList()

        fun reset() {
            occupied = false
            nextOccupied = false
        }

        fun findLongNeighbour(next: (location: Location) -> Location?): Location? =
            if (isSeat) this else next(this)?.findLongNeighbour(next)
    }
}