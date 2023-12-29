package year2022

import common.BaseSolution

fun main() {
    println("${SolutionDay05()}")
}

class SolutionDay05 : BaseSolution() {
    override val day = 5
    override val year = 2022

    override fun task1(): String {
        val crates = initCrates()
        moves.forEach {
            val fromCrate = crates[it.from - 1]
            val toCrate = crates[it.to - 1]
            (1..it.count).forEach { _ -> toCrate.stack.add(fromCrate.stack.removeLast()) }
        }
        return crates.map { it.stack.last() }.joinToString("")
    }

    override fun task2(): String {
        val crates = initCrates()
        moves.forEach {
            val fromCrate = crates[it.from - 1]
            val toCrate = crates[it.to - 1]
            val removed = (1..it.count).map { _ -> fromCrate.stack.removeLast() }
            removed.reversed().map { taken -> toCrate.stack.add(taken) }
        }
        return crates.map { it.stack.last() }.joinToString("")
    }

    private var init: List<String>
    private var moves: List<CrateMove>

    init {
        val regex = Regex("move (\\d+) from (\\d+) to (\\d+)")
        val split = input().split("\r\n\r\n")
        init = split[0].split("\n")
        moves = split[1].split("\n").map {
            val match = regex.find(it)!!
            CrateMove(match.groupValues[1].toInt(), match.groupValues[2].toInt(), match.groupValues[3].toInt())
        }
    }

    private fun initCrates(): List<CrateStack> {
        val crates = init.last().split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.map { CrateStack(it) }
        init.filterIndexed { index, _ -> index < init.size - 1 }.reversed()
            .forEach {
                var startIdx = 0
                do {
                    val foundIdx = it.indexOf('[', startIdx)
                    if (foundIdx >= 0) {
                        startIdx = foundIdx + 1
                        val crateNumber = (foundIdx / 4) + 1
                        crates[crateNumber - 1].stack.add(it[foundIdx + 1])
                    }
                } while (foundIdx >= 0)
            }
        return crates
    }

    data class CrateStack(val number: Int, val stack: ArrayDeque<Char> = ArrayDeque())

    data class CrateMove(val count: Int, val from: Int, val to: Int)
}