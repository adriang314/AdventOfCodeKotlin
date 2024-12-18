package common

fun Int.pow(exponent: Int) = power(this.toLong(), exponent)

fun Long.pow(exponent: Int) = power(this, exponent)

fun Long.numberOfDigits(): Int =
    when (this) {
        in -9L..9L -> 1
        else -> 1 + (this / 10).numberOfDigits()
    }

fun Int.numberOfDigits(): Int =
    when (this) {
        in -9..9 -> 1
        else -> 1 + (this / 10).numberOfDigits()
    }

fun Long.isEven(): Boolean = this % 2L == 0L

fun Int.isEven(): Boolean = this % 2 == 0

private fun power(base: Long, exponent: Int): Long {
    var result: Long = 1
    repeat(exponent) {
        result *= base
    }
    return result
}
