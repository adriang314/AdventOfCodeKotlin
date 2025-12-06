package year2015

import common.BaseSolution

fun main() = println(SolutionDay14().result())

class SolutionDay14 : BaseSolution() {

    override val day = 14

    private val regex = """(\w+) can fly (\d+) km/s for (\d+) seconds, but then must rest for (\d+) seconds.""".toRegex()

    private val reindeer = input().split("\r\n").map {
        val (name, speed, flyTime, restTime) = regex.matchEntire(it)!!.destructured
        Reindeer(name, speed.toInt(), flyTime.toInt(), restTime.toInt())
    }

    override fun task1(): String {
        return reindeer.maxOf { it.distanceAfter(2503) }.toString()
    }

    override fun task2(): String {
        val points = mutableMapOf<String, Int>()
        for (second in 1..2503) {
            val distances = reindeer.associateWith { it.distanceAfter(second) }
            val maxDistance = distances.values.max()
            distances.filter { it.value == maxDistance }.keys.forEach { deer -> points[deer.name] = points.getOrDefault(deer.name, 0) + 1 }
        }
        return points.maxOf { it.value }.toString()
    }

    private data class Reindeer(val name: String, val speed: Int, val flyTime: Int, val restTime: Int) {

        fun distanceAfter(seconds: Int): Int {
            val cycleTime = flyTime + restTime
            val fullCycles = seconds / cycleTime
            val remainingTime = seconds % cycleTime
            val flyingTimeInLastCycle = minOf(remainingTime, flyTime)
            return (fullCycles * flyTime + flyingTimeInLastCycle) * speed
        }
    }
}