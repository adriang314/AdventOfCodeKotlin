import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import solution.SolutionDay24.*
import kotlin.test.assertEquals

class Solution24Test {

    @Test
    fun test() {
//        Hailstone A: 19, 13, 30 @ -2, 1, -2
//        Hailstone B: 18, 19, 22 @ -1, -1, -2
//        Hailstones' paths will cross inside the test area (at x=14.333, y=15.333).
        var hailstoneA = HailStone(ThreeDPoint(19, 13, 30), VelocityChange(-2, 1, -2))
        var hailstoneB = HailStone(ThreeDPoint(18, 19, 22), VelocityChange(-1, -1, -2))
        var cross = Cross(hailstoneA, hailstoneB)
        assertFalse(hailstoneA.isParallelTo(hailstoneB))
        assertEquals(false, cross.inPast)
        assertTrue(cross.inTestArea(7, 27, 7, 27))

//        Hailstone A: 19, 13, 30 @ -2, 1, -2
//        Hailstone B: 20, 25, 34 @ -2, -2, -4
//        Hailstones' paths will cross inside the test area (at x=11.667, y=16.667).
        hailstoneA = HailStone(ThreeDPoint(19, 13, 30), VelocityChange(-2, 1, -2))
        hailstoneB = HailStone(ThreeDPoint(20, 25, 34), VelocityChange(-2, -2, -4))
        cross = Cross(hailstoneA, hailstoneB)
        assertFalse(hailstoneA.isParallelTo(hailstoneB))
        assertEquals(false, cross.inPast)
        assertTrue(cross.inTestArea(7, 27, 7, 27))

//        Hailstone A: 19, 13, 30 @ -2, 1, -2
//        Hailstone B: 12, 31, 28 @ -1, -2, -1
//        Hailstones' paths will cross outside the test area (at x=6.2, y=19.4).
        hailstoneA = HailStone(ThreeDPoint(19, 13, 30), VelocityChange(-2, 1, -2))
        hailstoneB = HailStone(ThreeDPoint(12, 31, 28), VelocityChange(-1, -2, -1))
        cross = Cross(hailstoneA, hailstoneB)
        assertEquals(false, cross.inPast)
        assertFalse(hailstoneA.isParallelTo(hailstoneB))
        assertFalse(cross.inTestArea(7, 27, 7, 27))

//        Hailstone A: 19, 13, 30 @ -2, 1, -2
//        Hailstone B: 20, 19, 15 @ 1, -5, -3
//        Hailstones' paths crossed in the past for hailstone A.
        hailstoneA = HailStone(ThreeDPoint(19, 13, 30), VelocityChange(-2, 1, -2))
        hailstoneB = HailStone(ThreeDPoint(20, 19, 15), VelocityChange(1, -5, -3))
        cross = Cross(hailstoneA, hailstoneB)
        assertEquals(true, cross.inPast)
        assertFalse(hailstoneA.isParallelTo(hailstoneB))
        assertTrue(cross.inTestArea(7, 27, 7, 27))

//        Hailstone A: 18, 19, 22 @ -1, -1, -2
//        Hailstone B: 20, 25, 34 @ -2, -2, -4
//        Hailstones' paths are parallel; they never intersect.
        hailstoneA = HailStone(ThreeDPoint(18, 19, 22), VelocityChange(-1, -1, -2))
        hailstoneB = HailStone(ThreeDPoint(20, 25, 34), VelocityChange(-2, -2, -4))
        cross = Cross(hailstoneA, hailstoneB)
        assertEquals(null, cross.inPast)
        assertTrue(hailstoneA.isParallelTo(hailstoneB))
        assertFalse(cross.inTestArea(7, 27, 7, 27))

//        Hailstone A: 18, 19, 22 @ -1, -1, -2
//        Hailstone B: 12, 31, 28 @ -1, -2, -1
//        Hailstones' paths will cross outside the test area (at x=-6, y=-5).
        hailstoneA = HailStone(ThreeDPoint(18, 19, 22), VelocityChange(-1, -1, -2))
        hailstoneB = HailStone(ThreeDPoint(12, 31, 28), VelocityChange(-1, -2, -1))
        cross = Cross(hailstoneA, hailstoneB)
        assertEquals(false, cross.inPast)
        assertFalse(hailstoneA.isParallelTo(hailstoneB))
        assertFalse(cross.inTestArea(7, 27, 7, 27))

//        Hailstone A: 18, 19, 22 @ -1, -1, -2
//        Hailstone B: 20, 19, 15 @ 1, -5, -3
//        Hailstones' paths crossed in the past for both hailstones.
        hailstoneA = HailStone(ThreeDPoint(18, 19, 22), VelocityChange(-1, -1, -2))
        hailstoneB = HailStone(ThreeDPoint(20, 19, 15), VelocityChange(1, -5, -3))
        cross = Cross(hailstoneA, hailstoneB)
        assertEquals(true, cross.inPast)
        assertFalse(hailstoneA.isParallelTo(hailstoneB))
        assertTrue(cross.inTestArea(7, 27, 7, 27))

//        Hailstone A: 20, 25, 34 @ -2, -2, -4
//        Hailstone B: 12, 31, 28 @ -1, -2, -1
//        Hailstones' paths will cross outside the test area (at x=-2, y=3).
        hailstoneA = HailStone(ThreeDPoint(20, 25, 34), VelocityChange(-2, -2, -4))
        hailstoneB = HailStone(ThreeDPoint(12, 31, 28), VelocityChange(-1, -2, -1))
        cross = Cross(hailstoneA, hailstoneB)
        assertEquals(false, cross.inPast)
        assertFalse(hailstoneA.isParallelTo(hailstoneB))
        assertFalse(cross.inTestArea(7, 27, 7, 27))

//        Hailstone A: 20, 25, 34 @ -2, -2, -4
//        Hailstone B: 20, 19, 15 @ 1, -5, -3
//        Hailstones' paths crossed in the past for hailstone B.
        hailstoneA = HailStone(ThreeDPoint(20, 25, 34), VelocityChange(-2, -2, -4))
        hailstoneB = HailStone(ThreeDPoint(20, 19, 15), VelocityChange(1, -5, -3))
        cross = Cross(hailstoneA, hailstoneB)
        assertEquals(true, cross.inPast)
        assertFalse(hailstoneA.isParallelTo(hailstoneB))
        assertTrue(cross.inTestArea(7, 27, 7, 27))

//        Hailstone A: 12, 31, 28 @ -1, -2, -1
//        Hailstone B: 20, 19, 15 @ 1, -5, -3
//        Hailstones' paths crossed in the past for both hailstones.
        hailstoneA = HailStone(ThreeDPoint(12, 31, 28), VelocityChange(-1, -2, -1))
        hailstoneB = HailStone(ThreeDPoint(20, 19, 15), VelocityChange(1, -5, -3))
        cross = Cross(hailstoneA, hailstoneB)
        assertEquals(true, cross.inPast)
        assertFalse(hailstoneA.isParallelTo(hailstoneB))
        assertFalse(cross.inTestArea(7, 27, 7, 27))
    }
}