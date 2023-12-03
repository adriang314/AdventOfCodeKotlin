package solution

class SolutionDay03 : BaseSolution() {

    override val day = 3

    override fun task1(): String {
        val rawLines = input().split("\n")
        val lines = rawLines.map { Line(it) }.toList()
        return process(lines, ::processLineTask1).toString()
    }

    override fun task2(): String {
        val rawLines = input().split("\n")
        val lines = rawLines.map { Line(it) }.toList()
        return process(lines, ::processLineTask2).toString()
    }

    private fun process(lines: List<Line>, processLine: (Line, Line?, Line?) -> Int) =
        lines.indices.sumOf { idx -> processLine(lines[idx], lines.getOrNull(idx - 1), lines.getOrNull(idx + 1)) }

    private fun processLineTask1(line: Line, before: Line?, after: Line?) =
        line.numbers.sumOf { number ->
            if (line.specialIdx.any { idx -> idx in (number.first.first - 1)..(number.first.last + 1) })
                number.second
            else if (before != null && before.specialIdx.any { idx -> idx in (number.first.first - 1)..(number.first.last + 1) })
                number.second
            else if (after != null && after.specialIdx.any { idx -> idx in (number.first.first - 1)..(number.first.last + 1) })
                number.second
            else 0
        }

    private fun processLineTask2(line: Line, before: Line?, after: Line?) =
        line.gearIdx.sumOf { idx ->
            val thisMatches = line.numbers.filter { no -> idx in (no.first.first - 1)..(no.first.last + 1) }
                .map { it.second }
            val beforeMatches = before?.numbers?.filter { no -> idx in (no.first.first - 1)..(no.first.last + 1) }
                ?.map { it.second } ?: emptyList()
            val afterMatches = after?.numbers?.filter { no -> idx in (no.first.first - 1)..(no.first.last + 1) }
                ?.map { it.second } ?: emptyList()
            val total = thisMatches + beforeMatches + afterMatches
            if (total.size == 2)
                total[0] * total[1]
            else 0
        }

    private class Line(line: String) {
        val numbers: List<Pair<IntRange, Int>>
        val specialIdx: List<Int>
        val gearIdx: List<Int>

        private val numberRegex = Regex("(\\d+)")
        private val specialRegex = Regex("([^\\d.\r])")
        private val gearRegex = Regex("(\\*)")

        init {
            specialIdx = findAll(specialRegex, line).map { it.first.first }
            numbers = findAll(numberRegex, line).map { Pair(it.first, it.second.toInt()) }
            gearIdx = findAll(gearRegex, line).map { it.first.first }
        }

        private fun findAll(regex: Regex, line: String): List<Pair<IntRange, String>> {
            val match = regex.findAll(line)
            return match.toList().stream().map { Pair(it.range, it.groupValues[1]) }.toList()
        }
    }
}