package common

import kotlin.math.max
import kotlin.math.min

/**
 * Ranges have intersection
 */
fun IntRange.hasIntersection(other: IntRange): Boolean {
    return this.intersection(other) != IntRange.EMPTY
}

/**
 * Ranges have intersection
 */
fun LongRange.hasIntersection(other: LongRange): Boolean {
    return this.intersection(other) != LongRange.EMPTY
}

/**
 * This range is fully inside the other range
 */
fun IntRange.inside(other: IntRange): Boolean {
    return this.first in other && this.last in other
}

/**
 * This range is fully inside the other range
 */
fun LongRange.inside(other: LongRange): Boolean {
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
 * Finds intersection range between two ranges
 */
fun LongRange.intersection(other: LongRange): LongRange {
    val first = max(this.first, other.first)
    val last = min(this.last, other.last)
    return if (first <= last) LongRange(first, last) else LongRange.EMPTY
}

/**
 * Returns the length of the range - number of elements
 */
fun IntRange.length() = if (this == IntRange.EMPTY) 0 else this.last - this.first + 1

/**
 * Returns the length of the range - number of elements
 */
fun LongRange.length() = if (this == LongRange.EMPTY) 0L else this.last - this.first + 1L