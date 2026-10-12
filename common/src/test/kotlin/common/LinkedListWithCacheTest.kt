package common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class LinkedListWithCacheTest {

    @Test
    fun emptyAndIterableConstructorsExposeExpectedState() {
        val empty = LinkedListWithCache<Int>()
        assertEquals(0, empty.size())
        assertNull(empty.getFirst())
        assertNull(empty.getLast())

        val list = LinkedListWithCache(listOf(1, 2, 3))
        assertEquals(listOf(1, 2, 3), list.toList())
        assertEquals(3, list.size())
        assertEquals(1, list.getFirst())
        assertEquals(3, list.getLast())
    }

    @ParameterizedTest
    @CsvSource(
        "1, 0, 1, 1",
        "1, 1, 4, 2",
        "1, 4, 1, 1",
        "2, 2, 4, 4",
        "4, 5, 3, 1"
    )
    fun navigationWrapsAround(element: Int, offset: Int, expectedBefore: Int, expectedAfter: Int) {
        val list = LinkedListWithCache(listOf(1, 2, 3, 4))

        assertEquals(expectedBefore, list.getBefore(element, offset))
        assertEquals(expectedAfter, list.getAfter(element, offset))
    }

    @Test
    fun insertionMaintainsOrderAndCachedMembership() {
        val list = LinkedListWithCache(listOf(2, 4))
        list.addFirst(1)
        list.addLast(6)
        list.addBefore(3, 4)
        list.addAfter(5, 4)

        assertEquals(listOf(1, 2, 3, 4, 5, 6), list.toList())
        assertEquals(6, list.size())
        assertTrue(3 in list)
        assertEquals(4, list.getAfter(3, 1))
    }

    @Test
    fun removeOperationsUpdateOrderEndsSizeAndCache() {
        val list = LinkedListWithCache(listOf(1, 2, 3, 4))

        list.removeFirst()
        assertEquals(listOf(2, 3, 4), list.toList())
        assertFalse(1 in list)

        list.removeLast()
        assertEquals(listOf(2, 3), list.toList())
        assertFalse(4 in list)

        assertEquals(2, list.remove(2))
        assertEquals(listOf(3), list.toList())
        assertFalse(2 in list)

        assertEquals(3, list.removeAfter(3, 0))
        assertEquals(0, list.size())
        assertNull(list.getFirst())
        assertNull(list.getLast())
        assertFalse(3 in list)
    }

    @ParameterizedTest
    @CsvSource(
        "1, 0, 1",
        "1, 1, 2",
        "4, 1, 1",
        "3, 5, 4"
    )
    fun removeAfterRemovesTheWrappedOffset(element: Int, offset: Int, expectedRemoved: Int) {
        val list = LinkedListWithCache(listOf(1, 2, 3, 4))

        assertEquals(expectedRemoved, list.removeAfter(element, offset))
        assertFalse(expectedRemoved in list)
        assertEquals(3, list.size())
    }

    private fun LinkedListWithCache<Int>.toList(): List<Int> {
        val elements = mutableListOf<Int>()
        forEach(elements::add)
        return elements
    }
}
