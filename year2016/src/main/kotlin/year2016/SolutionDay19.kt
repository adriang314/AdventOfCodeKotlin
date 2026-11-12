package year2016

import common.BaseSolution
import common.LinkedListWithCache

fun main() = println(SolutionDay19().result())

class SolutionDay19 : BaseSolution() {

    override val day = 19

    private val numberOfElves = input().toInt()

    override fun task1(): String {
        val elves = List(numberOfElves) { Elf(it + 1) }
        var elvesWithPresents = numberOfElves;
        var currElfIndex = 0

        fun nextElfWithPresentsIndex(elfIndex: Int): Int {
            var nextElfIndex = (elfIndex + 1) % elves.size
            while (!elves[nextElfIndex].hasPresents) {
                nextElfIndex = (nextElfIndex + 1) % elves.size
            }
            return nextElfIndex
        }

        while (elvesWithPresents-- > 1) {
            val nextElfIndex = nextElfWithPresentsIndex(currElfIndex)
            elves[nextElfIndex].hasPresents = false
            currElfIndex = nextElfWithPresentsIndex(nextElfIndex)
        }

        return elves.first { it.hasPresents }.id.toString()
    }

    override fun task2(): String {
        val elves = LinkedListWithCache<Elf>()
        (1..numberOfElves).forEach { elves.add(Elf(it)) }

        fun nextElfWithPresents(elf: Elf): Elf = elves.getAfter(elf, 1)

        fun oppositeElfWithPresents(elf: Elf): Elf = if (elves.size() % 2 == 0) elves.getAfter(elf, 1) else elf

        var currElf = elves.getFirst()!!
        var currOppositeElf = elves.getAfter(currElf, elves.size() / 2)
        while (elves.size() > 1) {
            val nextElfAfterOpposite = elves.getAfter(currOppositeElf, 1)
            elves.remove(currOppositeElf)
            currElf = nextElfWithPresents(currElf)
            currOppositeElf = oppositeElfWithPresents(nextElfAfterOpposite)
        }

        return currElf.id.toString()
    }

    private data class Elf(val id: Int, var hasPresents: Boolean = true)
}