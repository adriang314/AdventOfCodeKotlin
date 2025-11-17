package common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HashTest {

    @Test
    fun testMd5() {
        val hash1 = Hash.md5("abc0")
        assertEquals("577571be4de9dcce85a041ba0410f29f", hash1)

        val hash2 = Hash.md5("abc2016")
        assertEquals("47a2348ff8879368e4373d15ff5bbc58", hash2)
    }

    @Test
    fun testMd5Repeated() {
        val hash1 = Hash.md5("abc", 2)

        assertEquals("ec0405c5aef93e771cd80e0db180b88b", hash1)
    }
}