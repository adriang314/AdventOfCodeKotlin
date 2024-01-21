package common

import kotlin.math.max
import kotlin.math.min

/**
 * Ranges has intersection
 */
fun IntRange.hasIntersection(other: IntRange): Boolean {
    return this.intersection(other) != IntRange.EMPTY
}

/**
 * This range fully inside other range
 */
fun IntRange.inside(other: IntRange): Boolean {
    return this.first in other && this.last in other
}

/**
 * Finds intersection range between two ranges
 */
fun IntRange.intersection(other: IntRange): IntRange {
    val first = max(this.first, other.first)
    val last = min(this.last, other.last)
    return if (first <= last) first..last else IntRange.EMPTY
}

/**
 * Returns the length of the range - number of elements
 */
fun IntRange.length() = if (this == IntRange.EMPTY) 0 else this.last - this.first + 1
