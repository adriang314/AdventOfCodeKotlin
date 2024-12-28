package common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PositionTest {

    @Test
    fun compareToTest() {
        assertTrue { Position(0, 0) < Position(0, 1) }
        assertTrue { Position(0, 0) < Position(1, 0) }
        assertTrue { Position(1, 0) < Position(0, 1) }
        assertTrue { Position(1, 1) > Position(0, 1) }
    }

    @Test
    fun distanceToTest() {
        assertEquals(2, Position(0, 0).distanceTo(Position(1, 1)))
        assertEquals(2, Position(1, 1).distanceTo(Position(0, 0)))
    }
}