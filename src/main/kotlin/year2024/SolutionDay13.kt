package year2024

import common.BaseSolution
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.LUDecomposition
import kotlin.math.roundToLong

fun main() = println(SolutionDay13().result())

class SolutionDay13 : BaseSolution() {

    override val day = 13
    override val year = 2024

    private val aButtonRegex = Regex("Button A: X\\+(\\d+), Y\\+(\\d+)")
    private val bButtonRegex = Regex("Button B: X\\+(\\d+), Y\\+(\\d+)")
    private val prizeRegex = Regex("Prize: X=(\\d+), Y=(\\d+)")

    private val games1: List<Game>
    private val games2: List<Game>

    init {
        var aButton: Button? = null
        var bButton: Button? = null
        val games = mutableListOf<Game>()

        for (line in input().split("\r\n").filter { it.isNotEmpty() }) {
            if (aButton == null) {
                val (xShift, yShift) = aButtonRegex.find(line)!!.destructured
                aButton = Button(xShift.toInt(), yShift.toInt())
            } else if (bButton == null) {
                val (xShift, yShift) = bButtonRegex.find(line)!!.destructured
                bButton = Button(xShift.toInt(), yShift.toInt())
            } else {
                val (x, y) = prizeRegex.find(line)!!.destructured
                val prize = Prize(x.toLong(), y.toLong())

                games.add(Game(aButton, bButton, prize))
                aButton = null
                bButton = null
            }
        }

        val shift = 10_000_000_000_000L
        this.games1 = games
        this.games2 = games.map { Game(it.aButton, it.bButton, Prize(it.prize.x + shift, it.prize.y + shift)) }
    }

    override fun task1(): String {
        val result = games1.mapNotNull { it.tokenCost }.sum()
        return result.toString()
    }

    override fun task2(): String {
        val result = games2.mapNotNull { it.tokenCost }.sum()
        return result.toString()
    }

    private data class Game(val aButton: Button, val bButton: Button, val prize: Prize) {
        val aButtonPressCount: Long?
        val bButtonPressCount: Long?
        val tokenCost: Long?

        init {
            // solving two equations to find buttons count
            // prize.x = aButton.xShift * A(press) + bButton.xShift * B(press)
            // prize.y = aButton.yShift * A(press) + bButton.yShift * B(press)
            val coefficients = Array2DRowRealMatrix(
                arrayOf(
                    doubleArrayOf(aButton.xShift.toDouble(), bButton.xShift.toDouble()),
                    doubleArrayOf(aButton.yShift.toDouble(), bButton.yShift.toDouble())
                ),
                false
            )
            val solver = LUDecomposition(coefficients).solver
            val constants = ArrayRealVector(doubleArrayOf(prize.x.toDouble(), prize.y.toDouble()), false)
            val solution = solver.solve(constants)
            val aResult = solution.getEntry(0).roundToLong()
            val bResult = solution.getEntry(1).roundToLong()

//            val bUpper = (prize.y * aButton.xShift) - (prize.x * aButton.yShift)
//            val bLower = (bButton.yShift * aButton.xShift) - (bButton.xShift * aButton.yShift)
//            val bResult = bUpper / bLower
//            val aUpper = prize.x - (bResult * bButton.xShift)
//            val aLower = aButton.xShift
//            val aResult = aUpper / aLower

            val xPrizeOk = (aResult * aButton.xShift + bResult * bButton.xShift) == prize.x
            val yPrizeOk = (aResult * aButton.yShift + bResult * bButton.yShift) == prize.y
            if (xPrizeOk && yPrizeOk) {
                aButtonPressCount = aResult
                bButtonPressCount = bResult
                tokenCost = aButtonPressCount * 3L + bButtonPressCount
            } else {
                aButtonPressCount = null
                bButtonPressCount = null
                tokenCost = null
            }
        }
    }

    private data class Button(val xShift: Int, val yShift: Int)

    private data class Prize(val x: Long, val y: Long)
}