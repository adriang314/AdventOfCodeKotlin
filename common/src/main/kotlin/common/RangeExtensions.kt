package common

import kotlin.math.max
import kotlin.math.min

/**
 * Find middle value of the range.
 * In case of even number of items, returns lower one.
 */
fun IntRange.midValue(): Int {
    return (this.last + this.first) / 2
}

/**
 * Find middle value of the range.
 * In case of even number of items, returns lower one.
 */
fun LongRange.midValue(): Long {
    return (this.last + this.first) / 2L
}

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
 * Subtracts other range from this range and returns list of remaining ranges
 */
fun IntRange.difference(other: IntRange): List<IntRange> {
    val result = mutableListOf<IntRange>()
    if (!this.hasIntersection(other)) {
        result.add(this)
        return result
    }
    if (this.first < other.first) {
        result.add(this.first until other.first)
    }
    if (this.last > other.last) {
        result.add((other.last + 1)..this.last)
    }
    return result
}

/**
 * Subtracts other range from this range and returns list of remaining ranges
 */
fun LongRange.difference(other: LongRange): List<LongRange> {
    val result = mutableListOf<LongRange>()
    if (!this.hasIntersection(other)) {
        result.add(this)
        return result
    }
    if (this.first < other.first) {
        result.add(this.first until other.first)
    }
    if (this.last > other.last) {
        result.add((other.last + 1)..this.last)
    }
    return result
}

/**
 * Returns the length of the range - number of elements
 */
fun IntRange.length() = if (this == IntRange.EMPTY) 0 else this.last - this.first + 1

/**
 * Returns the length of the range - number of elements
 */
fun LongRange.length() = if (this == LongRange.EMPTY) 0L else this.last - this.first + 1L