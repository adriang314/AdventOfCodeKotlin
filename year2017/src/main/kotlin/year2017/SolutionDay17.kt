package year2017

import common.BaseSolution
import common.LinkedListWithCache

fun main() = println(SolutionDay17().result())

class SolutionDay17 : BaseSolution() {

    override val day = 17

    private val spinLock = input().toInt()

    override fun task1(): String {
        val machine = Machine(spinLock)
        (1..2017).forEach { machine.addItem(it) }
        return machine.itemAfterCurrent().toString()
    }

    override fun task2(): String {
        val machine = Machine(spinLock)
        (1..50_000_000).forEach { machine.addItem(it) }
        return machine.itemAfterZero().toString()
    }

    private class Machine(private val spinlock: Int) {
        private val items = LinkedListWithCache<Int>().also { it.add(0) }
        private var currentItem = items.getFirst()!!

        fun addItem(value: Int) {
            currentItem = items.getAfter(currentItem, spinlock)
            items.addAfter(value, currentItem)
            currentItem = value
        }

        fun itemAfterCurrent(): Int = items.getAfter(currentItem, 1)

        fun itemAfterZero(): Int = items.getAfter(0, 1)
    }
}