package year2020

import common.BaseSolution

fun main() = println(SolutionDay13().result())

class SolutionDay13 : BaseSolution() {

    override val day = 13
    
    override fun task1(): String {
        val nextDepartures = buses.mapNotNull { bus -> bus.nextDeparture(earliestTime)?.let { Pair(bus, it) } }
        val bestNextDeparture = nextDepartures.minBy { it.second }
        val result = (bestNextDeparture.second - earliestTime) * bestNextDeparture.first.id!!
        return result.toString()
    }

    override fun task2(): String {
        val numbers = buses.map { it.id!!.toLong() }.toLongArray()
        val reminders = buses.map { (it.id!! - it.index).toLong() }.toLongArray()
        val result = chineseRemainder(numbers, reminders)
        return result.toString()
    }

    private val earliestTime: Int
    private val buses: List<Bus>

    init {
        val lines = input().split("\r\n")
        earliestTime = lines[0].toInt()
        buses = lines[1].split(",").mapIndexed { index, s -> Bus(s.toIntOrNull(), index) }
            .filter { it.id != null }
    }

    /* returns x where (a * x) % b == 1 */
    private fun multInv(a: Long, b: Long): Long {
        if (b == 1L) return 1L
        var aa = a
        var bb = b
        var x0 = 0L
        var x1 = 1L
        while (aa > 1L) {
            val q = aa / bb
            var t = bb
            bb = aa % bb
            aa = t
            t = x0
            x0 = x1 - q * x0
            x1 = t
        }
        if (x1 < 0) x1 += b
        return x1
    }

    private fun chineseRemainder(n: LongArray, a: LongArray): Long {
        val prod = n.fold(1L) { acc, i -> acc * i }
        var sum = 0L
        for (i in n.indices) {
            val p = prod / n[i]
            sum += a[i] * multInv(p, n[i]) * p
        }
        return sum % prod
    }

    data class Bus(val id: Int?, val index: Int) {
        fun nextDeparture(after: Int) =
            if (id == null)
                null
            else
                ((after / id) + 1) * id
    }
}