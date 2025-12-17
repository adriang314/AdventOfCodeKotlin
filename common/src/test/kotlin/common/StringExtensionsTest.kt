package common

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StringExtensionsTest {

    @Test
    fun swapAtIndexTest() {
        assertEquals("dbca", "abcd".swapAtIndex(0, 3))
        assertEquals("dbca", "abcd".swapAtIndex(3, 0))
        assertEquals("acbd", "abcd".swapAtIndex(1, 2))
        assertEquals("acbd", "abcd".swapAtIndex(2, 1))
        assertEquals("afcdebgh", "abcdefgh".swapAtIndex(1, 5))
        assertEquals("afcdebgh", "abcdefgh".swapAtIndex(5, 1))
    }

    @Test
    fun swapFirstLettersTest() {
        assertEquals("dbca", "abcd".swapFirstLetters('a', 'd'))
        assertEquals("dbca", "abcd".swapFirstLetters('d', 'a'))
        assertEquals("acbd", "abcd".swapFirstLetters('b', 'c'))
        assertEquals("acbd", "abcd".swapFirstLetters('c', 'b'))
        assertEquals("afcdebgh", "abcdefgh".swapFirstLetters('b', 'f'))
        assertEquals("afcdebgh", "abcdefgh".swapFirstLetters('f', 'b'))
    }

    @Test
    fun shiftRightTest() {
        assertEquals("dabc", "abcd".shiftRight(1))
        assertEquals("dabc", "abcd".shiftRight(5))
        assertEquals("abcd", "abcd".shiftRight(0))
        assertEquals("abcd", "abcd".shiftRight(4))
        assertEquals("habcdefg", "abcdefgh".shiftRight(1))
        assertEquals("ghabcdef", "abcdefgh".shiftRight(2))
    }

    @Test
    fun shiftLeftTest() {
        assertEquals("bcda", "abcd".shiftLeft(1))
        assertEquals("bcda", "abcd".shiftLeft(5))
        assertEquals("abcd", "abcd".shiftLeft(0))
        assertEquals("abcd", "abcd".shiftLeft(4))
        assertEquals("bcdefgha", "abcdefgh".shiftLeft(1))
        assertEquals("cdefghab", "abcdefgh".shiftLeft(2))
    }

    @Test
    fun reverseTest() {
        assertEquals("acbd", "abcd".reverse(1, 2))
        assertEquals("dcba", "abcd".reverse(0, 3))
        assertEquals("abcd", "abcd".reverse(0, 0))
        assertEquals("abdc", "abcd".reverse(2, 3))
        assertEquals("afedcbgh", "abcdefgh".reverse(1, 5))
        assertEquals("dcbaefgh", "abcdefgh".reverse(0, 3))
    }

    @Test
    fun moveTest() {
        assertEquals("acbd", "abcd".move(1, 2))
        assertEquals("acbd", "abcd".move(2, 1))
        assertEquals("bcda", "abcd".move(0, 3))
        assertEquals("dabc", "abcd".move(3, 0))
        assertEquals("abcd", "abcd".move(0, 0))
        assertEquals("abdc", "abcd".move(2, 3))
        assertEquals("abdc", "abcd".move(3, 2))
        assertEquals("acdefbgh", "abcdefgh".move(1, 5))
        assertEquals("afbcdegh", "abcdefgh".move(5, 1))
        assertEquals("bcdaefgh", "abcdefgh".move(0, 3))
        assertEquals("dabcefgh", "abcdefgh".move(3, 0))
    }

    @Test
    fun adjustIndexTest() {
        assertEquals(3, "abcd".adjustIndex(-5))
        assertEquals(3, "abcd".adjustIndex(-1))
        assertEquals(0, "abcd".adjustIndex(0))
        assertEquals(3, "abcd".adjustIndex(3))
        assertEquals(1, "abcd".adjustIndex(5))
        assertEquals(1, "abcd".adjustIndex(9))
    }
}