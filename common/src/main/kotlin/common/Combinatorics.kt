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
    }
}