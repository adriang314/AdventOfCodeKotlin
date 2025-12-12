package year2025

import common.BaseSolution

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {

    override val day = 12

    private val presents: List<Present>
    private val regions: List<Region>

    init {
        val lines = input().split("\r\n")
        val idRegex = """^(\d):$""".toRegex()
        val regionRegex = """(\d+)x(\d+): (\d+) (\d+) (\d+) (\d+) (\d+) (\d+)""".toRegex()
        val tmpPresents = mutableListOf<Present>()
        val tmpRegions = mutableListOf<Region>()
        var tmpLine = mutableListOf<List<Char>>()
        var id = 0

        lines.forEach { line ->
            if (idRegex.matches(line)) {
                val (idx) = idRegex.find(line)!!.destructured
                id = idx.toInt()
            } else if (line.contains('#') || line.contains('.')) {
                tmpLine.add(line.toList())
            } else if (line.contains('x')) {
                val (width, height, p0, p1, p2, p3, p4, p5) = regionRegex.find(line)!!.destructured
                tmpRegions.add(Region(width.toInt(), height.toInt(), listOf(p0.toInt(), p1.toInt(), p2.toInt(), p3.toInt(), p4.toInt(), p5.toInt())))
            } else {
                tmpPresents.add(Present(id, tmpLine.toList()))
                tmpLine.clear()
            }
        }

        presents = tmpPresents.toList()
        regions = tmpRegions.toList()
    }

    override fun task1(): String {
        val result = regions.count { it.canFit(presents) }
        return result.toString()
    }

    override fun task2(): String {
        return ""
    }

    private data class Region(val width: Int, val height: Int, val presentsToFit: List<Int>) {
        val size = width * height

        fun canFit(presents: List<Present>): Boolean {
            val presentsSize = presentsToFit.mapIndexed { idx, presentCount ->
                val present = presents.first { it.id == idx }
                presentCount * present.size
            }.sum()

            return size >= presentsSize
        }
    }

    private data class Present(val id: Int, val details: List<List<Char>>) {
        val size = details.sumOf { r -> r.count { it == '#' } }
    }
}