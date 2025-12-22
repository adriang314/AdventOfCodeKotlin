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

fun Long.sumOfAllDividers(): Long {
    var sum = 0L
    for (i in 1..this) {
        if (this % i == 0L) {
            sum += i
        }
    }
    return sum
}

fun String.parseToInt(): Int {
    var number = this
    while (this.startsWith('0')) {
        number = number.substring(1)
    }

    if (number.isEmpty())
        return 0
    return number.toInt()
}

fun String.parseToLong(): Long {
    var number = this
    while (this.startsWith('0')) {
        number = number.substring(1)
    }

    if (number.isEmpty())
        return 0L
    return number.toLong()
}

fun Long.isEven(): Boolean = this % 2L == 0L

fun Int.isEven(): Boolean = this % 2 == 0

fun Int.isPrime(): Boolean = (2..this / 2).none { this % it == 0 }

fun Long.isPrime(): Boolean = (2L..this / 2L).none { this % it == 0L }

fun Long.divisors(): Sequence<Long> = sequence {
    var i = 1L
    while (i * i <= this@divisors) {
        if (this@divisors % i == 0L) {
            yield(i)

            val other = this@divisors / i
            if (other != i) {
                yield(other)
            }
        }
        i++
    }
}

private fun power(base: Long, exponent: Int): Long {
    var result: Long = 1
    repeat(exponent) {
        result *= base
    }
    return result
}
