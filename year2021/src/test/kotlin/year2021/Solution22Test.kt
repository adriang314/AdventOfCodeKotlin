package year2021

import year2021.SolutionDay22.Cube
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Solution22Test {

    @Test
    fun cubeIntersectionAndInsideTest() {
        val c1 = Cube(0..4, 0..4, 0..4)
        val c2 = Cube(4..4, 4..4, 4..4)
        val c3 = Cube(0..0, 0..0, 0..0)
        val c4 = Cube(3..5, 3..5, 3..5)

        assertTrue { c1.hasIntersection(c2) }
        assertTrue { c1.hasIntersection(c3) }
        assertTrue { c1.hasIntersection(c4) }
        assertTrue { c2.hasIntersection(c1) }
        assertTrue { c3.hasIntersection(c1) }
        assertTrue { c4.hasIntersection(c1) }

        assertTrue { c2.inside(c1) }
        assertTrue { c3.inside(c1) }
        assertFalse { c4.inside(c1) }
        assertFalse { c1.inside(c2) }
        assertFalse { c1.inside(c3) }
        assertFalse { c1.inside(c4) }

        val c5 = Cube(4..7, 5..5, 5..5)
        val c6 = Cube(5..5, 5..5, 5..5)
        val c7 = Cube(0..4, 0..4, 5..5)

        assertFalse { c1.hasIntersection(c5) }
        assertFalse { c1.hasIntersection(c6) }
        assertFalse { c1.hasIntersection(c7) }
        assertFalse { c5.hasIntersection(c1) }
        assertFalse { c6.hasIntersection(c1) }
        assertFalse { c7.hasIntersection(c1) }
    }

    @Test
    fun cubeRemoveTest() {
        val c1 = Cube(0..4, 0..4, 0..4)
        val c2 = Cube(4..4, 4..4, 4..4)

        val removed1 = c1.remove(c2)
        assertEquals(3, removed1.size)
        assertEquals(c1.size() - 1, removed1.sumOf { it.size() })

        val c3 = Cube(0..4, 0..4, 0..4)
        val c4 = Cube(2..2, 2..2, 2..2)

        val removed2 = c3.remove(c4)
        assertEquals(6, removed2.size)
        assertEquals(c1.size() - 1, removed2.sumOf { it.size() })

        val c5 = Cube(0..4, 0..4, 0..4)
        val c6 = Cube(4..8, 4..8, 4..8)

        val removed3 = c5.remove(c6)
        assertEquals(3, removed3.size)
        assertEquals(c5.size() - 1, removed3.sumOf { it.size() })


        val c7 = Cube(0..4, 0..4, 0..4)
        val c8 = Cube(3..8, 2..8, 1..8)

        val removed4 = c7.remove(c8)
        assertEquals(3, removed4.size)
        assertEquals(c7.size() - 24, removed4.sumOf { it.size() })
    }
}