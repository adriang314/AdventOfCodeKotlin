package common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IntRangeTest {

    @Test
    fun rangeInnerTest() {
        assertEquals((3..10), (1..10).intersection(3..10))
        assertEquals((3..5), (1..10).intersection(3..5))
        assertEquals((1..5), (1..10).intersection(-3..5))
        assertEquals((1..5), (1..10).intersection(1..5))
        assertEquals((1..10), (1..10).intersection(-3..15))
    }

    @Test
    fun insideTest() {
        assertTrue { (1..10).inside(1..10) }
        assertTrue { (1..10).inside(-1..10) }
        assertTrue { (1..10).inside(-1..11) }

        assertFalse { (1..10).inside(2..10) }
        assertFalse { (1..10).inside(1..9) }
        assertFalse { (1..10).inside(-1..9) }
    }
}