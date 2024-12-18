package year2019

import common.BaseSolution
import common.LeastCommonMultiple
import java.util.LinkedList
import kotlin.math.absoluteValue

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {

    override val day = 12
    
    private val moonPositionRegEx = Regex("x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)")
    private val initMoons = input().split("\r\n").map { line ->
        val (x, y, z) = moonPositionRegEx.find(line)!!.destructured
        Moon(Position(x.toInt(), y.toInt(), z.toInt()), Velocity(0, 0, 0))
    }

    override fun task1(): String {
        val moons = initMoons.map { it.copy(position = it.position.copy(), velocity = it.velocity.copy()) }
        repeat(1_000) {
            applyGravity(moons)
        }
        val result = moons.sumOf { it.totalEnergy() }
        return result.toString()
    }

    override fun task2(): String {
        val moons = initMoons.map { it.copy(position = it.position.copy(), velocity = it.velocity.copy()) }
        var counter = 0L
        val xMatches = LinkedList<Long>()
        val yMatches = LinkedList<Long>()
        val zMatches = LinkedList<Long>()

        while (xMatches.size < 2L || yMatches.size < 2L || zMatches.size < 2L) {
            applyGravity(moons)
            counter++

            val xMatch = moons.mapIndexed { idx, moon -> moon.xMatch(initMoons[idx]) }.all { it }
            val yMatch = moons.mapIndexed { idx, moon -> moon.yMatch(initMoons[idx]) }.all { it }
            val zMatch = moons.mapIndexed { idx, moon -> moon.zMatch(initMoons[idx]) }.all { it }

            if (xMatch) xMatches.add(counter)
            if (yMatch) yMatches.add(counter)
            if (zMatch) zMatches.add(counter)
        }

        val xCycle = xMatches[xMatches.size - 1] - xMatches[xMatches.size - 2]
        val yCycle = yMatches[yMatches.size - 1] - yMatches[yMatches.size - 2]
        val zCycle = zMatches[zMatches.size - 1] - zMatches[zMatches.size - 2]
        val result = LeastCommonMultiple.find(listOf(xCycle, yCycle, zCycle))
        return result.toString()
    }

    private fun applyGravity(moons: List<Moon>) {
        for (moon1 in moons) {
            // velocity changes
            var x = 0
            var y = 0
            var z = 0
            for (moon2 in moons) {
                if (moon1 === moon2)
                    continue
                if (moon1.position.x > moon2.position.x) x-- else if (moon1.position.x < moon2.position.x) x++
                if (moon1.position.y > moon2.position.y) y-- else if (moon1.position.y < moon2.position.y) y++
                if (moon1.position.z > moon2.position.z) z-- else if (moon1.position.z < moon2.position.z) z++
            }

            val moon1VelocityChange = Velocity(x, y, z)
            moon1.velocity.update(moon1VelocityChange)
        }

        moons.forEach { it.move() }
    }

    private data class Moon(var position: Position, var velocity: Velocity) {

        fun xMatch(other: Moon): Boolean =
            other.position.x == position.x && other.velocity.x == velocity.x

        fun yMatch(other: Moon): Boolean =
            other.position.y == position.y && other.velocity.y == velocity.y

        fun zMatch(other: Moon): Boolean =
            other.position.z == position.z && other.velocity.z == velocity.z

        fun potentialEnergy(): Long =
            0L + position.x.absoluteValue + position.y.absoluteValue + position.z.absoluteValue

        fun kineticEnergy(): Long =
            0L + velocity.x.absoluteValue + velocity.y.absoluteValue + velocity.z.absoluteValue

        fun totalEnergy(): Long = potentialEnergy() * kineticEnergy()

        fun move(): Unit = position.update(velocity)
    }

    private data class Position(var x: Int, var y: Int, var z: Int) {
        fun update(other: Velocity) {
            x += other.x
            y += other.y
            z += other.z
        }
    }

    private data class Velocity(var x: Int, var y: Int, var z: Int) {
        fun update(other: Velocity) {
            x += other.x
            y += other.y
            z += other.z
        }
    }
}