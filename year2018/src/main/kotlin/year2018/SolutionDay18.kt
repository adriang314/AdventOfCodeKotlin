package year2018

import common.BaseSolution
import common.Cell
import common.Grid
import common.Position

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {

    override val day = 18

    override fun task1(): String {
        val map = Grid(input()) { value: Char, position: Position -> Area(position, value) }
        repeat(10) {
            map.cells.forEach { it.prepareTransformation() }
            map.cells.forEach { it.transform() }
        }

        val result = calculateResourceValue(map)
        return result.toString()
    }


    override fun task2(): String {
        val map = Grid(input()) { value: Char, position: Position -> Area(position, value) }
        val mapCache = mutableMapOf<Int, String>() // key = iteration, value = map as text

        // transforming till there are three replicas of the map in different iterations
        var transformation = 0
        while (mapCache.entries.groupBy { it.value }.all { it.value.size < 3 }) {
            map.cells.forEach { it.prepareTransformation() }
            map.cells.forEach { it.transform() }
            mapCache[++transformation] = map.toString()
        }

        // calculating replication metrics
        val replications = mapCache.entries.groupBy { it.value }.filter { it.value.size > 1 }
        val replicationSize = replications.size
        val replicationStart = replications.mapValues { it.value.map { i -> i.key } }.minOf { it.value.min() }
        val requiredTransformations = 1_000_000_000
        val fullReplicas = (requiredTransformations - replicationStart) / replicationSize
        val reminder = (requiredTransformations - replicationStart - (fullReplicas * replicationSize))

        // getting map after required transformations
        val resultMapAsText = mapCache[replicationStart + reminder]!!
        val resultMap = Grid(resultMapAsText) { value: Char, position: Position -> Area(position, value) }

        val result = calculateResourceValue(resultMap)
        return result.toString()
    }

    private fun calculateResourceValue(map: Grid<Area>): Int {
        val woodenAcres = map.cells.count { it.trees() }
        val lumberyards = map.cells.count { it.lumberyard() }
        return woodenAcres * lumberyards
    }

    private class Area(position: Position, value: Char) : Cell<Area>(position, value) {
        private var transformedValue: Char? = null

        fun lumberyard() = value == '#'
        fun openGround() = value == '.'
        fun trees() = value == '|'

        private fun neighbourTressCount() = neighboursAll().count { it.trees() }
        private fun neighbourLumberyardCount() = neighboursAll().count { it.lumberyard() }

        fun prepareTransformation() {
            transformedValue = when {
                openGround() -> if (neighbourTressCount() >= 3) '|' else '.'
                trees() -> if (neighbourLumberyardCount() >= 3) '#' else '|'
                lumberyard() -> if (neighbourLumberyardCount() >= 1 && neighbourTressCount() >= 1) '#' else '.'
                else -> throw RuntimeException("Unknown area type")
            }
        }

        fun transform() {
            value = transformedValue!!
            transformedValue = null
        }
    }
}
