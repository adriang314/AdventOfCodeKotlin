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

    /**
     * Return N, E, S, W neighbours.
     */
    fun neighbours() = listOfNotNull(n, e, s, w)

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

    /**
     * Returns the Manhattan distance between two positions.
     */
    fun distance(a: Position, b: Position): Long {
        return (a.x - b.x).absoluteValue + (a.y - b.y).absoluteValue.toLong()
    }

    fun isPositionOnEdge(position: Position): Boolean {
        if (position.x == 0 || position.y == 0)
            return true
        if (position.x == width - 1 || position.y == height - 1)
            return true
        return false
    }

    fun getCell(position: Position): T? {
        return cells[position]
    }
}