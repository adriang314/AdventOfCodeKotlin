package year2022

import common.BaseSolution

fun main() {
    println("${SolutionDay01()}")
}

class SolutionDay01 : BaseSolution() {

    override val day = 1
    override val year = 2022

    override fun task1() = calories.maxCalories.toString()

    override fun task2() = calories.topThreeCalories.toString()

    private var calories: Calories = Calories(input())

    class Calories(cal: String) {
        val maxCalories: Int
        val topThreeCalories: Int

        init {
            val caloriesPerElf = cal.split("\r\n\r\n")
                .map { it.split("\r\n") }
                .map { it.map { cal -> cal.toInt() } }
                .map { it.sum() }
            maxCalories = caloriesPerElf.max()
            topThreeCalories = caloriesPerElf.sortedDescending().take(3).sum()
        }
    }
}