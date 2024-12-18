package year2019

import common.BaseSolution
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

fun main() = println(SolutionDay10().result())

class SolutionDay10 : BaseSolution() {

    override val day = 10
    
    private val map = UniverseMap(input().split("\r\n").mapIndexed { y, row ->
        row.mapIndexed { x, c -> if (c == '#') Position(x, y) else null }
    }.flatten().filterNotNull())

    override fun task1(): String {
        val result = map.bestPositionVisibility
        return result.toString()
    }

    override fun task2(): String {
        map.vaporize()
        val result = map.vaporizedAsteroids[199].let { it.original.x * 100 + it.original.y }
        return result.toString()
    }

    private data class UniverseMap(val asteroids: List<Position>) {
        private val visibilityMap = asteroids.associateWith { visibility(it) }

        private val best = visibilityMap.maxBy { it.value }
        private val bestPosition = best.key
        val bestPositionVisibility = best.value

        // best position in the middle (0,0)
        private val asteroidsRelativePositions = asteroids
            .map { RelativePosition(it, Position(it.x - bestPosition.x, it.y - bestPosition.y)) }
            .filter { it.original != bestPosition }
            .sortedWith(compareBy(RelativePosition::angle, RelativePosition::distance))

        val vaporizedAsteroids = mutableListOf<RelativePosition>()

        fun vaporize() {
            val asteroidsToVaporize = asteroidsRelativePositions.toMutableList()

            val firstAsteroid = asteroidsToVaporize.first { it.angle >= -90.0 }
            var currIndex = asteroidsToVaporize.indexOf(firstAsteroid)
            var prevAsteroid: RelativePosition? = null

            while (asteroidsToVaporize.isNotEmpty()) {
                val currAsteroid = asteroidsToVaporize[currIndex]

                val onlySingleAngleLeft = asteroidsToVaporize.groupBy { it.angle }.size == 1
                val angleDiffThanPrevious = prevAsteroid == null || prevAsteroid.angle != currAsteroid.angle
                if (angleDiffThanPrevious || onlySingleAngleLeft) {
                    vaporizedAsteroids.add(currAsteroid)
                    asteroidsToVaporize.removeAt(currIndex)
                    currIndex %= max(asteroidsToVaporize.size, 1)
                } else {
                    currIndex = (currIndex + 1) % asteroidsToVaporize.size
                }

                prevAsteroid = currAsteroid
            }
        }

        private fun visibility(asteroid: Position): Int {
            return asteroids.asSequence()
                .filter { it != asteroid }
                .count { otherAsteroid ->
                    val anyInTheMiddle = asteroids.asSequence()
                        .filter { it != asteroid && it != otherAsteroid }
                        .any { isInTheMiddle(asteroid, otherAsteroid, it) }
                    !anyInTheMiddle
                }
        }

        private fun isInTheMiddle(pos1: Position, pos2: Position, pos: Position): Boolean {
            // (y-y1)(x2-x1)=(y2-y1)(x-x1)
            val left = (pos.y - pos1.y) * (pos2.x - pos1.x)
            val right = (pos2.y - pos1.y) * (pos.x - pos1.x)
            val onSameLine = left == right
            val between = if (pos1.x != pos2.x)
                (pos.x > pos1.x && pos.x < pos2.x) || (pos.x < pos1.x && pos.x > pos2.x)
            else
                (pos.y > pos1.y && pos.y < pos2.y) || (pos.y < pos1.y && pos.y > pos2.y)
            return onSameLine && between
        }
    }

    private data class RelativePosition(val original: Position, val normalized: Position) {
        val angle = atan2(normalized.y.toDouble(), normalized.x.toDouble()) * 180.0 / Math.PI
        val distance = sqrt(normalized.x.toDouble().pow(2) + normalized.y.toDouble().pow(2))

        override fun toString() = "$original angle: $angle dist: $distance"
    }

    private data class Position(val x: Int, val y: Int)
}