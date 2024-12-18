package year2021

import year2021.SolutionDay18.SnailNumber
import kotlin.test.Test
import kotlin.test.assertEquals

class Solution18Test {

    @Test
    fun test1() {
        val text = "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"
        val sn = SnailNumber.fromText(text)

        assertEquals(0, sn.left!!.left!!.right!!.left!!.leftNeighbour()?.value)
        assertEquals(5, sn.left!!.left!!.right!!.left!!.rightNeighbour()?.value)
        assertEquals(0, sn.right!!.left!!.left!!.left!!.leftNeighbour()?.value)
        assertEquals(5, sn.right!!.left!!.left!!.left!!.rightNeighbour()?.value)
        assertEquals(null, sn.left!!.left!!.left!!.leftNeighbour()?.value)
        assertEquals(4, sn.left!!.left!!.left!!.rightNeighbour()?.value)

        assertEquals(1, sn.level())

        assertEquals(2, sn.left!!.level())
        assertEquals(2, sn.right!!.level())

        assertEquals(3, sn.left!!.left!!.level())
        assertEquals(3, sn.left!!.right!!.level())
        assertEquals(3, sn.right!!.left!!.level())
        assertEquals(3, sn.right!!.right!!.level())

        assertEquals(4, sn.left!!.left!!.left!!.level())
        assertEquals(4, sn.left!!.left!!.right!!.level())
        assertEquals(4, sn.left!!.right!!.left!!.level())
        assertEquals(4, sn.left!!.right!!.right!!.level())
        assertEquals(4, sn.right!!.left!!.left!!.level())
        assertEquals(4, sn.right!!.left!!.right!!.level())
        assertEquals(4, sn.right!!.right!!.left!!.level())
        assertEquals(4, sn.right!!.right!!.right!!.level())

        assertEquals(5, sn.left!!.left!!.right!!.left!!.level())
        assertEquals(5, sn.left!!.left!!.right!!.right!!.level())
        assertEquals(5, sn.right!!.left!!.left!!.left!!.level())
        assertEquals(5, sn.right!!.left!!.left!!.right!!.level())
        assertEquals(5, sn.right!!.left!!.right!!.left!!.level())
        assertEquals(5, sn.right!!.left!!.right!!.right!!.level())
    }

    @Test
    fun test2() {
        val s1 = "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"
        val s2 = "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]"
        val s3 = "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"
        val s4 = "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]"
        val s5 = "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]"
        val s6 = "[7,[5,[[3,8],[1,4]]]]"
        val s7 = "[[2,[2,2]],[8,[8,1]]]"
        val s8 = "[2,9]"
        val s9 = "[1,[[[9,3],9],[[9,0],[0,7]]]]"
        val s10 = "[[[5,[7,4]],7],1]"
        val s11 = "[[[[4,2],2],6],[8,7]]"

        assertEquals(s1, SnailNumber.fromText(s1).toText())
        assertEquals(s2, SnailNumber.fromText(s2).toText())
        assertEquals(s3, SnailNumber.fromText(s3).toText())
        assertEquals(s4, SnailNumber.fromText(s4).toText())
        assertEquals(s5, SnailNumber.fromText(s5).toText())
        assertEquals(s6, SnailNumber.fromText(s6).toText())
        assertEquals(s7, SnailNumber.fromText(s7).toText())
        assertEquals(s8, SnailNumber.fromText(s8).toText())
        assertEquals(s9, SnailNumber.fromText(s9).toText())
        assertEquals(s10, SnailNumber.fromText(s10).toText())
        assertEquals(s11, SnailNumber.fromText(s11).toText())
    }

    @Test
    fun test4() {
        assertEquals(143, SnailNumber.fromText("[[1,2],[[3,4],5]]").magnitude())
        assertEquals(1384, SnailNumber.fromText("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]").magnitude())
        assertEquals(445, SnailNumber.fromText("[[[[1,1],[2,2]],[3,3]],[4,4]]").magnitude())
        assertEquals(791, SnailNumber.fromText("[[[[3,0],[5,3]],[4,4]],[5,5]]").magnitude())
        assertEquals(1137, SnailNumber.fromText("[[[[5,0],[7,4]],[5,5]],[6,6]]").magnitude())
        assertEquals(3488, SnailNumber.fromText("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").magnitude())
    }

    @Test
    fun test5() {
        val sn = SnailNumber.fromText("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]")
        sn.reduce()
        assertEquals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", sn.toText())
    }

    @Test
    fun test6() {
        val sn1 = SnailNumber.fromText("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]")
        val sn2 = SnailNumber.fromText("[7,[5,[[3,8],[1,4]]]]")
        val sum = sn1.addNumber(sn2)
        sum.reduce()
        assertEquals("[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]", sum.toText())
    }

    @Test
    fun test7() {
        val sn1 = SnailNumber.fromText("[[[[[9,8],1],2],3],4]")
        sn1.reduce()
        assertEquals("[[[[0,9],2],3],4]", sn1.toText())

        val sn2 = SnailNumber.fromText("[7,[6,[5,[4,[3,2]]]]]")
        sn2.reduce()
        assertEquals("[7,[6,[5,[7,0]]]]", sn2.toText())

        val sn3 = SnailNumber.fromText("[[6,[5,[4,[3,2]]]],1]")
        sn3.reduce()
        assertEquals("[[6,[5,[7,0]]],3]", sn3.toText())

        val sn4 = SnailNumber.fromText("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]")
        sn4.reduce()
        assertEquals("[[3,[2,[8,0]]],[9,[5,[7,0]]]]", sn4.toText())

        val sn5 = SnailNumber.fromText("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
        sn5.reduce()
        assertEquals("[[3,[2,[8,0]]],[9,[5,[7,0]]]]", sn5.toText())
    }

    @Test
    fun test8() {
        val sn1 = SnailNumber.fromText("[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]")
        val sn2 = SnailNumber.fromText("[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]")
        val sum = sn1.addNumber(sn2)
        sum.reduce()
        assertEquals("[[[[7,8],[6,6]],[[6,0],[7,7]]],[[[7,8],[8,8]],[[7,9],[0,6]]]]", sum.toText())
        assertEquals(3993, sum.magnitude())
    }
}