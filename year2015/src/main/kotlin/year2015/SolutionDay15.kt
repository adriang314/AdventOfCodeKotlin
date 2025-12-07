package year2015

import common.BaseSolution

fun main() = println(SolutionDay15().result())

class SolutionDay15 : BaseSolution() {

    override val day = 15

    private val regex = """(\w+): capacity (-?\d+), durability (-?\d+), flavor (-?\d+), texture (-?\d+), calories (-?\d+)""".toRegex()

    private val ingredients = input().split("\r\n").map { line ->
        val (name, capacity, durability, flavor, texture, calories) = regex.matchEntire(line)!!.destructured
        Ingredient(name, capacity.toInt(), durability.toInt(), flavor.toInt(), texture.toInt(), calories.toInt())
    }

    override fun task1(): String {
        return BestRecipe(ingredients).find { _ -> true }.toString()
    }

    override fun task2(): String {
        return BestRecipe(ingredients).find { recipe -> recipe.calories() == 500L }.toString()
    }

    private class BestRecipe(private val ingredients: List<Ingredient>) {

        fun find(isAcceptable: (Recipe) -> Boolean): Long {
            return (0..100).maxOf { amount -> find(listOf(IngredientAmount(ingredients.first(), amount)), isAcceptable) }
        }

        private fun find(usedIngredientAmounts: List<IngredientAmount>, isAcceptable: (Recipe) -> Boolean): Long {
            val usedIngredients = usedIngredientAmounts.map { it.ingredient }
            val usedAmount = usedIngredientAmounts.sumOf { it.amount }
            val remainingAmount = 100 - usedAmount
            if (remainingAmount == 0) {
                val recipe = Recipe(usedIngredientAmounts)
                return if (isAcceptable(recipe)) recipe.score() else 0L
            }

            val nextIngredient = ingredients.first { it !in usedIngredients }
            val isLastIngredient = usedIngredients.size + 1 == ingredients.size
            return if (isLastIngredient) {
                find(usedIngredientAmounts.plus(IngredientAmount(nextIngredient, remainingAmount)), isAcceptable)
            } else {
                (0..remainingAmount).maxOf { amount -> find(usedIngredientAmounts.plus(IngredientAmount(nextIngredient, amount)), isAcceptable) }
            }
        }
    }

    private class Recipe(private val ingredients: List<IngredientAmount>) {
        fun score(): Long {
            val capacity = ingredients.sumOf { (ingredient, amount) -> ingredient.capacity * amount }.coerceAtLeast(0)
            val durability = ingredients.sumOf { (ingredient, amount) -> ingredient.durability * amount }.coerceAtLeast(0)
            val flavor = ingredients.sumOf { (ingredient, amount) -> ingredient.flavor * amount }.coerceAtLeast(0)
            val texture = ingredients.sumOf { (ingredient, amount) -> ingredient.texture * amount }.coerceAtLeast(0)
            return 1L * capacity * durability * flavor * texture
        }

        fun calories(): Long {
            return ingredients.sumOf { (ingredient, amount) -> 1L * ingredient.calories * amount }
        }
    }

    private data class IngredientAmount(val ingredient: Ingredient, val amount: Int)

    private data class Ingredient(val name: String, val capacity: Int, val durability: Int, val flavor: Int, val texture: Int, val calories: Int)
}