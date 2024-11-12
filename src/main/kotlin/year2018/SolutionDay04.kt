package year2018

import common.BaseSolution
import java.time.LocalDateTime

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4
    override val year = 2018

    private val regexLine = Regex("^\\[(\\d+)-(\\d+)-(\\d+) (\\d+):(\\d+)] (.*)$")
    private val regexGuard = Regex("^Guard #(\\d+) begins shift$")
    private val regexGuardSleeps = Regex("^falls asleep$")
    private val regexGuardWakesUp = Regex("^wakes up$")
    private val guards = mutableListOf<Guard>()

    init {
        var currGuard: Guard? = null
        //var currShift: Shift? = null
        var currAsleep: LocalDateTime? = null

        val lines = input().split("\r\n").map {
            val (year, month, day, hour, minute, text) = regexLine.find(it)!!.destructured
            Line(LocalDateTime.of(year.toInt(), month.toInt(), day.toInt(), hour.toInt(), minute.toInt()), text)
        }.sortedBy { it.date }

        lines.forEach { line ->
            if (regexGuard.matches(line.text)) {
                val (id) = regexGuard.find(line.text)!!.destructured
                currGuard = guards.firstOrNull { it.id == id.toInt() } ?: Guard(id.toInt())
                currGuard!!.shifts.add(Shift(line.date))

                if (!guards.contains(currGuard))
                    guards.add(currGuard!!)
            } else if (regexGuardSleeps.matches(line.text)) {
                currAsleep = line.date
            } else if (regexGuardWakesUp.matches(line.text)) {
                currGuard!!.addNapToLatestShift(IntRange(currAsleep!!.minute, line.date.minute - 1))
            } else {
                throw RuntimeException("Unknown line")
            }
        }
    }

    override fun task1(): String {
        val maxSleepGuard = guards.maxBy { it.totalNapTimeInMinutes() }
        val mostProbableSleep = maxSleepGuard.mostProbableSleepMinute()
        val result = maxSleepGuard.id * mostProbableSleep.minute
        return result.toString()
    }

    override fun task2(): String {
        val mostProbableSleepMinuteGuard = guards.maxBy { it.mostProbableSleepMinute().times }
        val result = mostProbableSleepMinuteGuard.id * mostProbableSleepMinuteGuard.mostProbableSleepMinute().minute
        return result.toString()
    }

    private data class Line(val date: LocalDateTime, val text: String)

    private data class Guard(val id: Int) {
        val shifts = mutableListOf<Shift>()
        fun addNapToLatestShift(nap: IntRange) = shifts.last().naps.add(nap)
        fun totalNapTimeInMinutes() = shifts.sumOf { it.totalNapTimeInMinutes() }
        fun mostProbableSleepMinute(): MostProbableSleepMinute {
            val sleepingMinutes = shifts.map { it.naps.map { rng -> rng.toList() } }.flatten().flatten()
            val max = sleepingMinutes.groupBy { it }.maxByOrNull { it.value.size }
            return MostProbableSleepMinute(max?.key ?: 0, max?.value?.size ?: 0)
        }
    }

    private data class MostProbableSleepMinute(val minute: Int, val times: Int)

    private data class Shift(val start: LocalDateTime) {
        val naps = mutableListOf<IntRange>()
        fun totalNapTimeInMinutes() = naps.sumOf { it.last - it.first }
    }
}