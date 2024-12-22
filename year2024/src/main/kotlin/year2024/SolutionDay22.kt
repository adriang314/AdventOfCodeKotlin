package year2024

import common.BaseSolution

fun main() = println(SolutionDay22().result())

class SolutionDay22 : BaseSolution() {

    override val day = 22

    private val secretNumbers = input().split("\r\n").map { line -> SecretNumber(line.toLong()) }
    private val secretNumbersForDay = secretNumbers.map { SecretNumbersForDay(it) }
    private val pricesForDay = secretNumbersForDay.map { PricesForDay(it) }

    override fun task1(): String {
        val result = secretNumbersForDay.sumOf { it.list.last().value }
        return result.toString()
    }

    override fun task2(): String {
        var maxBananas = 0
        for (chg1 in -9..9) {
            for (chg2 in -9..9) {
                for (chg3 in -9..9) {
                    for (chg4 in -9..9) {
                        val change = PriceChange(chg1, chg2, chg3, chg4)
                        val receivedBananas = pricesForDay.sumOf { prices -> prices.getPriceAt(change) ?: 0 }
                        if (receivedBananas > maxBananas) {
                            maxBananas = receivedBananas
                        }
                    }
                }
            }
        }

        return maxBananas.toString()
    }

    private data class PriceChange(val ch1: Int, val ch2: Int, val chg3: Int, val chg4: Int)

    private class PricesForDay(secretNumbers: SecretNumbersForDay) {
        private val prices = secretNumbers.list.map { (it.value % 10).toInt() }
        private val priceChanges = prices.mapIndexedNotNull { idx, l -> if (idx == 0) null else l - prices[idx - 1] }
        private val priceChangeMap = mutableMapOf<PriceChange, Int>()

        init {
            priceChanges.forEachIndexed { idx, chg4 ->
                if (idx >= 3) {
                    val chg1 = priceChanges[idx - 3]
                    val chg2 = priceChanges[idx - 2]
                    val chg3 = priceChanges[idx - 1]
                    val priceChange = PriceChange(chg1, chg2, chg3, chg4)
                    priceChangeMap.putIfAbsent(priceChange, prices[idx + 1])
                }
            }
        }

        fun getPriceAt(priceChange: PriceChange): Int? {
            return priceChangeMap[priceChange]
        }
    }

    private class SecretNumbersForDay(initSecretNumber: SecretNumber) {
        val list = calculate(initSecretNumber)

        private fun calculate(initSecretNumber: SecretNumber): List<SecretNumber> {
            val list = ArrayList<SecretNumber>(2001)
            list.add(initSecretNumber)
            repeat(2000) {
                list.add(list.last().next())
            }
            return list
        }
    }

    private data class SecretNumber(val value: Long) {
        fun next(): SecretNumber {
            var next = value * 64L
            next = mix(next, value)
            var newValue = prune(next)

            next = newValue / 32L
            next = mix(next, newValue)
            newValue = prune(next)

            next = newValue * 2048L
            next = mix(next, newValue)
            newValue = prune(next)

            return SecretNumber(newValue)
        }

        companion object {
            private fun mix(x: Long, y: Long): Long {
                return x.xor(y)
            }

            private fun prune(x: Long): Long {
                return x % 16777216L
            }
        }
    }
}