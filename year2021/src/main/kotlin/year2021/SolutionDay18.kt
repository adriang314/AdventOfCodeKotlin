package year2021

import common.BaseSolution

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {

    override val day = 18
    
    override fun task1(): String {
        val numbers = input().split("\r\n").map { SnailNumber.fromText(it) }
        var result = numbers.first()
        for (i in 1 until numbers.size) {
            result = result.addNumber(numbers[i])
            result.reduce()
        }

        return result.magnitude().toString()
    }

    override fun task2(): String {
        val numbers = input().split("\r\n")
        var maxMagnitude = 0L
        for (i in numbers.indices)
            for (j in numbers.indices) {
                if (i != j) {
                    val result = SnailNumber.fromText(numbers[i]).addNumber(SnailNumber.fromText(numbers[j]))
                    result.reduce()
                    val magnitude = result.magnitude()
                    if (magnitude > maxMagnitude)
                        maxMagnitude = magnitude
                }
            }

        return maxMagnitude.toString()
    }

    class SnailNumber(
        var value: Long? = null,
        var parent: SnailNumber? = null,
        var left: SnailNumber? = null,
        var right: SnailNumber? = null
    ) {
        lateinit var name: String

        private fun increaseValue(by: Long?) {
            if (value != null && by != null)
                value = value!! + by
        }

        private fun leftRightWithValues() = left?.value != null && right?.value != null

        private fun findNumberExplodes(): SnailNumber? {
            if (leftRightWithValues() && level() == 5)
                return this
            val leftExplode = left?.findNumberExplodes()
            if (leftExplode != null)
                return leftExplode
            val rightExplode = right?.findNumberExplodes()
            if (rightExplode != null)
                return rightExplode
            return null
        }

        private fun findNumberSplits(): SnailNumber? {
            if (canSplit(left))
                return left
            val leftSplits = left?.findNumberSplits()
            if (leftSplits != null)
                return leftSplits

            if (canSplit(right))
                return right
            val rightSplits = right?.findNumberSplits()
            if (rightSplits != null)
                return rightSplits
            return null
        }

        private fun explode() {
            leftNeighbour()?.increaseValue(left?.value)
            rightNeighbour()?.increaseValue(right?.value)

            if (this.parent!!.left == this)
                this.parent!!.left = newNumber(0).also { it.parent = this.parent }
            else if (this.parent!!.right == this)
                this.parent!!.right = newNumber(0).also { it.parent = this.parent }
            else
                throw Exception()
        }

        private fun split() {
            left = newNumber(value!! / 2).also { it.parent = this }
            right = newNumber(if (value!! % 2 == 0L) value!! / 2 else (value!! + 1) / 2).also { it.parent = this }
            value = null
        }

        fun reduce() {
            val numberExplodes = findNumberExplodes()
            if (numberExplodes != null) {
                numberExplodes.explode()
                reduce()
                return
            }

            val numberSplits = findNumberSplits()
            if (numberSplits != null) {
                numberSplits.split()
                reduce()
            }

            return
        }

        fun magnitude(): Long = value ?: ((left!!.magnitude() * 3) + (right!!.magnitude() * 2))

        fun level(): Int {
            var value = 0
            var curr: SnailNumber? = this
            do {
                curr = curr?.parent
                value++
            } while (curr != null)
            return value
        }

        private fun rightValue(): SnailNumber = right?.rightValue() ?: this

        private fun leftValue(): SnailNumber = left?.leftValue() ?: this

        fun addNumber(other: SnailNumber) = addNumber(this, other)

        fun leftNeighbour(): SnailNumber? {
            var curr: SnailNumber? = this
            do {
                val parentLeft = curr?.parent?.left ?: return null
                if (parentLeft != curr)
                    return parentLeft.rightValue()
                curr = curr.parent
            } while (true)
        }

        fun rightNeighbour(): SnailNumber? {
            var curr: SnailNumber? = this
            do {
                val parentRight = curr?.parent?.right ?: return null
                if (parentRight != curr)
                    return parentRight.leftValue()
                curr = curr.parent
            } while (true)
        }

        fun toText(): String = value?.toString() ?: "[${left?.toText()},${right?.toText()}]"

        override fun toString(): String = if (value != null) value.toString() else "[${left?.value},${right?.value}]"

        companion object {
            private var refCount = 1
            private fun newName() = "sn" + refCount++
            private fun newNumber(value: Long?) = SnailNumber(value).also { it.name = newName() }
            private fun canSplit(num: SnailNumber?) = num?.value != null && num.value!! >= 10

            private fun addNumber(num1: SnailNumber, num2: SnailNumber): SnailNumber {
                return SnailNumber(
                    left = num1,
                    right = num2
                ).also {
                    it.name = newName()
                    it.left!!.parent = it
                    it.right!!.parent = it
                }
            }

            fun fromText(text: String): SnailNumber {
                val regex = Regex("(\\[\\w+,\\w+])")
                var currText = text
                val map = mutableMapOf<String, SnailNumber>()
                while (true) {
                    val matchFound = regex.findAll(currText).toList()
                    if (matchFound.isEmpty())
                        break

                    matchFound.map { match ->
                        val (result) = match.destructured
                        val parts = result.replace("[", "").replace("]", "").split(",")
                        val snLeft = map[parts[0]] ?: newNumber(parts[0].toLongOrNull())
                        val snRight = map[parts[1]] ?: newNumber(parts[1].toLongOrNull())
                        val sn = addNumber(snLeft, snRight)
                        currText = currText.replaceFirst(result, sn.name)
                        map[sn.name] = sn
                    }
                }

                return map[currText]!!
            }
        }
    }
}