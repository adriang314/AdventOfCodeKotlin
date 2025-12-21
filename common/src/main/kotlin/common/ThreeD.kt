package common

import kotlin.math.abs

data class Point3D(val x: Long, val y: Long, val z: Long) {

    fun shiftAll(shift: Long) = Point3D(x + shift, y + shift, z + shift)

    fun shift(shift: Point3D) = Point3D(x + shift.x, y + shift.y, z + shift.z)

    fun shiftX(shift: Long) = Point3D(x + shift, y, z)

    fun shiftY(shift: Long) = Point3D(x, y + shift, z)

    fun shiftZ(shift: Long) = Point3D(x, y, z + shift)

    fun distanceTo(other: Point3D) = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)

    fun euclideanDistance(other: Point3D): Double {
        val dx = (x - other.x).toDouble()
        val dy = (y - other.y).toDouble()
        val dz = (z - other.z).toDouble()
        return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
    }
}

class Parallelogram(val x: LongRange, val y: LongRange, val z: LongRange) {

    fun intersection(other: Parallelogram): Parallelogram? {
        if (!hasIntersection(other))
            return null

        return Parallelogram(x.intersection(other.x), y.intersection(other.y), z.intersection(other.z))
    }

    fun hasIntersection(other: Parallelogram) =
        x.hasIntersection(other.x) && y.hasIntersection(other.y) && z.hasIntersection(other.z)

    fun isInside(other: Parallelogram) =
        x.inside(other.x) && y.inside(other.y) && z.inside(other.z)

    override fun toString() = "x:$x,y:$y,z:$z"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Parallelogram) return false
        if (x != other.x || y != other.y || z != other.z) return false
        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }
}

open class Sphere(val position: Point3D, val range: Long) {

    fun contains(other: Point3D): Boolean {
        return position.distanceTo(other) <= range
    }

    fun contains(other: Sphere): Boolean {
        return position.distanceTo(other.position) + other.range <= range
    }

    fun hasIntersection(other: Sphere): Boolean {
        return position.distanceTo(other.position) <= range + other.range
    }

    fun toParallelogram(): Parallelogram {
        val xRange = position.x - range..position.x + range
        val yRange = position.y - range..position.y + range
        val zRange = position.z - range..position.z + range
        return Parallelogram(xRange, yRange, zRange)
    }

    override fun toString() = "pos:$position,r:$range"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Sphere) return false
        if (position != other.position || range != other.range) return false
        return true
    }

    override fun hashCode(): Int {
        var result = position.hashCode()
        result = 31 * result + range.hashCode()
        return result
    }
}