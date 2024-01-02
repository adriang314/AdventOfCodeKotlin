package year2023

import common.BaseSolution
import kotlin.math.max
import kotlin.math.min

fun main() = println(SolutionDay05().result())

class SolutionDay05 : BaseSolution() {

    override val day = 5

    override fun task1(): String {
        val task = Task(input(), false)
        return task.getLocationRanges().minBy { it.first }.first.toString()
    }

    override fun task2(): String {
        val task = Task(input(), true)
        return task.getLocationRanges().minBy { it.first }.first.toString()
    }

    class Task(input: String, extendedSeeds: Boolean) {
        private val numberRegex = Regex("(\\d+)")
        private val mapRegex = Regex("(\\d+) (\\d+) (\\d+)")
        private val seedRanges: List<LongRange>
        private val seedsToSoilMap: Mappings
        private val soilToFertilizerMap: Mappings
        private val fertilizerToWaterMap: Mappings
        private val waterToLightMap: Mappings
        private val lightToTemperatureMap: Mappings
        private val temperatureToHumidityMap: Mappings
        private val humidityToLocationMap: Mappings

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

            val seeds = numbers(numberRegex, inputParts[0])
            seedRanges = if (extendedSeeds)
                List(seeds.filterIndexed { i, _ -> i % 2 == 0 }.size) { i -> seeds[2 * i]..<seeds[2 * i] + seeds[2 * i + 1] }
            else
                seeds.map { it..it }.toList()
            seedsToSoilMap = buildMap(inputParts[1])
            soilToFertilizerMap = buildMap(inputParts[2])
            fertilizerToWaterMap = buildMap(inputParts[3])
            waterToLightMap = buildMap(inputParts[4])
            lightToTemperatureMap = buildMap(inputParts[5])
            temperatureToHumidityMap = buildMap(inputParts[6])
            humidityToLocationMap = buildMap(inputParts[7])
        }

        fun getLocationRanges(): List<LongRange> {
            val soilRanges = seedsToSoilMap.mapRanges(seedRanges)
            val fertilizerRanges = soilToFertilizerMap.mapRanges(soilRanges)
            val waterRanges = fertilizerToWaterMap.mapRanges(fertilizerRanges)
            val lightRanges = waterToLightMap.mapRanges(waterRanges)
            val temperatureRanges = lightToTemperatureMap.mapRanges(lightRanges)
            val humidityRanges = temperatureToHumidityMap.mapRanges(temperatureRanges)
            return humidityToLocationMap.mapRanges(humidityRanges)
        }

        private fun buildMap(input: String) =
            Mappings(input
                .split("\r\n", "\n")
                .map { mapRegex.findAll(it).toList() }
                .map { matches ->
                    matches.map {
                        val destinationIdx = it.groupValues[1].toLong()
                        val sourceIdx = it.groupValues[2].toLong()
                        val length = it.groupValues[3].toLong()
                        SingleMapping(
                            sourceIdx..<sourceIdx + length,
                            destinationIdx - sourceIdx
                        )
                    }
                }.flatten().toList()
            )

        class Mappings(mappings: List<SingleMapping>) {
            private val fullMappings: List<SingleMapping>

            init {
                val noShiftMappings = ArrayList<SingleMapping>(mappings.size + 1)
                val sortedMappings = mappings.sortedBy { it.range.first }
                for (i in sortedMappings.indices) {
                    val start = if (i > 0) (sortedMappings.getOrNull(i - 1)!!.range.last + 1) else 0
                    val end = sortedMappings[i].range.first - 1
                    if (start <= end)
                        noShiftMappings.add(SingleMapping(start..<end, 0))
                }

                val lastMapping = sortedMappings.last().range.last
                if (lastMapping < Long.MAX_VALUE)
                    noShiftMappings.add(SingleMapping(lastMapping + 1..Long.MAX_VALUE, 0L))

                fullMappings = mappings + noShiftMappings
            }

            fun mapRanges(ranges: List<LongRange>) = ranges.map { mapRanges(it) }.flatten()

            private fun mapRanges(range: LongRange): List<LongRange> {
                val mappedRanges = ArrayList<LongRange>(fullMappings.size)
                fullMappings.forEach { mapRange ->
                    if (range.first <= mapRange.range.last && range.last >= mapRange.range.first) {
                        val newRange = LongRange(
                            max(range.first, mapRange.range.first) + mapRange.shift,
                            min(range.last, mapRange.range.last) + mapRange.shift
                        )
                        mappedRanges.add(newRange)
                    }
                }
                return mappedRanges
            }
        }

        class SingleMapping(val range: LongRange, val shift: Long)

        private fun numbers(regex: Regex, line: String): List<Long> {
            val match = regex.findAll(line)
            return match.toList().stream().map { it.groupValues[1].toLong() }.toList()
        }
    }
}