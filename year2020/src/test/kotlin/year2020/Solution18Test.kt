package year2020

import year2020.SolutionDay18.*
import kotlin.test.Test
import kotlin.test.assertEquals

class Solution18Test {

    @Test
    fun test() {
        assertEquals(71L, Expression("1 + 2 * 3 + 4 * 5 + 6").eval(false))
        assertEquals(51L, Expression("1 + (2 * 3) + (4 * (5 + 6))").eval(false))
        assertEquals(26L, Expression("2 * 3 + (4 * 5)").eval(false))
        assertEquals(437L, Expression("5 + (8 * 3 + 9 + 3 * 4 * 3)").eval(false))
        assertEquals(12240L, Expression("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))").eval(false))
        assertEquals(13632L, Expression("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2").eval(false))

        assertEquals(231L, Expression("1 + 2 * 3 + 4 * 5 + 6").eval(true))
        assertEquals(51L, Expression("1 + (2 * 3) + (4 * (5 + 6))").eval(true))
        assertEquals(46L, Expression("2 * 3 + (4 * 5)").eval(true))
        assertEquals(1445L, Expression("5 + (8 * 3 + 9 + 3 * 4 * 3)").eval(true))
        assertEquals(669060L, Expression("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))").eval(true))
        assertEquals(23340L, Expression("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2").eval(true))
        assertEquals(524566748160L, Expression("((4 * 4 * 9 * 7) * (8 + 9 + 6 * 9 + 3) * 8 + 6 * 7) * 2 + 3 * 3 + 5 * (3 + (6 + 8) + (9 + 4) + 7 * 4 + 9)").eval(true))
        assertEquals(30L, Expression("4 + 9 + 8 + 9").eval(true))
        assertEquals(10L, Expression("4 + 6").eval(true))
        assertEquals(248529420288L, Expression("(2 + (2 * 9 + 2 + 6) * 4 * (7 + 5 * 6 * 6)) * (3 * 3 + (5 * 8 + 9 * 3 * 3) * 4 * 6 + 3) + (2 + 2 * 7 + 2 * 8) * 6 + 6 * 4").eval(true))
        assertEquals(69709626685440L, Expression("(7 + 6 + 9 + 6 * (7 * 8 + 9 + 4)) + (6 * 6 * 8 * 5 * (7 * 8)) * ((8 + 6 + 8 + 4) + (9 * 6) * 6 * 7) * 4 * (9 * 4 + 9 * 3 + (7 + 2 + 7 * 2 * 8 * 2) + 8) + 5").eval(true))
        assertEquals(14868L, Expression("((6 + 9 + 8) * 2 + 8) + 6 * 7 * 9").eval(true))
        assertEquals(1792L, Expression("8 * 6 + 8 * 4 * 4").eval(true))
        assertEquals(20L, Expression("3 + 3 + (3 + (4 + 2)) + 5").eval(true))
        assertEquals(11L, Expression("(5 + 2) + 4").eval(true))
        assertEquals(300L, Expression("3 * 6 + 8 + 6 + (5 * 8 + 8)").eval(true))
    }
}