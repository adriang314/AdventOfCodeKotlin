package year2022

import common.BaseSolution

fun main() = println(SolutionDay03().result())

private val lower = 'a'.code..'z'.code
private val upper = 'A'.code..'Z'.code

private fun valueOf(common: Char) =
    if (common.code in lower) common.code - 96 else if (common.code in upper) common.code - 38 else throw Exception()

class SolutionDay03 : BaseSolution() {
    override val day = 3
    
    override fun task1(): String {
        val commonValues = input().split("\r\n").map {
            Rucksack(it).commonValue
        }
        return commonValues.sum().toString()
    }

    override fun task2(): String {
        val lines = input().split("\r\n")
        val commonValues = (lines.indices step 3).map { i ->
            RucksackGroup(lines[i], lines[i + 1], lines[i + 2]).commonValue
        }
        return commonValues.sum().toString()
    }

    class Rucksack(s: String) {
        val commonValue: Int

        init {
            val half = s.length / 2
            val common = s.substring(0, half).toSet().intersect(s.substring(half, s.length).toSet()).first()
            commonValue = valueOf(common)
        }
    }

    class RucksackGroup(r1: String, r2: String, r3: String) {
        val commonValue: Int

        init {
            val common = r1.toSet().intersect(r2.toSet()).intersect(r3.toSet()).first()
            commonValue = valueOf(common)
        }
    }
}