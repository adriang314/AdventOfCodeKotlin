package year2017

import common.BaseSolution
import common.List2D
import common.Position

fun main() = println(SolutionDay21().result())

class SolutionDay21 : BaseSolution() {

    override val day = 21

    private val rules = input().split("\r\n")
        .map { line ->
            val pattern1 = """(\W{2})/(\W{2}) => (\W{3})/(\W{3})/(\W{3})""".toRegex().find(line)
            val pattern2 = """(\W{3})/(\W{3})/(\W{3}) => (\W{4})/(\W{4})/(\W{4})/(\W{4})""".toRegex().find(line)

            if (pattern1 != null) {
                val (s1, s2, t1, t2, t3) = pattern1.destructured
                EnhancementRule(Pattern(listOf(s1.toList(), s2.toList())), Pattern(listOf(t1.toList(), t2.toList(), t3.toList())))
            } else if (pattern2 != null) {
                val (t1, t2, t3, f1, f2, f3, f4) = pattern2.destructured
                EnhancementRule(Pattern(listOf(t1.toList(), t2.toList(), t3.toList())), Pattern(listOf(f1.toList(), f2.toList(), f3.toList(), f4.toList())))
            } else {
                throw RuntimeException("Unknown pattern")
            }
        }

    private val startImage = """
        |.#.
        |..#
        |###
        """.trimMargin().split("\n").let { lines -> Image(lines.map { it.toList() }) }

    override fun task1(): String {
        var image = startImage
        repeat(5) {
            image = image.evaluate(rules)
        }

        return image.pixelsOn.toString()
    }

    override fun task2(): String {
        var image = startImage
        repeat(18) {
            image = image.evaluate(rules)
        }

        return image.pixelsOn.toString()
    }

    private class Image(private val cells: List<List<Char>>) {
        private constructor(pattern: Pattern) : this(pattern.value)

        var pixelsOn = cells.sumOf { line -> line.count { it == '#' } }

        fun evaluate(rules: List<EnhancementRule>): Image = when {
            cells.size % 2 == 0 -> evaluate(2, rules)
            cells.size % 3 == 0 -> evaluate(3, rules)
            else -> this
        }

        private fun evaluate(divisor: Int, rules: List<EnhancementRule>): Image {
            val topLeftCorners = (0 until cells.size step divisor).map { y -> (0 until cells.size step divisor).map { x -> Position(x, y) } }.flatten()
            val dividedSquares = topLeftCorners.associateWith { point ->
                Image((0 until divisor).map { i -> cells[point.y + i].filterIndexed { idx, _ -> idx in point.x until point.x + divisor } }.toList())
            }

            val enhancedSquares = dividedSquares.map { entry ->
                val applicableRules = rules.filter { it.from.size == divisor && it.from.pixelsOn == entry.value.pixelsOn }
                val matching = applicableRules.filter { rule -> entry.value.matchesPattern(rule.from) }
                val topLeftCorner = Position(entry.key.x + (entry.key.x / divisor), entry.key.y + (entry.key.y / divisor))
                topLeftCorner to Image(matching.single().to)
            }.toMap()

            val evaluatedImageSize = (cells.size / divisor) * (divisor + 1)
            val evaluatedImage = Array(evaluatedImageSize) { Array(evaluatedImageSize) { ' ' } }

            enhancedSquares.forEach { (corner, square) ->
                square.cells.forEachIndexed { rowIdx, row ->
                    row.forEachIndexed { colIdx, char ->
                        evaluatedImage[rowIdx + corner.y][colIdx + corner.x] = char
                    }
                }
            }

            return Image(evaluatedImage.toList().map { it.toList() })
        }

        private fun matchesPattern(pattern: Pattern): Boolean = pattern.options.any { it == cells }
    }

    private data class EnhancementRule(val from: Pattern, val to: Pattern)

    private data class Pattern(val value: List<List<Char>>) {
        val size = value.size
        val pixelsOn = value.sumOf { line -> line.count { it == '#' } }

        val options by lazy {
            setOf(
                this.value,
                List2D.rotateRight(value, 1),
                List2D.rotateRight(value, 2),
                List2D.rotateRight(value, 3),

                List2D.flipVertically(value),
                List2D.rotateRight(List2D.flipVertically(value), 1),
                List2D.rotateRight(List2D.flipVertically(value), 2),
                List2D.rotateRight(List2D.flipVertically(value), 3),

                List2D.flipHorizontally(value),
                List2D.rotateRight(List2D.flipHorizontally(value), 1),
                List2D.rotateRight(List2D.flipHorizontally(value), 2),
                List2D.rotateRight(List2D.flipHorizontally(value), 3),
            )
        }

        override fun toString(): String = value.joinToString("\n") { it.joinToString("") }
    }
}