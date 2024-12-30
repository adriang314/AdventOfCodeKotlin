package common

import java.util.*

/**
 * Class represents a list of cells in grid forming a path
 */
class Path<T : Cell<T>>(val cells: List<T>) : Comparable<Path<T>> {
    init {
        require(cells.isNotEmpty())
    }

    /**
     * Number of connections between cells
     */
    val connections = cells.size - 1
    val start = cells.first()
    val end = cells.last()

    /**
     * Checks whether this path contains cell
     *
     * @param cell to search in path
     */
    fun contains(cell: T) = cells.any { it == cell }

    /**
     * Add new cell at the end of this path. Returns new path.
     *
     * @param cell to add to this path
     */
    fun add(cell: T) = Path(cells.plus(cell))

    /**
     * Creates sub path starting from given cell inside this path.
     *
     * @param cell to start sub path from
     *
     * @return new path or null if [cell] cannot be found in this path
     */
    fun subPath(cell: T): Path<T>? {
        val cellIdx = cells.indexOf(cell)
        if (cellIdx == -1)
            return null

        return Path(cells.subList(cellIdx, cells.size))
    }

    override fun compareTo(other: Path<T>): Int {
        if (connections != other.connections || start != other.start || end != other.end)
            throw RuntimeException("Cannot compare these paths")

        for (idx in cells.indices) {
            val thisPathCell = cells[idx]
            val otherPathCell = other.cells[idx]

            val positionComparison = thisPathCell.position.compareTo(otherPathCell.position)
            if (positionComparison != 0)
                return positionComparison
        }

        return 0
    }

    override fun toString(): String {
        return "[${cells.joinToString(",") { "(${it.position})" }}]"
    }
}

/**
 * Class represents grid's cell
 *
 * @param position cell position, (x,y) coordinates
 * @param value cell value represented as single character
 */
abstract class Cell<T : Cell<T>>(val position: Position, var value: Char) {

    @Suppress("UNCHECKED_CAST")
    private fun current() = this as T

    var n: T? = null
    var s: T? = null
    var e: T? = null
    var w: T? = null
    var nw: T? = null
    var ne: T? = null
    var sw: T? = null
    var se: T? = null

    open fun canGoN() = n != null
    open fun canGoNW() = nw != null
    open fun canGoNE() = ne != null
    open fun canGoS() = s != null
    open fun canGoSW() = sw != null
    open fun canGoSE() = se != null
    open fun canGoE() = e != null
    open fun canGoW() = w != null

    /**
     * Gets parent grid
     */
    lateinit var grid: Grid<T>

    /**
     * Return N, E, S, W neighbours filtered by can go function
     */
    open fun neighbours() = listOfNotNull(
        if (canGoN()) n else null,
        if (canGoE()) e else null,
        if (canGoS()) s else null,
        if (canGoW()) w else null,
    )

    /**
     * Returns the Manhattan distance between this cell and the other one
     */
    fun distanceTo(other: T): Long = this.position.distanceTo(other.position)

    /**
     * Returns map with distances to reachable cells from X cell
     * For example:
     *
     * @param neighbourFilter extra filter on neighbours
     *
     * 4321234
     * 321X123
     * 4321234
     */
    fun distanceMap(neighbourFilter: (T) -> Boolean = { true }): Map<T, Int> {
        val distanceMap = mutableMapOf(current() to 0)
        val points = LinkedList(listOf(Pair(this, 0)))

        while (points.isNotEmpty()) {
            val (point, distance) = points.pop()

            point.neighbours()
                .filter { !distanceMap.containsKey(it) && neighbourFilter(it) }
                .forEach {
                    distanceMap[it] = distance + 1
                    points.add(Pair(it, distance + 1))
                }
        }

        return distanceMap
    }

    /**
     * Finding manhattan paths to destination cell
     *
     * @param destination cell
     * @param neighbourFilter predicate for neighbour filtering
     */
    fun manhattanPaths(
        destination: T,
        neighbourFilter: (T) -> Boolean = { true }
    ): List<Path<T>> {
        val shortestPaths = mutableListOf<Path<T>>()
        val distance = this.distanceTo(destination)

        fun generatePaths(currPath: Path<T>, currCell: T) {
            if (currCell == destination) {
                shortestPaths.add(currPath)
                return
            }

            if (currPath.connections + 1 <= distance) {
                currCell.neighbours()
                    .filter(neighbourFilter)
                    .filter { it.distanceTo(destination) < currCell.distanceTo(destination) }
                    .filter { !currPath.contains(it) }
                    .forEach { generatePaths(currPath.add(it), it) }
            }
        }

        generatePaths(Path(listOf(current())), current())
        return shortestPaths
    }

    /**
     * Finding paths to destination cell using A* search algorithm
     *
     * @param destination cell
     * @param maxDistance represents the maximum length of the paths to search
     * @param neighbourFilter predicate for neighbour filtering
     */
    fun shortestPaths(
        destination: T,
        maxDistance: Int = Int.MAX_VALUE,
        neighbourFilter: (T) -> Boolean = { true }
    ): List<Path<T>> {
        if (this.distanceTo(destination) == maxDistance.toLong())
            return manhattanPaths(destination, neighbourFilter)

        val shortestPaths = mutableListOf<Path<T>>()
        var shortestDistance = maxDistance

        val paths = PriorityQueue<Path<T>>(compareBy { it.connections + it.end.distanceTo(destination) })
        paths.add(Path(listOf(current())))

        val visitedPaths = mutableMapOf<T, Int>()

        while (paths.isNotEmpty()) {
            val path = paths.poll()

            // reached destination
            if (destination == path.end) {
                if (shortestDistance == path.connections) {
                    shortestPaths.add(path)
                } else if (shortestDistance > path.connections) {
                    shortestDistance = path.connections
                    shortestPaths.clear()
                    shortestPaths.add(path)
                } else {
                    // path longer than existing
                }
            } else {
                path.end.neighbours()
                    .filter { neighbour -> !path.contains(neighbour) && neighbourFilter(neighbour) }
                    .forEach { neighbour ->
                        val nextPath = path.add(neighbour)

                        if (nextPath.connections <= visitedPaths.getOrDefault(neighbour, Int.MAX_VALUE)) {
                            // next path is at least as good as current best
                            visitedPaths[neighbour] = nextPath.connections
                            // no point to check paths longer then current shortest
                            if (nextPath.connections <= shortestDistance) {
                                paths.add(nextPath)
                            }
                        }
                    }
            }
        }

        return shortestPaths
    }

    /**
     * Find direction from current cell its neighbour.
     */
    fun findNeighbourDirection(neighbour: Cell<T>): Direction = when {
        n === neighbour -> Direction.N
        s === neighbour -> Direction.S
        w === neighbour -> Direction.W
        e === neighbour -> Direction.E
        else -> throw RuntimeException("Unknown direction")
    }

    /**
     * Returns N, NE, E, SE, S, SW, W, NW neighbours filtered by can go function
     */
    open fun neighboursAll() = listOfNotNull(
        if (canGoN()) n else null,
        if (canGoNE()) ne else null,
        if (canGoE()) e else null,
        if (canGoSE()) se else null,
        if (canGoS()) s else null,
        if (canGoSW()) sw else null,
        if (canGoW()) w else null,
        if (canGoNW()) nw else null,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cell<*>) return false
        return this.position == other.position
    }

    override fun hashCode() = position.hashCode()
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

    /**
     * Print grid
     */
    fun print() {
        for (y in 0..<height) {
            for (x in 0..<width) {
                print(cellMap[Position(x, y)]!!.value)
            }
            println()
        }
    }

    /**
     * Return grid as string
     */
    override fun toString(): String {
        val builder = StringBuilder(width * (height + 2))
        for (y in 0..<height) {
            for (x in 0..<width) {
                builder.append(cellMap[Position(x, y)]!!.value)
            }
            if (y < height - 1) {
                builder.append("\r\n")
            }
        }
        return builder.toString()
    }

    /**
     * Returns true if position is located on the edge of the grid
     *
     * @param position to check location
     */
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