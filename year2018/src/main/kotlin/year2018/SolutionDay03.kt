package year2018

import common.BaseSolution

fun main() = println(SolutionDay03().result())

class SolutionDay03 : BaseSolution() {

    override val day = 3
    
    private val regex = Regex("^#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)$")
    private val areas = input().split("\r\n").map {
        val (id, left, top, width, height) = regex.find(it)!!.destructured
        Area(id.toInt(), left.toInt(), top.toInt(), width.toInt(), height.toInt())
    }

    private val map: MutableMap<Position, Int> = mutableMapOf()

    init {
        areas.forEach { area ->
            area.positions.forEach { position ->
                map.compute(position) { _, v -> if (v == null) 1 else v + 1 }
            }
        }
    }

    override fun task1(): String {
        val result = map.filter { it.value > 1 }.count()
        return result.toString()
    }

    override fun task2(): String {
        val notOverlappingArea = areas.first { area -> area.positions.all { pos -> map[pos] == 1 } }
        return notOverlappingArea.id.toString()
    }

    private data class Area(val id: Int, val fromLeft: Int, val fromTop: Int, val width: Int, val height: Int) {
        val positions: List<Position> = (0 until width).map { x ->
            (0 until height).map { y ->
                Position(fromLeft + x, fromTop + y)
            }
        }.flatten().toList()
    }

    private data class Position(val x: Int, val y: Int)
}