package year2017

import common.BaseSolution
import common.Point3D

fun main() = println(SolutionDay20().result())

class SolutionDay20 : BaseSolution() {

    override val day = 20

    private val particles = input().split("\r\n").mapIndexed { idx, line ->
        val (pX, pY, pZ, vX, vY, vZ, aX, aY, aZ) = """p=<(-?\d+),(-?\d+),(-?\d+)>, v=<(-?\d+),(-?\d+),(-?\d+)>, a=<(-?\d+),(-?\d+),(-?\d+)>""".toRegex().find(line)!!.destructured
        Particle(idx, Point3D(pX.toLong(), pY.toLong(), pZ.toLong()), Point3D(vX.toLong(), vY.toLong(), vZ.toLong()), Point3D(aX.toLong(), aY.toLong(), aZ.toLong()))
    }

    override fun task1(): String {
        var particlesStatus = particles
        repeat(500) { particlesStatus = particlesStatus.map { it.tick() } }
        val result = particlesStatus.minBy { it.distance() }.id
        return result.toString()
    }

    override fun task2(): String {
        var particlesStatus = particles
        repeat(500) { particlesStatus = particlesStatus.map { it.tick() }.groupBy { it.position }.filter { it.value.size == 1 }.values.flatten() }
        val result = particlesStatus.count()
        return result.toString()
    }

    private data class Particle(val id: Int, val position: Point3D, val velocity: Point3D, val acceleration: Point3D) {

        fun tick(): Particle {
            val newVelocity = velocity.shift(acceleration)
            val newPosition = position.shift(newVelocity)
            return Particle(id, newPosition, newVelocity, acceleration)
        }

        fun distance(): Long = position.distanceTo(Point3D(0, 0, 0))
    }
}