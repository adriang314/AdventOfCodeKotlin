package year2015

import common.BaseSolution

fun main() = println(SolutionDay02().result())

class SolutionDay02 : BaseSolution() {

    override val day = 2

    private val prisms = input().split("\r\n").map { Prism.from(it) }

    override fun task1(): String {
        return prisms.sumOf { it.area() + it.slack() }.toString()
    }

    override fun task2(): String {
        return prisms.sumOf { it.ribbon() }.toString()
    }

    private data class Prism(val l: Int, val w: Int, val h: Int) {
        private val edges = listOf(l, w, h).sorted()
        fun area(): Long = (2L * l * w) + (2L * l * h) + (2L * w * h)
        fun slack(): Int = edges[0] * edges[1]
        fun ribbon(): Long = (2L * edges[0]) + (2L * edges[1]) + (l * w * h)

        companion object {
            private val regex = """^(\d+)x(\d+)x(\d+)$""".toRegex()

            fun from(input: String): Prism {
                val (l, w, h) = regex.matchEntire(input)!!.destructured
                return Prism(l.toInt(), w.toInt(), h.toInt())
            }
        }
    }
}