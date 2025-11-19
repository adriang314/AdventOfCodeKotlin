package year2015

import common.BaseSolution

fun main() = println(SolutionDay05().result())

class SolutionDay05 : BaseSolution() {

    override val day = 5

    private val strings = input().split("\r\n").map { SomeString(it) }

    override fun task1(): String {
        return strings.count { it.isNice() }.toString()
    }

    override fun task2(): String {
        return strings.count { it.isNicer() }.toString()
    }

    private class SomeString(private val text: String) {
        companion object {
            private val vowels = listOf('a', 'e', 'i', 'o', 'u')
        }

        fun isNice(): Boolean {
            val hasThreeVowels = text.count { vowels.contains(it) } >= 3
            val hasDoubleLetter = text.windowed(2, 1).any { it[0] == it[1] }
            val noSpecialStrings = !text.contains("ab") && !text.contains("cd") && !text.contains("pq") && !text.contains("xy")
            return hasThreeVowels && hasDoubleLetter && noSpecialStrings
        }

        fun isNicer(): Boolean {
            val hasThreeCharsPalindrome = text.windowed(3).any { it[0] == it[2] }
            val hasPairThatAppearsTwice = text.windowed(2, 1).foldIndexed(PairStore()) { index, store, pair -> store.tryAdd(pair, index) }.hasTwoPairs()
            return hasThreeCharsPalindrome && hasPairThatAppearsTwice
        }

        private class PairStore(val map: MutableMap<String, PairAppearance> = mutableMapOf()) {

            fun hasTwoPairs() = map.any { it.value.indexes.size > 1 }

            fun tryAdd(pair: String, index: Int): PairStore {
                map.compute(pair) { _, pairAppearance ->
                    if (pairAppearance == null) {
                        PairAppearance(1, listOf(index))
                    } else {
                        when {
                            pairAppearance.indexes.contains(index - 1) -> pairAppearance
                            else -> pairAppearance.copy(count = pairAppearance.count + 1, indexes = pairAppearance.indexes.plus(index))
                        }
                    }
                }
                return this
            }
        }

        private data class PairAppearance(val count: Int, val indexes: List<Int>)
    }
}