import org.junit.jupiter.api.Test
import solution.Hashing
import kotlin.test.assertEquals

class Solution15Test {

    @Test
    fun test() {
        val hashing = Hashing()
        assertEquals(0, hashing.get("rn"))
        assertEquals(0, hashing.get("cm"))
        assertEquals(1, hashing.get("qp"))
        assertEquals(3, hashing.get("ot"))
    }
}