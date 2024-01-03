package year2022

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import com.google.common.collect.RangeSet
import com.google.common.collect.TreeRangeSet
import common.BaseSolution
import kotlin.math.abs

fun main() = println(SolutionDay15().result())

class SolutionDay15 : BaseSolution() {
    override val day = 15
    override val year = 2022

    override fun task1(): String {
        val marked = (sensorsMinX..sensorsMaxX).map { x ->
            sensorsWithBeacons.all { it.notSensorOrBeacon(x, 2000000L) } &&
                    sensorsWithBeacons.any { it.isInCoveredArea(x, 2000000L) }
        }.count { it }
        return marked.toString()
    }

    override fun task2(): String {
        val areaRanges = (0L..4000000L).associateWith {
            val rangeSet: RangeSet<Long> = TreeRangeSet.create()
            rangeSet.add(Range.closed(0L, 4000000L))
            rangeSet
        }
        println("created area ranges")

        sensorsWithBeacons.forEach {
            it.coveredArea.forEach { rng ->
                if (areaRanges.containsKey(rng.key))
                    areaRanges[rng.key]!!.removeAll(rng.value)
            }
            println("removed area coverage for sensor ${it.sensor}")
        }
        println("removed sensors coverage")

        val distressBeacon = areaRanges.filter { !it.value.isEmpty }
            .filter {
                val span = it.value.span()
                val diff = span.upperEndpoint() - span.lowerEndpoint()
                diff > 1 || span.upperBoundType() == BoundType.CLOSED || span.lowerBoundType() == BoundType.CLOSED
            }.entries.first()

        val y = distressBeacon.key
        val x = distressBeacon.value.span().upperEndpoint() - 1L
        val result = x * 4000000L + y
        return result.toString()
    }

    private var sensorsWithBeacons: List<SensorWithBeacon>
    private val sensorsMinX: Long
    private val sensorsMaxX: Long

    init {
        val regex = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
        sensorsWithBeacons = input().split("\r\n").map { line ->
            val match = regex.find(line)!!
            val sensor = Point(match.groupValues[1].toLong(), match.groupValues[2].toLong())
            val beacon = Point(match.groupValues[3].toLong(), match.groupValues[4].toLong())
            SensorWithBeacon(sensor, beacon)
        }

        sensorsMinX = sensorsWithBeacons.minOf { it.minX }
        sensorsMaxX = sensorsWithBeacons.maxOf { it.maxX }
    }

    data class SensorWithBeacon(val sensor: Point, val beacon: Point) {
        var coveredArea: MutableMap<Long, RangeSet<Long>> = mutableMapOf()
        private val range = abs(sensor.x - beacon.x) + abs(sensor.y - beacon.y)
        val minX = sensor.x - range
        val maxX = sensor.x + range

        init {
            (0L..range).forEach { idx ->
                val rangeSet: RangeSet<Long> = TreeRangeSet.create()
                if (idx == 0L) {
                    rangeSet.add(Range.closed(minX, maxX))
                    coveredArea[sensor.y] = rangeSet
                } else {
                    rangeSet.add(Range.closed(minX + idx, maxX - idx))
                    coveredArea[sensor.y - idx] = rangeSet
                    coveredArea[sensor.y + idx] = rangeSet
                }
            }

            println("created coverage area for sensor: $sensor")
        }

        fun notSensorOrBeacon(x: Long, y: Long) =
            (sensor.x != x || sensor.y != y) && (beacon.x != x || beacon.y != y)

        fun isInCoveredArea(x: Long, y: Long) =
            abs(sensor.x - x) + abs(sensor.y - y) <= range
    }

    data class Point(val x: Long, val y: Long)
}