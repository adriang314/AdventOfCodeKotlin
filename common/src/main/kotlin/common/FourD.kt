package common

import kotlin.math.abs

data class Point4D(val x: Long, val y: Long, val z: Long, val t: Long) {
    fun distanceTo(other: Point4D) = abs(x - other.x) + abs(y - other.y) + abs(z - other.z) + abs(t - other.t)
}