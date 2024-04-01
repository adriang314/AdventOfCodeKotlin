package year2020

import common.BaseSolution
import java.math.BigInteger

fun main() = println(SolutionDay25().result())

class SolutionDay25 : BaseSolution() {

    override val day = 25
    override val year = 2020

    override fun task1(): String {
        val cardLoopSize = LoopSizeDecoder.decode(cardPublicKey)
        val doorLoopSize = LoopSizeDecoder.decode(doorPublicKey)

        val cardEncryptionKey = Encryption.encrypt(cardPublicKey, doorLoopSize)
        val doorEncryptionKey = Encryption.encrypt(doorPublicKey, cardLoopSize)
        if (cardEncryptionKey != doorEncryptionKey)
            throw RuntimeException("Keys not equal")

        return doorEncryptionKey.value.toString()
    }

    override fun task2(): String {
        return ""
    }

    private val cardPublicKey: PublicKey
    private val doorPublicKey: PublicKey

    init {
        val parts = input().split("\r\n")
        cardPublicKey = PublicKey(parts[0].toLong())
        doorPublicKey = PublicKey(parts[1].toLong())
    }

    object Encryption {
        const val MULTIPLIER = 7L
        const val MODULO = 20201227L
        private val MODULO_BIG_INT = BigInteger.valueOf(20201227L)

        fun encrypt(publicKey: PublicKey, loopSize: LoopSize): EncryptionKey {
            val multiplier = BigInteger.valueOf(publicKey.value)
            var result = BigInteger.valueOf(1L)

            repeat(loopSize.value) {
                result *= multiplier
                result %= MODULO_BIG_INT
            }

            return EncryptionKey(result.longValueExact())
        }
    }

    object LoopSizeDecoder {

        fun decode(publicKey: PublicKey): LoopSize {
            var loopSize = 1
            var currValue = 7L
            do {
                currValue = (currValue * Encryption.MULTIPLIER) % Encryption.MODULO
                loopSize++
            } while (currValue != publicKey.value)

            println("Found loop size for $publicKey = $loopSize")
            return LoopSize(loopSize)
        }
    }

    data class EncryptionKey(val value: Long)

    data class PublicKey(val value: Long)

    data class LoopSize(val value: Int)
}