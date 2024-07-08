package year2021

import common.BaseSolution
import kotlin.math.abs
import year2021.SolutionDay19.OrientationType.*

fun main() = println(SolutionDay19().result())

class SolutionDay19 : BaseSolution() {

    override val day = 19
    override val year = 2021

    override fun task1(): String {
        val size = scanners.map { it.points }.flatten().toSet().size
        return size.toString()
    }

    override fun task2(): String {
        val maxAbsDiffSum = scanners.map { s1 ->
            scanners.mapNotNull { s2 ->
                if (s1 == s2)
                    null
                else
                    s1.locationVsS0!!.absDiff(s2.locationVsS0!!).diff
            }
        }.flatten().maxOf { it.x() + it.y() + it.z() }
        return maxAbsDiffSum.toString()
    }

    private var scanners: List<Scanner>

    init {
        val scannersData = input().split("\r\n\r\n")
        val regex = Regex("--- scanner (\\d+) ---")
        scanners = scannersData.map {
            val lines = it.split("\r\n")
            val (id) = regex.find(lines[0])!!.destructured
            val points = lines.drop(1).map { line ->
                val lineParts = line.split(",")
                Point(lineParts[0].toInt(), lineParts[1].toInt(), lineParts[2].toInt())
            }
            Scanner(id.toInt(), points)
        }

        val scanner0 = scanners.first()
        scanner0.orientationVsS0 = Orientation.SSS
        scanner0.rotationVsS0 = Point.Rotation.XYZ
        scanner0.locationVsS0 = Point.ZERO

        findScannerLocations()
    }

    private fun findScannerLocations() {
        val rotations = Point.Rotation.values()
        while (scanners.any { it.locationVsS0 == null }) {
            var currScanner: Scanner? = null
            var sharedPoints: List<Pair<Scanner, Set<Point>>> = emptyList()
            for (scanner in scanners.filter { it.locationVsS0 != null }) {
                for (rotationIdx in rotations.indices) {
                    sharedPoints = findSharedPointsWith(scanner, rotations[rotationIdx])
                    if (sharedPoints.isNotEmpty())
                        break
                }

                if (sharedPoints.isNotEmpty()) {
                    currScanner = scanner
                    break
                }
            }

            if (currScanner == null)
                throw Exception()

            sharedPoints.forEach { pair ->
                val (otherScanner, diffs) = pair
                remapPoints(currScanner, otherScanner, diffs)
            }
        }
    }

    private fun remapPoints(s1: Scanner, s2: Scanner, s1s2Diffs: Set<Point>) {
        val s1SharedDiffsMap = s1.absDiffsMap.filter { it.key in s1s2Diffs }
        val s2SharedDiffsMap = s2.absDiffsMap.filter { it.key in s1s2Diffs }

        val mapping = mutableMapOf<Point, Point>()
        s1SharedDiffsMap.forEach { s1SharedDiff ->
            // find correct mapping between beacons using alternate diff that uses s1 beacon 1
            val s1Beacon1 = s1SharedDiff.value.point1
            val s1Beacon2 = s1SharedDiff.value.point2
            val s2Beacon1 = s2SharedDiffsMap[s1SharedDiff.key]!!.point1
            val s2Beacon2 = s2SharedDiffsMap[s1SharedDiff.key]!!.point2

            if (!mapping.containsKey(s1Beacon1)) {
                val s1AltDiff = s1.absDiffsMap
                    .filter {
                        it.key in s1s2Diffs && it != s1SharedDiff && (it.value.point1 == s1Beacon1 || it.value.point2 == s1Beacon1)
                    }.entries.first()
                val s2AltDiff = s2.absDiffsMap[s1AltDiff.key]!!
                if (s2Beacon1 == s2AltDiff.point1 || s2Beacon1 == s2AltDiff.point2) {
                    mapping[s1Beacon1] = s2Beacon1
                    mapping[s1Beacon2] = s2Beacon2
                } else {
                    mapping[s1Beacon1] = s2Beacon2
                    mapping[s1Beacon2] = s2Beacon1
                }
            }
        }

        val pair1 = mapping.entries.first()
        val pair2 = mapping.entries.last()

        val xOType = if (pair1.key.x() - pair2.key.x() == pair1.value.x() - pair2.value.x()) Same else Opposite
        val yOType = if (pair1.key.y() - pair2.key.y() == pair1.value.y() - pair2.value.y()) Same else Opposite
        val zOType = if (pair1.key.z() - pair2.key.z() == pair1.value.z() - pair2.value.z()) Same else Opposite

        s2.orientationVsS0 = Orientation.with(xOType, yOType, zOType)
        s2.locationVsS0 = pair1.key.zeroShift(pair1.value, s2.orientationVsS0!!)
        s2.rotationVsS0 = s1.rotationVsS0

        s2.remapToS0()
    }

    private fun findSharedPointsWith(scanner: Scanner, rotation: Point.Rotation): List<Pair<Scanner, Set<Point>>> {
        return scanners.filter { it != scanner && it.locationVsS0 == null }.map { otherScanner ->
            otherScanner.applyRotation(rotation)
            Pair(otherScanner, scanner.diffs().intersect(otherScanner.diffs()))
        }.filter { it.second.size >= 12 }
    }

    data class Scanner(val id: Int, var points: List<Point>) {
        var locationVsS0: Point? = null
        var rotationVsS0: Point.Rotation? = null
        var orientationVsS0: Orientation? = null
        var absDiffsMap = calcAbsDiffs()

        fun applyRotation(rotation: Point.Rotation) {
            points.forEach { it.rotation = rotation }
            absDiffsMap = calcAbsDiffs()
        }

        fun remapToS0() {
            if (locationVsS0 != null && locationVsS0!! != Point.ZERO) {
                points = points.map { locationVsS0!!.remap(it, orientationVsS0!!) }
                orientationVsS0 = Orientation.SSS
                rotationVsS0 = Point.Rotation.XYZ
                absDiffsMap = calcAbsDiffs()
            }
        }

        fun diffs() = absDiffsMap.keys

        private fun calcAbsDiffs(): Map<Point, PointAbsDiff> {
            return points.mapIndexed { i, beacon1 ->
                points.filterIndexed { j, _ -> j > i }.map { beacon2 -> beacon1.absDiff(beacon2) }
            }.flatten().associateBy { it.diff }
        }
    }

    data class Point(
        private val x: Int,
        private val y: Int,
        private val z: Int,
        var rotation: Rotation = Rotation.XYZ
    ) {
        fun x() = rotation.x.invoke(this)
        fun y() = rotation.y.invoke(this)
        fun z() = rotation.z.invoke(this)

        fun absDiff(other: Point): PointAbsDiff {
            val diff = Point(abs(this.x() - other.x()), abs(this.y() - other.y()), abs(this.z() - other.z()))
            return PointAbsDiff(this, other, diff)
        }

        fun remap(other: Point, orientation: Orientation) = Point(
            if (orientation.x == Same) this.x() + other.x() else this.x() - other.x(),
            if (orientation.y == Same) this.y() + other.y() else this.y() - other.y(),
            if (orientation.z == Same) this.z() + other.z() else this.z() - other.z(),
        )

        fun zeroShift(other: Point, orientation: Orientation) = Point(
            if (orientation.x == Same) this.x() - other.x() else this.x() + other.x(),
            if (orientation.y == Same) this.y() - other.y() else this.y() + other.y(),
            if (orientation.z == Same) this.z() - other.z() else this.z() + other.z(),
        )

        companion object {
            val ZERO = Point(0, 0, 0)
        }

        enum class Rotation(val x: (Point) -> Int, val y: (Point) -> Int, val z: (Point) -> Int) {
            XYZ({ point: Point -> point.x }, { point: Point -> point.y }, { point: Point -> point.z }),
            XZY({ point: Point -> point.x }, { point: Point -> point.z }, { point: Point -> point.y }),
            YXZ({ point: Point -> point.y }, { point: Point -> point.x }, { point: Point -> point.z }),
            YZX({ point: Point -> point.y }, { point: Point -> point.z }, { point: Point -> point.x }),
            ZXY({ point: Point -> point.z }, { point: Point -> point.x }, { point: Point -> point.y }),
            ZYX({ point: Point -> point.z }, { point: Point -> point.y }, { point: Point -> point.x }),
        }
    }

    data class PointAbsDiff(val point1: Point, val point2: Point, val diff: Point)

    enum class Orientation(val x: OrientationType, val y: OrientationType, val z: OrientationType) {
        SSS(Same, Same, Same),
        SSO(Same, Same, Opposite),
        SOS(Same, Opposite, Same),
        OSS(Opposite, Same, Same),
        SOO(Same, Opposite, Opposite),
        OOS(Opposite, Opposite, Same),
        OSO(Opposite, Same, Opposite),
        OOO(Opposite, Opposite, Opposite);

        companion object {
            fun with(xType: OrientationType, yType: OrientationType, zType: OrientationType) =
                Orientation.values().first { it.x == xType && it.y == yType && it.z == zType }
        }
    }

    enum class OrientationType { Same, Opposite }
}
