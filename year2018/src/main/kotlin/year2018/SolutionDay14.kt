package year2018

import common.BaseSolution

fun main() = println(SolutionDay14().result())

class SolutionDay14 : BaseSolution() {

    override val day = 14

    private val recipes = Recipes(input().toInt())

    override fun task1(): String {
        recipes.getRequired()
        return recipes.getScore()
    }

    override fun task2(): String {
        val score = recipes.findRecipeSequence()
        return score.toString()
    }

    private class Recipes(val required: Int) {
        private val all = ArrayList<Int>(20_300_000).also { it.addAll(listOf(3, 7)) }
        private var elf1Position = 0
        private var elf2Position = 1

        fun getRequired() {
            while (all.size < required + 10) {
                next()
            }
        }

        fun getScore() = (required..<required + 10).joinToString("") { all[it].toString() }

        fun findRecipeSequence(): Int {
            val requiredSequence = required.toString().toList().map { it.digitToInt() }

            var position = 0
            while (!matchesAt(position, requiredSequence)) {
                if (all.size < ++position + requiredSequence.size)
                    next()
            }
            return position
        }

        private fun matchesAt(position: Int, sequence: List<Int>) =
            sequence.mapIndexed { index, digit -> digit == all[position + index] }.all { it }

        private fun next() {
            val recipe1 = all[elf1Position]
            val recipe2 = all[elf2Position]
            val nextRecipes = (recipe1 + recipe2).toString().asSequence().map { it.digitToInt() }

            all.addAll(nextRecipes)

            elf1Position = (elf1Position + recipe1 + 1) % all.size
            elf2Position = (elf2Position + recipe2 + 1) % all.size
        }
    }
}
