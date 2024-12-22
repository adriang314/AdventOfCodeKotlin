package common

abstract class Cell(val position: Position, val value: Char) {
    var n: Cell? = null
    var s: Cell? = null
    var e: Cell? = null
    var w: Cell? = null
    var nw: Cell? = null
    var ne: Cell? = null
    var sw: Cell? = null
    var se: Cell? = null

    var canGoN = false
    var canGoNW = false
    var canGoNE = false
    var canGoS = false
    var canGoSW = false
    var canGoSE = false
    var canGoE = false
    var canGoW = false

    /**
     * Return N, E, S, W neighbours.
     */
    fun neighbours() = listOfNotNull(n, e, s, w)

    /**
     * Returns the Manhattan distance between this cell and the other one
     */
    fun distanceTo(other: Cell): Long = this.position.distanceTo(other.position)

    /**
     * Find direction from current cell of given neighbour.
     */
    fun findNeighbourDirection(neighbour: Cell): Direction {
        return when {
            n === neighbour -> Direction.Up
            s === neighbour -> Direction.Down
            w === neighbour -> Direction.Left
            e === neighbour -> Direction.Right
            else -> throw RuntimeException("Unknown direction")
        }
    }

    /**
     * Return N, NE, E, SE, S, SW, W, NW neighbours.
     */
    fun neighboursAll() = listOfNotNull(n, ne, e, se, s, sw, w, nw)
}

class Grid<T : Cell>(builder: Builder, cellFactory: (Char, Position) -> T) {
    constructor(input: String, cellFactory: (Char, Position) -> T) :
            this(input.lines().let {
                val lines = input.lines().map { it.toList() }
                val yRange = lines.indices
                val xRange = lines.first().indices
                Builder(xRange, yRange) { position -> lines[position.y][position.x] }
            }, cellFactory)

    val cells: Map<Position, T> = buildCells(builder, cellFactory)
    var height = builder.yRange.length()
    var width = builder.xRange.length()

    private fun buildCells(builder: Builder, cellFactory: (Char, Position) -> T): Map<Position, T> {
        val tempCells = mutableMapOf<Position, T>()
//   ------X----->
//   |
//   |
//   |
//   Y
//   |
//   |
//   |
//   v
        for (y in builder.yRange) {
            for (x in builder.xRange) {
                val position = Position(x, y)
                val name = builder.charOnPosition(position)
                tempCells[position] = cellFactory(name, position)
            }
        }

        tempCells.values.forEach { cell ->
            cell.n = tempCells[cell.position.n()]
            cell.s = tempCells[cell.position.s()]
            cell.e = tempCells[cell.position.e()]
            cell.w = tempCells[cell.position.w()]
            cell.nw = tempCells[cell.position.nw()]
            cell.ne = tempCells[cell.position.ne()]
            cell.sw = tempCells[cell.position.sw()]
            cell.se = tempCells[cell.position.se()]
        }

        return tempCells
    }

    fun isPositionOnEdge(position: Position): Boolean {
        if (position.x == 0 || position.y == 0)
            return true
        if (position.x == width - 1 || position.y == height - 1)
            return true
        return false
    }

    /**
     * Returns the cell at the given string position (e.g. "0,0").
     */
    fun getCell(position: String): T? {
        return cells[Position.fromString(position)]
    }

    /**
     * Returns the cell at the given position.
     */
    fun getCell(position: Position): T? {
        return cells[position]
    }

    class Builder(val xRange: IntRange, val yRange: IntRange, val charOnPosition: (position: Position) -> Char)
}