package year2023

import common.BaseSolution

private val hashing = Hashing()

fun main() {
    println("${SolutionDay15()}")
}

class SolutionDay15 : BaseSolution() {

    override val day = 15

    override fun task1(): String {
        val parts = input().split(",")
        return parts.sumOf { hashing.get(it) }.toString()
    }

    override fun task2(): String {
        val parts = input().split(",")
        val boxes = Boxes()
        parts.forEach { boxes.executeOperation(it) }
        return boxes.focusingPower().toString()
    }

    class Boxes {
        private val boxes = mutableMapOf<Long, Box>().apply { (0..<256L).forEach { this[it] = Box(it) } }.toMap()

        fun executeOperation(p: String) {
            val isRemove = p.endsWith('-')
            val label = p.substring(0, if (isRemove) p.length - 1 else p.length - 2)
            val hash = hashing.get(label)
            val box = boxes[hash]!!
            if (isRemove)
                box.remove(label)
            else
                box.add(Label(label).apply { focalLength = p.last().toString().toInt() })
        }

        fun focusingPower() = boxes.values.sumOf { it.focusingPower() }
    }

    class Box(private val id: Long) {
        private val labels = mutableMapOf<String, Pair<Long, Label>>()
        private var newLabelCounter = 0L

        fun focusingPower() = labels().mapIndexed { index, focalLength -> (index + 1) * focalLength * (id + 1) }.sum()

        fun remove(label: String) = labels.remove(label)

        fun add(label: Label) {
            val curr = labels[label.text]
            labels[label.text] = Pair(curr?.first ?: ++newLabelCounter, label)
        }

        private fun labels() = labels.values.sortedBy { it.first }.map { it.second.focalLength!! }
    }

    class Label(val text: String, var focalLength: Int? = null)
}

class Hashing {
    fun get(input: String): Long {
        var currVal = 0L
        input.forEach {
            currVal += it.code
            currVal *= 17L
            currVal %= 256L
        }
        return currVal
    }
}