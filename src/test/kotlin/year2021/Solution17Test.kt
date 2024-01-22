package year2021

import year2021.SolutionDay17.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Solution17Test {

    @Test
    fun test() {
        val target = Target(20..30, -10..-5)

        val result0 = Probe(Velocity(9, 0)).shoot(Position(0, 0), target)
        assertEquals(true, result0.hitTarget)

        val result1 = Probe(Velocity(6, 9)).shoot(Position(0, 0), target)
        assertEquals(true, result1.hitTarget)
        assertEquals(45, result1.maxHeight)

        val result2 = Probe(Velocity(7, 2)).shoot(Position(0, 0), target)
        assertEquals(true, result2.hitTarget)
        assertEquals(3, result2.maxHeight)

        val result3 = Probe(Velocity(6, 3)).shoot(Position(0, 0), target)
        assertEquals(true, result3.hitTarget)
        assertEquals(6, result3.maxHeight)
    }

    private val hittingVelocities = "23,-10  25,-9   27,-5   29,-6   22,-6   21,-7   9,0     27,-7   24,-5\n" +
            "25,-7   26,-6   25,-5   6,8     11,-2   20,-5   29,-10  6,3     28,-7\n" +
            "8,0     30,-6   29,-8   20,-10  6,7     6,4     6,1     14,-4   21,-6\n" +
            "26,-10  7,-1    7,7     8,-1    21,-9   6,2     20,-7   30,-10  14,-3\n" +
            "20,-8   13,-2   7,3     28,-8   29,-9   15,-3   22,-5   26,-8   25,-8\n" +
            "25,-6   15,-4   9,-2    15,-2   12,-2   28,-9   12,-3   24,-6   23,-7\n" +
            "25,-10  7,8     11,-3   26,-7   7,1     23,-9   6,0     22,-10  27,-6\n" +
            "8,1     22,-8   13,-4   7,6     28,-6   11,-4   12,-4   26,-9   7,4\n" +
            "24,-10  23,-8   30,-8   7,0     9,-1    10,-1   26,-5   22,-9   6,5\n" +
            "7,5     23,-6   28,-10  10,-2   11,-1   20,-9   14,-2   29,-7   13,-3\n" +
            "23,-5   24,-8   27,-9   30,-7   28,-5   21,-10  7,9     6,6     21,-5\n" +
            "27,-10  7,2     30,-9   21,-8   22,-7   24,-9   20,-6   6,9     29,-5\n" +
            "8,-2    27,-8   30,-5   24,-7"

    @Test
    fun test2() {
        val target = Target(20..30, -10..-5)
        val regex = Regex("(-?\\d+),(-?\\d+)", RegexOption.MULTILINE)

        val velocities = regex.findAll(hittingVelocities).map {
            val (x, y) = it.destructured
            Velocity(x.toInt(), y.toInt())
        }.toList()

        val missed = velocities.map {
            Pair(it, Probe(it).shoot(Position(0, 0), target))
        }.filter { !it.second.hitTarget }

        assertTrue(missed.isEmpty())
    }
}