package common

import java.util.*

class Combinatorics {
    companion object {

        /**
         *   val list = LinkedList<CharArray>()
         *   val item = CharArray(operations)
         *   val input = charArrayOf('*', '+')
         *
         *   variationsWithRepetition(list, operationTypes, item)
         *
         *   generates for size 2: **, *+, +*, ++
         */
        fun variationsWithRepetition(result: LinkedList<CharArray>, input: CharArray, item: CharArray, count: Int = 0) {
            if (count < item.size) {
                for (i in input.indices) {
                    item[count] = input[i]
                    variationsWithRepetition(result, input, item, count + 1)
                }
            } else {
                result.add(item.clone())
            }
        }

        fun <T> combinations(items: Set<T>, size: Int): List<Set<T>> {
            require(size >= 0) { "Size must be non-negative" }
            if (size == 0) return listOf(emptySet())
            if (size > items.size) return emptyList()

            val itemList = items.toList()

            fun combine(start: Int, current: MutableList<T>, result: MutableList<Set<T>>) {
                if (current.size == size) {
                    result.add(current.toSet())
                    return
                }

                for (i in start until itemList.size) {
                    current.add(itemList[i])
                    combine(i + 1, current, result)
                    current.removeAt(current.lastIndex)
                }
            }

            val result = mutableListOf<Set<T>>()
            combine(0, mutableListOf(), result)
            return result
        }
    }
}