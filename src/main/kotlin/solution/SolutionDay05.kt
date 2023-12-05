package solution

class SolutionDay05 : BaseSolution() {

    override val day = 5

    override fun task1(): String {
        val task = Task(input())
        return task.seedList.minOfOrNull { task.getLocationIdx(it) }.toString()
    }

    override fun task2(): String {
        val task = Task(input())
        var minLocationIdx = Long.MAX_VALUE
        for (i in task.seedList.indices step 2) {
            val from = task.seedList[i]
            val len = task.seedList[i + 1]
            for (seedIdx in from..<from + len) {
                val locationIdx = task.getLocationIdx(seedIdx)
                if (minLocationIdx > locationIdx)
                    minLocationIdx = locationIdx
            }
        }

        return minLocationIdx.toString()
    }

    class Task(input: String) {
        private val numberRegex = Regex("(\\d+)")
        private val mapRegex = Regex("(\\d+) (\\d+) (\\d+)")
        val seedList: List<Long>
        private val seedsToSoilMap: MapEntries
        private val soilToFertilizerMap: MapEntries
        private val fertilizerToWaterMap: MapEntries
        private val waterToLightMap: MapEntries
        private val lightToTemperatureMap: MapEntries
        private val temperatureToHumidityMap: MapEntries
        private val humidityToLocationMap: MapEntries

        init {
            val inputParts = input.split(
                "seeds:",
                "seed-to-soil map:",
                "soil-to-fertilizer map:",
                "fertilizer-to-water map:",
                "water-to-light map:",
                "light-to-temperature map:",
                "temperature-to-humidity map:",
                "humidity-to-location map:"
            ).minus("")

            seedList = numbers(numberRegex, inputParts[0])
            seedsToSoilMap = buildMapEntries(inputParts[1])
            soilToFertilizerMap = buildMapEntries(inputParts[2])
            fertilizerToWaterMap = buildMapEntries(inputParts[3])
            waterToLightMap = buildMapEntries(inputParts[4])
            lightToTemperatureMap = buildMapEntries(inputParts[5])
            temperatureToHumidityMap = buildMapEntries(inputParts[6])
            humidityToLocationMap = buildMapEntries(inputParts[7])
        }

        fun getLocationIdx(seedIdx: Long): Long {
            val soilIdx = seedsToSoilMap.valueFor(seedIdx)
            val fertilizerIdx = soilToFertilizerMap.valueFor(soilIdx)
            val waterIdx = fertilizerToWaterMap.valueFor(fertilizerIdx)
            val lightIdx = waterToLightMap.valueFor(waterIdx)
            val temperatureIdx = lightToTemperatureMap.valueFor(lightIdx)
            val humidityIndex = temperatureToHumidityMap.valueFor(temperatureIdx)
            return humidityToLocationMap.valueFor(humidityIndex)
        }

        private fun buildMapEntries(input: String) =
            MapEntries(input
                .split("\r\n", "\n")
                .map { mapRegex.findAll(it).toList() }
                .map { matches ->
                    matches.map {
                        MapEntry(
                            it.groupValues[1].toLong(),
                            it.groupValues[2].toLong(),
                            it.groupValues[3].toLong()
                        )
                    }
                }.flatten().toList()
            )

        data class MapEntries(val mapEntries: List<MapEntry>) {
            fun valueFor(idx: Long): Long {
                for (entry in mapEntries) {
                    val maxIdx = entry.sourceIdx + entry.length
                    if (idx >= entry.sourceIdx && idx < maxIdx)
                        return entry.destinationIdx + idx - entry.sourceIdx
                }

                return idx
            }
        }

        data class MapEntry(val destinationIdx: Long, val sourceIdx: Long, val length: Long)

        private fun numbers(regex: Regex, line: String): List<Long> {
            val match = regex.findAll(line)
            return match.toList().stream().map { it.groupValues[1].toLong() }.toList()
        }
    }
}