package year2024

import common.BaseSolution

fun main() = println(SolutionDay25().result())

class SolutionDay25 : BaseSolution() {

    override val day = 25

    private val items: List<Item>

    init {
        var value = mutableListOf<String>()
        val items = mutableListOf<Item>()

        input().split("\r\n").forEach { line ->
            if (line.isEmpty()) {
                items.add(Item(value))
                value = mutableListOf()
            } else {
                value.add(line)
            }
        }

        items.add(Item(value))
        this.items = items
    }

    override fun task1(): String {
        val locks = items.filter { it.isLock }
        val keys = items.filter { !it.isLock }
        val result = locks.sumOf { lock -> keys.map { key -> if (Item.isMatching(key, lock)) 1 else 0 }.sum() }
        return result.toString()
    }

    override fun task2(): String {
        return ""
    }

    private class Item(value: List<String>) {
        val parts = value.map { it.toList() }
        val isLock = value.first() == "#####"
        val pin1Height: Int
        val pin2Height: Int
        val pin3Height: Int
        val pin4Height: Int
        val pin5Height: Int

        init {
            pin1Height = if (isLock) calcHeightForLock(0, 0) else calcHeightForKey(0, 5)
            pin2Height = if (isLock) calcHeightForLock(1, 0) else calcHeightForKey(1, 5)
            pin3Height = if (isLock) calcHeightForLock(2, 0) else calcHeightForKey(2, 5)
            pin4Height = if (isLock) calcHeightForLock(3, 0) else calcHeightForKey(3, 5)
            pin5Height = if (isLock) calcHeightForLock(4, 0) else calcHeightForKey(4, 5)
        }

        private fun calcHeightForLock(x: Int, y: Int): Int =
            if (y == 6)
                5
            else if (parts[y][x] == '#')
                calcHeightForLock(x, y + 1)
            else y - 1

        private fun calcHeightForKey(x: Int, y: Int): Int =
            if (y == 0)
                5
            else if (parts[y][x] == '#')
                calcHeightForKey(x, y - 1)
            else 5 - y

        companion object {
            fun isMatching(key: Item, schema: Item) =
                (key.pin1Height + schema.pin1Height) <= 5 &&
                        (key.pin2Height + schema.pin2Height) <= 5 &&
                        (key.pin3Height + schema.pin3Height) <= 5 &&
                        (key.pin4Height + schema.pin4Height) <= 5 &&
                        (key.pin5Height + schema.pin5Height) <= 5
        }

    }
}