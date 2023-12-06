package solution

class SolutionDay06 : BaseSolution() {

    override val day = 6

    override fun task1(): String {
        val rawLines = Line(input())
        return rawLines.resultTask1.toString()
    }

    override fun task2(): String {
        val rawLines = Line(input())
        return rawLines.resultTask2.toString()
    }

    class Line(input: String) {
        var resultTask1: Long
        var resultTask2: Long

        private val numberRegex = Regex("(\\d+)")

        init {
            val lines = input.split("\r\n", "\n")

            val timesTask1 = numbers(numberRegex, lines[0])
            val recordsTask1 = numbers(numberRegex, lines[1])
            val racesTask1 = timesTask1.mapIndexed { index, i -> Race(i, recordsTask1[index]) }.toList()
            resultTask1 = racesTask1.map { countRaceWithNewRecord(it) }.scan(1L) { acc, l -> acc * l }.last

            val timeTask2 = numbers(numberRegex, lines[0]).scan("") { acc, l -> acc + l.toString() }.last.toLong()
            val recordTask2 = numbers(numberRegex, lines[1]).scan("") { acc, l -> acc + l.toString() }.last.toLong()
            val raceTask2 = Race(timeTask2, recordTask2)
            resultTask2 = countRaceWithNewRecord(raceTask2)
        }

        private fun countRaceWithNewRecord(race: Race) =
            (0..<race.time)
                .mapIndexed { index, speed -> speed * (race.time - index) }
                .count { it > race.record }.toLong()

        private fun numbers(regex: Regex, line: String): List<Long> {
            val match = regex.findAll(line)
            return match.toList().stream().map { it.groupValues[1].toLong() }.toList()
        }

        data class Race(val time: Long, val record: Long)
    }
}