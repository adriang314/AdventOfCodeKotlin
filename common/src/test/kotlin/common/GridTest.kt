package common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GridTest {
    private val gridToTest = """
        |#######
        |#A....#
        |#...#C#
        |#..B###
        |#...#D#
        |#######
    """.trimMargin()

    private val grid = Grid(gridToTest) { c, position -> Point(position, c) }
    private val aPoint = grid.cells.single { it.value == 'A' }
    private val bPoint = grid.cells.single { it.value == 'B' }
    private val cPoint = grid.cells.single { it.value == 'C' }
    private val dPoint = grid.cells.single { it.value == 'D' }

    @Test
    fun neighboursTest() {
        assertEquals(listOf(aPoint.e!!, aPoint.s!!), aPoint.neighbours())
        assertEquals(listOf(bPoint.n!!, bPoint.s!!, bPoint.w!!), bPoint.neighbours())
        assertEquals(listOf(cPoint.n!!), cPoint.neighbours())
        assertEquals(emptyList(), dPoint.neighbours())
    }

    @Test
    fun findNeighbourDirectionTest() {
        assertEquals(Direction.S, aPoint.findNeighbourDirection(aPoint.s!!))
        assertEquals(Direction.E, aPoint.findNeighbourDirection(aPoint.e!!))
        assertEquals(Direction.N, cPoint.findNeighbourDirection(cPoint.n!!))
        assertEquals(Direction.W, bPoint.findNeighbourDirection(bPoint.w!!))
    }

    @Test
    fun manhattanPathsTest() {
        val abPaths = aPoint.manhattanPaths(bPoint)
        val acPaths = aPoint.manhattanPaths(cPoint)
        val bcPaths = bPoint.manhattanPaths(cPoint)
        assert(abPaths.size == 6)
        assert(acPaths.size == 1)
        assert(bcPaths.isEmpty())
    }

    @Test
    fun shortestPathsTest() {
        val abPaths = aPoint.shortestPaths(bPoint)
        val acPaths = aPoint.shortestPaths(cPoint)
        val adPaths = aPoint.shortestPaths(dPoint)

        assert(abPaths.size == 6)
        assert(acPaths.size == 1)
        assert(adPaths.isEmpty())

        val baPaths = bPoint.shortestPaths(aPoint)
        val bcPaths = bPoint.shortestPaths(cPoint)
        val bdPaths = bPoint.shortestPaths(dPoint)

        assert(baPaths.size == 6)
        assert(bcPaths.size == 1)
        assert(bdPaths.isEmpty())

        val caPaths = cPoint.shortestPaths(aPoint)
        val cbPaths = cPoint.shortestPaths(bPoint)
        val cdPaths = cPoint.shortestPaths(dPoint)

        assert(caPaths.size == 1)
        assert(cbPaths.size == 1)
        assert(cdPaths.isEmpty())

        val daPaths = dPoint.shortestPaths(aPoint)
        val dbPaths = dPoint.shortestPaths(bPoint)
        val dcPaths = dPoint.shortestPaths(cPoint)

        assert(daPaths.isEmpty())
        assert(dbPaths.isEmpty())
        assert(dcPaths.isEmpty())
    }

    @Test
    fun distanceMapTest() {
        /*
            #######
            #01234#
            #123#5#
            #234###
            #345#0#
            #######
        */
        val aDistanceMap = aPoint.distanceMap()

        assertEquals(15, aDistanceMap.size)
        assertEquals(0, aDistanceMap[aPoint])
        assertEquals(1, aDistanceMap[aPoint.s!!])
        assertEquals(1, aDistanceMap[aPoint.e!!])
        assertEquals(2, aDistanceMap[aPoint.e!!.e!!])
        assertEquals(2, aDistanceMap[aPoint.s!!.e!!])
        assertEquals(2, aDistanceMap[aPoint.s!!.s!!])
        assertEquals(3, aDistanceMap[aPoint.e!!.e!!.e!!])
        assertEquals(3, aDistanceMap[aPoint.s!!.e!!.e!!])
        assertEquals(3, aDistanceMap[aPoint.s!!.s!!.e!!])
        assertEquals(3, aDistanceMap[aPoint.s!!.s!!.s!!])
        assertEquals(4, aDistanceMap[aPoint.e!!.e!!.e!!.e!!])
        assertEquals(4, aDistanceMap[aPoint.s!!.s!!.e!!.e!!])
        assertEquals(4, aDistanceMap[aPoint.s!!.s!!.s!!.e!!])
        assertEquals(5, aDistanceMap[aPoint.e!!.e!!.e!!.e!!.s!!])
        assertEquals(5, aDistanceMap[aPoint.s!!.s!!.s!!.e!!.e!!])

        val dDistanceMap = dPoint.distanceMap()
        assertEquals(1, dDistanceMap.size)
        assertEquals(0, dDistanceMap[dPoint])
    }

    @Test
    fun isPositionOnEdgeTest() {
        assertEquals(true, grid.isPositionOnEdge(Position(0, 0)))
        assertEquals(true, grid.isPositionOnEdge(Position(1, 0)))
        assertEquals(true, grid.isPositionOnEdge(Position(0, 1)))
        assertEquals(true, grid.isPositionOnEdge(Position(0, 5)))
        assertEquals(true, grid.isPositionOnEdge(Position(6, 0)))
        assertEquals(true, grid.isPositionOnEdge(Position(6, 5)))
    }

    @Test
    fun pathComparisonTest() {
        val path1 = Path(listOf(aPoint, aPoint.e!!, aPoint.e!!.e!!, aPoint.e!!.e!!.s!!, bPoint))
        val path2 = Path(listOf(aPoint, aPoint.e!!, aPoint.e!!.s!!, aPoint.e!!.s!!.e!!, bPoint))
        val path3 = Path(listOf(aPoint, aPoint.e!!, aPoint.e!!.s!!, aPoint.e!!.s!!.s!!, bPoint))
        val path4 = Path(listOf(aPoint, aPoint.s!!, aPoint.s!!.e!!, aPoint.s!!.e!!.e!!, bPoint))
        val path5 = Path(listOf(aPoint, aPoint.s!!, aPoint.s!!.e!!, aPoint.s!!.e!!.s!!, bPoint))
        val path6 = Path(listOf(aPoint, aPoint.s!!, aPoint.s!!.s!!, aPoint.s!!.s!!.e!!, bPoint))

        assertTrue { path1 < path2 }
        assertTrue { path1 < path3 }
        assertTrue { path1 < path4 }
        assertTrue { path1 < path5 }
        assertTrue { path1 < path6 }
        assertTrue { path2 < path3 }
        assertTrue { path2 < path4 }
        assertTrue { path2 < path5 }
        assertTrue { path2 < path6 }
        assertTrue { path3 < path4 }
        assertTrue { path3 < path5 }
        assertTrue { path3 < path6 }
        assertTrue { path4 < path5 }
        assertTrue { path4 < path6 }
        assertTrue { path5 < path6 }
    }

    @Test
    fun subPathTest() {
        val path = Path(listOf(aPoint, aPoint.e!!, aPoint.e!!.e!!, aPoint.e!!.e!!.s!!, bPoint))
        val subPath = path.subPath(aPoint.e!!.e!!)!!

        assertEquals(3, subPath.cells.size)
        assertEquals(2, subPath.connections)
        assertTrue { subPath.cells[0] == aPoint.e!!.e!! }
        assertTrue { subPath.cells[1] == aPoint.e!!.e!!.s!! }
        assertTrue { subPath.cells[2] == bPoint }
    }

    private class Point(position: Position, value: Char) : Cell<Point>(position, value) {
        val isSpace = value != '#'

        override fun canGoN() = n?.isSpace == true
        override fun canGoS() = s?.isSpace == true
        override fun canGoW() = w?.isSpace == true
        override fun canGoE() = e?.isSpace == true

        override fun toString() = position.toString()
    }
}