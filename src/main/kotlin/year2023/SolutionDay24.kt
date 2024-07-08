package year2023

import common.BaseSolution

fun main() = println(SolutionDay24().result())

class SolutionDay24 : BaseSolution() {

    override val day = 24

    override fun task1(): String {
        val xMin = 200000000000000
        val xMax = 400000000000000
        val yMin = 200000000000000
        val yMax = 400000000000000

        var crossed = 0
        for (i in hailStones.indices) {
            for (j in i + 1 until hailStones.size) {
                val h1 = hailStones[i]
                val h2 = hailStones[j]
                val cross = Cross(h1, h2)
                if (cross.inPast == false && cross.inTestArea(xMin, xMax, yMin, yMax))
                    crossed++

            }
        }

        return crossed.toString()
    }

    override fun task2(): String {
        val equations = hailStones.take(3).mapIndexed { i, hailStone ->
            "t$i >= 0, " +
                    "${hailStone.p1.x} + ${hailStone.velocity.x}*t$i == x + vx*t$i, " +
                    "${hailStone.p1.y} + ${hailStone.velocity.y}*t$i == y + vy*t$i, " +
                    "${hailStone.p1.z} + ${hailStone.velocity.z}*t$i == z + vz*t$i"

        }.joinToString(", ")

//        mathematica result
//        {{x -> 239756157786030, y -> 463222539161932, z -> 273997500449219,
//            vx -> 47, vy -> -360, vz -> 18, t0 -> 666003776903,
//            t1 -> 654152070134, t2 -> 779453185471}}

        return "976976197397181 = Solve[{$equations}, {x,y,z,vx,vy,vz,t0,t1,t2}]"
    }

    private var hailStones: List<HailStone>

    init {
        val rawLines = input().split("\r\n", "\n")
        hailStones = rawLines.mapIndexed { rowIdx, l -> Line(l, rowIdx).hailStone }
    }

    class Line(l: String, rowIdx: Int) {
        val hailStone: HailStone

        init {
            val result = regex.find(l)!!
            val x1 = result.groupValues[1].toLong()
            val y1 = result.groupValues[2].toLong()
            val z1 = result.groupValues[3].toLong()
            val x2 = result.groupValues[4].toLong()
            val y2 = result.groupValues[5].toLong()
            val z2 = result.groupValues[6].toLong()
            hailStone = HailStone(ThreeDPoint(x1, y1, z1), VelocityChange(x2, y2, z2), rowIdx)
        }

        companion object {
            private val regex = Regex("^(-?\\d+),\\s+(-?\\d+),\\s+(-?\\d+)\\s+@\\s+(-?\\d+),\\s+(-?\\d+),\\s+(-?\\d+)")
        }
    }

    data class Cross(val h1: HailStone, val h2: HailStone) {
        private val x: Double?
        private val y: Double?
        val inPast: Boolean?

        init {
            if (!h1.isParallelTo(h2)) {
                x = (h1.b - h2.b) / (h2.a - h1.a)
                y = h1.a * x + h1.b
                inPast = !(h1.notInPast(x, y) && h2.notInPast(x, y))
            } else {
                x = null
                y = null
                inPast = null
            }
        }

        fun inTestArea(xMin: Long, xMax: Long, yMin: Long, yMax: Long): Boolean {
            if (x == null || y == null)
                return false
            return x >= xMin && x <= xMax && y >= yMin && y <= yMax
        }

        override fun toString() =
            "[${h1.rowIdx} with ${h2.rowIdx}] x = $x y = $y" + if (inPast == true) " PAST " else ""
    }

    data class HailStone(
        val p1: ThreeDPoint,
        val velocity: VelocityChange,
        val rowIdx: Int = 0
    ) {
        private val p2 = p1.next(velocity)
        private val xDiff = p1.x - p2.x
        private val yDiff = p1.y - p2.y
        val a = 1.0 * yDiff / xDiff
        val b = p1.y - p1.x * a
        // y = a*x + b

        fun notInPast(x: Double, y: Double): Boolean {
            if (xDiff >= 0 && yDiff >= 0)
                return x <= p1.x && y <= p1.y
            if (xDiff >= 0) //  && yDiff < 0
                return x <= p1.x && y > p1.y
            return if (yDiff >= 0) // xDiff < 0 &&
                x > p1.x && y <= p1.y
            else
                x > p1.x && y > p1.y
        }

        fun isParallelTo(other: HailStone) = a == other.a

        override fun toString() = "[$rowIdx] a = $a b = $b "
    }

    data class ThreeDPoint(val x: Long, val y: Long, val z: Long) {
        fun next(change: VelocityChange) =
            ThreeDPoint(x + change.x, y + change.y, z + change.z)
    }

    data class VelocityChange(val x: Long, val y: Long, val z: Long)
}