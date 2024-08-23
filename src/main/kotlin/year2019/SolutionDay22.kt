package year2019

import common.BaseSolution

fun main() = println(SolutionDay22().result())

class SolutionDay22 : BaseSolution() {

    override val day = 22
    override val year = 2019

    private val incrementRegex = Regex("^deal with increment (\\d+)$")
    private val newStackRegex = Regex("^deal into new stack$")
    private val cutRegex = Regex("^cut (-?\\d+)$")

    private val shuffles = input().split("\r\n").map { row ->
        if (incrementRegex.matches(row)) {
            Increment(incrementRegex.find(row)!!.groupValues[1].toInt())
        } else if (newStackRegex.matches(row)) {
            NewStack()
        } else if (cutRegex.matches(row)) {
            Cut(cutRegex.find(row)!!.groupValues[1].toInt())
        } else {
            throw RuntimeException("Cannot parse")
        }
    }

    override fun task1(): String {
        val deck = DeckWithTrackingCard(10007L, 2019L, 2019L)
        shuffles.forEach { it.make(deck) }
        return deck.cardIdx.toString()
    }

    override fun task2(): String {
        val repeat = 101741582076661L
        val deck = DeckAsFunction(119315717514047L, 0L, 1L)
        shuffles.forEach { it.make(deck) }
        println(deck)

        // calculate (increment, offset) for the number of iterations of the process
        // increment = increment_mul^iterations
        val increment = pow(deck.increment, repeat, deck.size)
        // offset = 0 + offset_diff * (1 + increment_mul + increment_mul^2 + ... + increment_mul^iterations)
        // use geometric series
        val a = mul(deck.offset, 1L - increment, deck.size)
        val b = inv((1L - deck.increment) % deck.size, deck.size)
        val offset = mul(a, b, deck.size)

        // gets the 2020th number in a given sequence
        val result = add(offset, mul(2020L, increment, deck.size), deck.size)
        return result.toString()
    }

    private companion object {
        // gets the modular inverse of n
        // as cards is prime, use Euler's theorem
        fun inv(n: Long, mod: Long) =
            pow(n, mod - 2L, mod)

        fun pow(a: Long, b: Long, mod: Long) =
            a.toBigInteger().modPow(b.toBigInteger(), mod.toBigInteger()).toLong()

        fun mul(a: Long, b: Long, mod: Long) =
            a.toBigInteger().multiply(b.toBigInteger()).mod(mod.toBigInteger()).toLong()

        fun add(a: Long, b: Long, mod: Long) =
            a.toBigInteger().add(b.toBigInteger()).mod(mod.toBigInteger()).toLong()
    }

    private data class DeckWithTrackingCard(val size: Long, val card: Long, var cardIdx: Long)

    /**
     * @property [offset] the first number in the sequence.
     * @property [increment] the difference between two adjacent numbers
     */
    private data class DeckAsFunction(val size: Long, var offset: Long, var increment: Long)

    private interface Shuffle {
        fun make(deck: DeckWithTrackingCard)
        fun make(deck: DeckAsFunction)
    }

    private class NewStack : Shuffle {
        override fun make(deck: DeckWithTrackingCard) {
            deck.cardIdx = deck.size - deck.cardIdx - 1
        }

        override fun make(deck: DeckAsFunction) {
            // reverse sequence
            // instead of going up, go down
            deck.increment *= -1L // no overflow
            deck.offset = add(deck.offset, deck.increment, deck.size)
        }
    }

    private data class Increment(val increment: Int) : Shuffle {
        override fun make(deck: DeckWithTrackingCard) {
            deck.cardIdx = (deck.cardIdx * increment) % deck.size
        }

        override fun make(deck: DeckAsFunction) {
            // difference between two adjacent numbers is multiplied by the
            // inverse of the increment
            deck.increment = mul(deck.increment, inv(increment.toLong(), deck.size), deck.size)
        }
    }

    private data class Cut(val cut: Int) : Shuffle {
        override fun make(deck: DeckWithTrackingCard) {
            if (cut > 0) {
                if (deck.cardIdx < cut)
                    deck.cardIdx = deck.size + deck.cardIdx - cut
                else
                    deck.cardIdx -= cut
            } else {
                if (deck.cardIdx >= deck.size + cut)
                    deck.cardIdx = deck.cardIdx - cut - deck.size
                else
                    deck.cardIdx -= cut
            }
        }

        override fun make(deck: DeckAsFunction) {
            deck.offset = add(deck.offset, mul(cut.toLong(), deck.increment, deck.size), deck.size)
        }
    }
}