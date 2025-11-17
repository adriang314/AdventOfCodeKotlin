package common

import java.security.MessageDigest

object Hash {
    private val hexBytes = "0123456789abcdef".toByteArray(Charsets.US_ASCII)

    /**
     * Convert a byte array to ASCII hex bytes. Returns the byte array containing ASCII hex (lowercase).
     */
    private fun toHexAscii(src: ByteArray): ByteArray {
        val out = ByteArray(src.size * 2)
        require(out.size >= src.size * 2) { "Destination buffer too small" }

        var di = 0
        for (b in src) {
            val unsigned = b.toInt() and 0xFF
            val highNibble = unsigned ushr 4
            val lowNibble = unsigned and 0x0F
            out[di++] = hexBytes[highNibble]
            out[di++] = hexBytes[lowNibble]
        }
        return out
    }

    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return String(toHexAscii(digest), Charsets.US_ASCII)
    }

    fun md5(input: String, repeat: Int): String {
        val md = MessageDigest.getInstance("MD5")
        var current = input.toByteArray(Charsets.UTF_8)
        repeat(repeat) {
            val digest = md.digest(current)
            current = toHexAscii(digest)
        }
        return String(current, Charsets.US_ASCII)
    }
}