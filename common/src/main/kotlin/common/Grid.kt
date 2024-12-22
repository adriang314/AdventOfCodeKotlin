package common

import kotlin.math.absoluteValue

abstract class Cell(val position: Position, val c: Char) {
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

class Grid<T : Cell>(input: String, cellFactory: (Char, Position) -> T) {
    val cells: Map<Position, T>
    val height: Int
    val width: Int

    init {
        val lines = input.lines()
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
        for (y in lines.indices) {
            for (x in lines[y].indices) {
                val position = Position(x, y)
                val name = lines[y][x]
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

        cells = tempCells
        height = lines.size
        width = lines.first().length
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
}