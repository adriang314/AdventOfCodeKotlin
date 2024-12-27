package common

/**
 * Class represents grid's cell
 *
 * @param position cell position, (x,y) coordinates
 * @param value cell value represented as single character
 */
abstract class Cell<T : Cell<T>>(val position: Position, val value: Char) {
    var n: T? = null
    var s: T? = null
    var e: T? = null
    var w: T? = null
    var nw: T? = null
    var ne: T? = null
    var sw: T? = null
    var se: T? = null

    var canGoN = false
    var canGoNW = false
    var canGoNE = false
    var canGoS = false
    var canGoSW = false
    var canGoSE = false
    var canGoE = false
    var canGoW = false

    /**
     * Gets parent grid
     */
    lateinit var grid : Grid<T>

    /**
     * Return N, E, S, W neighbours.
     */
    fun neighbours() = listOfNotNull(n, e, s, w)

    /**
     * Returns the Manhattan distance between this cell and the other one
     */
    fun distanceTo(other: Cell<T>): Long = this.position.distanceTo(other.position)

    /**
     * Find direction from current cell its neighbour.
     */
    fun findNeighbourDirection(neighbour: Cell<T>): Direction {
        return when {
            n === neighbour -> Direction.Up
            s === neighbour -> Direction.Down
            w === neighbour -> Direction.Left
            e === neighbour -> Direction.Right
            else -> throw RuntimeException("Unknown direction")
        }
    }

    /**
     * Returns N, NE, E, SE, S, SW, W, NW neighbours.
     */
    fun neighboursAll() = listOfNotNull(n, ne, e, se, s, sw, w, nw)
}

/**
 * Class represents rectangular grid with cells, where top left corner coordinates are (0,0)
 *
 * @param builder builder used to create grid
 * @param cellFactory method to create cell object
 */
class Grid<T : Cell<T>>(builder: Builder, cellFactory: (Char, Position) -> T) {
    constructor(input: String, cellFactory: (Char, Position) -> T) :
            this(input.lines().let {
                val lines = input.lines().map { it.toList() }
                val yRange = lines.indices
                val xRange = lines.first().indices
                Builder(xRange, yRange) { position -> lines[position.y][position.x] }
            }, cellFactory)

    private val cellMap: Map<Position, T> = buildCells(builder, cellFactory)
    val cells = cellMap.values
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
                val name = builder.valueOnPosition(position)
                val cell = cellFactory(name, position)
                cell.grid = this
                tempCells[position] = cell
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
        return cellMap[Position.fromString(position)]
    }

    /**
     * Returns the cell at the given (x,y) position.
     */
    fun getCell(x: Int, y: Int): T? {
        return cellMap[Position(x, y)]
    }

    /**
     * Returns the cell at the given position.
     */
    fun getCell(position: Position): T? {
        return cellMap[position]
    }

    /**
     * Grid builder class
     *
     * @param xRange range used as width  (0 ... width)
     * @param yRange range used as height (0 ... height)
     * @param valueOnPosition cell value on given (x,y) position
     */
    class Builder(val xRange: IntRange, val yRange: IntRange, val valueOnPosition: (position: Position) -> Char)
}