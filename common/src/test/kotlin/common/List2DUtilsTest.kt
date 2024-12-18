package common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class List2DUtilsTest {
    private val array1 = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9))
    private val array2 = listOf(listOf(1, 2, 3, 4), listOf(5, 6, 7, 8), listOf(9, 10, 11, 12), listOf(13, 14, 15, 16))

    @Test
    fun rotateTest() {
        val expected1 = listOf(listOf(7, 4, 1), listOf(8, 5, 2), listOf(9, 6, 3))
        assertEquals(expected1, List2D.rotateRight(array1))

        val expected2 = listOf(listOf(13, 9, 5, 1), listOf(14, 10, 6, 2), listOf(15, 11, 7, 3), listOf(16, 12, 8, 4))
        assertEquals(expected2, List2D.rotateRight(array2))
    }

    @Test
    fun flipHorizontallyTest() {
        val expected1 = listOf(listOf(7, 8, 9), listOf(4, 5, 6), listOf(1, 2, 3))
        assertEquals(expected1, List2D.flipHorizontally(array1))

        val expected2 = listOf(listOf(13, 14, 15, 16), listOf(9, 10, 11, 12), listOf(5, 6, 7, 8), listOf(1, 2, 3, 4))
        assertEquals(expected2, List2D.flipHorizontally(array2))
    }

    @Test
    fun flipVerticallyTest() {
        val expected1 = listOf(listOf(3, 2, 1), listOf(6, 5, 4), listOf(9, 8, 7))
        assertEquals(expected1, List2D.flipVertically(array1))

        val expected2 = listOf(listOf(4, 3, 2, 1), listOf(8, 7, 6, 5), listOf(12, 11, 10, 9), listOf(16, 15, 14, 13))
        assertEquals(expected2, List2D.flipVertically(array2))
    }
}
