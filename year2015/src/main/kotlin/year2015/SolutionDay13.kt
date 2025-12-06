package year2015

import common.BaseSolution
import common.allPermutations

fun main() = println(SolutionDay13().result())

class SolutionDay13 : BaseSolution() {

    override val day = 13

    private val regex = """(\w+) would (lose|gain) (\d+) happiness units by sitting next to (\w+).""".toRegex()

    private val arrangements = input().split("\r\n").map {
        val match = regex.matchEntire(it)!!
        val person1 = match.groupValues[1]
        val gainOrLose = match.groupValues[2]
        val happiness = match.groupValues[3].toInt() * if (gainOrLose == "gain") 1 else -1
        val person2 = match.groupValues[4]
        SittingArrangementOption(person1, person2, happiness)
    }

    private val attendees = arrangements.flatMap { listOf(it.person1, it.person2) }.toSet()

    override fun task1(): String {
        val arrangement = SittingArrangement(arrangements)
        val maxHappiness = arrangement.maxHappiness(attendees)
        return maxHappiness.toString()
    }

    override fun task2(): String {
        val attendeesWithMe = attendees + "Me"
        val arrangementsWithMe = arrangements.toMutableList()
        for (attendee in attendees) {
            arrangementsWithMe.add(SittingArrangementOption("Me", attendee, 0))
            arrangementsWithMe.add(SittingArrangementOption(attendee, "Me", 0))
        }

        val arrangement = SittingArrangement(arrangementsWithMe)
        val maxHappiness = arrangement.maxHappiness(attendeesWithMe)
        return maxHappiness.toString()
    }

    private data class SittingArrangementOption(val person1: String, val person2: String, val happiness: Int)

    private data class SittingArrangement(val options: List<SittingArrangementOption>) {

        fun maxHappiness(attendees: Set<String>): Int = attendees.allPermutations().maxOf(::happiness)

        private fun happiness(arrangement: List<String>): Int {
            var total = 0
            for (i in arrangement.indices) {
                val person1 = arrangement[i]
                val person2 = arrangement[(i + 1) % arrangement.size]
                val option1 = options.first { it.person1 == person1 && it.person2 == person2 }
                val option2 = options.first { it.person1 == person2 && it.person2 == person1 }
                total += option1.happiness + option2.happiness
            }
            return total
        }
    }
}