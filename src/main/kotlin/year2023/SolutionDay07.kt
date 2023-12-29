package year2023

fun main() {
    println("${SolutionDay07()}")
}

class SolutionDay07 : BaseSolution() {

    override val day = 7

    override fun task1(): String {
        val rawLines = input().split("\r\n", "\n")
        val hands = rawLines.map { Hand(it, false) }.sorted().reversed().toList()
        val result = hands.mapIndexed { index, hand -> (index + 1) * hand.rank }.sum()
        return result.toString()
    }

    override fun task2(): String {
        val rawLines = input().split("\r\n", "\n")
        val hands = rawLines.map { Hand(it, true) }.sorted().reversed().toList()
        val result = hands.mapIndexed { index, hand -> (index + 1) * hand.rank }.sum()
        return result.toString()
    }

    private class Hand(private val line: String, private val jokers: Boolean) : Comparable<Hand> {

        private val numberRegex = Regex("(\\d+)")
        private val cards: List<Card>
        private val cardsNoJokers: List<Card>
        private val type: HandType
        private val jokersCount: Int
        private var groups: List<Pair<Char, List<Card>>>
        val rank: Long

        override fun toString() = "$type $line"

        init {
            val split = line.split(" ")
            cards = split[0].map { Card(it, jokers) }
            cardsNoJokers = if (jokers) cards.filter { it.value != 'J' } else cards
            rank = numbers(numberRegex, split[1])[0]
            jokersCount = if (jokers) cards.count { it.value == 'J' } else 0
            groups = cardsNoJokers.groupBy { it.value }.toList()

            type = if (fiveOfKind())
                HandType.FIVE_OF_KIND
            else if (fourOfKind())
                HandType.FOUR_OF_KIND
            else if (fullHouse())
                HandType.FULL_HOUSE
            else if (threeOfKind())
                HandType.THREE_OF_KIND
            else if (twoPair())
                HandType.TWO_PAIR
            else if (onePair())
                HandType.ONE_PAIR
            else
                HandType.HIGH_CARD
        }

        override fun compareTo(other: Hand): Int {
            val typeComparison = this.type.compareTo(other.type)
            if (typeComparison != 0)
                return typeComparison

            for (i in 0..this.cards.size) {
                val cardThis = this.cards[i]
                val cardOther = other.cards[i]
                val cardComparison = cardThis.compareTo(cardOther)
                if (cardComparison != 0)
                    return cardComparison * -1
            }
            return 0
        }

        private fun fiveOfKind(): Boolean {
            return groups.isEmpty() || groups.any { it.second.size + jokersCount == 5 }
        }

        private fun fourOfKind(): Boolean {
            return groups.any { it.second.size + jokersCount == 4 }
        }

        private fun fullHouse(): Boolean {
            if (jokersCount == 0)
                return groups.any { it.second.size == 3 } && groups.any { it.second.size == 2 }
            if (jokersCount == 1)
                return groups.filter { it.second.size == 2 }.size == 2
            if (jokersCount == 2)
                return groups.filter { it.second.size == 2 }.size == 1 && !groups.any { it.second.size == 3 }
            return false
        }

        private fun threeOfKind(): Boolean {
            if (jokersCount == 0)
                return groups.any { it.second.size == 3 } && !groups.any { it.second.size == 2 }
            if (jokersCount == 1)
                return groups.filter { it.second.size == 2 }.size == 1 && !groups.any { it.second.size == 3 }
            if (jokersCount == 2)
                return groups.filter { it.second.size == 1 }.size == 3
            return false
        }

        private fun twoPair(): Boolean {
            if (jokersCount == 0)
                return groups.filter { it.second.size == 2 }.size == 2
            return false
        }

        private fun onePair(): Boolean {
            if (jokersCount == 0)
                return groups.filter { it.second.size == 2 }.size == 1 && !groups.any { it.second.size == 3 }
            if (jokersCount == 1)
                return groups.filter { it.second.size == 1 }.size == 5 - jokersCount
            return false
        }

        private fun numbers(regex: Regex, line: String): List<Long> {
            val match = regex.findAll(line)
            return match.toList().stream().map { it.groupValues[1].toLong() }.toList()
        }
    }

    private enum class HandType {
        FIVE_OF_KIND, FOUR_OF_KIND, FULL_HOUSE, THREE_OF_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD
    }

    private class Card(val value: Char, jokers: Boolean) : Comparable<Card> {

        private val valueMap = if (jokers) valuesJoker else values

        override fun compareTo(other: Card): Int {
            val v1 = valueMap.getOrDefault(value, 0)
            val v2 = valueMap.getOrDefault(other.value, 0)
            return v1.compareTo(v2)
        }

        override fun toString() = value.toString()

        private companion object {

            private val values = mapOf(
                '2' to 2, '3' to 3, '4' to 4, '5' to 5, '6' to 6, '7' to 7, '8' to 8, '9' to 9,
                'T' to 10, 'J' to 11, 'Q' to 12, 'K' to 13, 'A' to 14,
            )

            private val valuesJoker = values.plus('J' to 1)
        }
    }
}