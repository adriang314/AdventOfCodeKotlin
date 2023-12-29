package year2023

import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    println("${SolutionDay22()}")
}

class SolutionDay22 : BaseSolution() {

    override val day = 22

    override fun task1(): String {
        val disintegrated = brickStack.disintegrated()
        return disintegrated.size.toString()
    }

    override fun task2(): String {
        val integrated = brickStack.integrated()
        val result = integrated.sumOf {
            val removed = mutableListOf<Brick>()
            it.removeBrick(removed)
            removed.size - 1
        }
        return result.toString()
    }

    private var brickStack = BrickStack()

    init {
        val rawLines = input().split("\r\n", "\n")
        val bricks = rawLines.mapIndexed { index, l -> Line(l, index).brick }
        bricks.sortedBy { it.minZ }.forEach { brickStack.addBrick(it) }
        brickStack.calcCanBeRemoved()
    }

    class BrickStack {
        private val stack = LinkedList<Brick>()
        private val zMinLevel = 1

        fun addBrick(brick: Brick) {
            val potentiallyBlockingBricks = stack.filter { brick.potentiallyBlocks(it) }.groupBy { it.maxZ }
            if (potentiallyBlockingBricks.isEmpty()) {
                stack.add(brick.fallTo(zMinLevel))
                return
            }

            val maxZ = potentiallyBlockingBricks.keys.sorted().last
            val allBlockingBricks = potentiallyBlockingBricks[maxZ]!!
            brick.supportedBy.addAll(allBlockingBricks)
            val newStackedBrick = brick.fallTo(maxZ + 1)
            allBlockingBricks.forEach { it.supporting.add(newStackedBrick) }
            stack.add(newStackedBrick)
        }

        fun calcCanBeRemoved() = stack.forEach { it.calcCanBeRemoved() }

        fun integrated() = stack.filter { it.canBeRemoved == false }

        fun disintegrated() = stack.filter { it.canBeRemoved == true }
    }

    class Line(l: String, rowIdx: Int) {
        val brick: Brick

        init {
            val result = regex.find(l)!!
            val x1 = result.groupValues[1].toInt()
            val y1 = result.groupValues[2].toInt()
            val z1 = result.groupValues[3].toInt()
            val x2 = result.groupValues[4].toInt()
            val y2 = result.groupValues[5].toInt()
            val z2 = result.groupValues[6].toInt()
            brick = Brick(ThreeDPoint(x1, y1, z1), ThreeDPoint(x2, y2, z2), rowIdx)
        }

        companion object {
            private val regex = Regex("^(\\d+),(\\d+),(\\d+)~(\\d+),(\\d+),(\\d+)")
        }
    }

    data class Brick(
        val corner1: ThreeDPoint, val corner2: ThreeDPoint, val idx: Int,
        val supportedBy: MutableList<Brick> = mutableListOf(),
        val supporting: MutableList<Brick> = mutableListOf()
    ) {
        val minZ = min(corner1.z, corner2.z)
        val maxZ = max(corner1.z, corner2.z)
        private val xyTiles: List<Tile>
        var canBeRemoved: Boolean? = null

        init {
            val tiles = mutableListOf<Tile>()
            for (x in corner1.x..corner2.x)
                for (y in corner1.y..corner2.y)
                    tiles.add(Tile(x, y))
            xyTiles = tiles
        }

        override fun toString(): String {
            val supportingText = supporting.map { it.idx }.joinToString(",")
            val supportedByText = supportedBy.map { it.idx }.joinToString(",")
            return "$corner1 $corner2 (Idx: $idx) supporting: $supportingText supportedBy: $supportedByText"
        }

        fun removeBrick(removedBricks: MutableList<Brick>, checkIfFall: Boolean = false) {
            val suppBy: Int?
            if (!checkIfFall) {
                suppBy = 0
                removedBricks.add(this)
            } else {
                suppBy = supportedBy.filter { !removedBricks.contains(it) }.size
                if (suppBy == 0)
                    removedBricks.add(this)
            }

            if (suppBy == 0) {
                for (supp in supporting) {
                    supp.removeBrick(removedBricks, true)
                }
            }
        }

        fun calcCanBeRemoved() {
            if (supporting.isEmpty())
                canBeRemoved = true
            canBeRemoved = supporting.all { it.supportedBy.size > 1 }
        }

        fun fallTo(targetZ: Int): Brick {
            return if (targetZ == minZ)
                this
            else {
                val zDiff = corner2.z - corner1.z
                Brick(corner1.withZ(targetZ), corner2.withZ(targetZ + zDiff), idx, supportedBy, supporting)
            }
        }

        fun potentiallyBlocks(other: Brick) = xyTiles.any { other.xyTiles.contains(it) }
    }

    data class Tile(val x: Int, val y: Int)

    data class ThreeDPoint(val x: Int, val y: Int, val z: Int) {
        fun withZ(newZ: Int) = ThreeDPoint(x, y, newZ)
    }
}