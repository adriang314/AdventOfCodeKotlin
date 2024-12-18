package year2020

import common.BaseSolution

fun main() = println(SolutionDay21().result())

class SolutionDay21 : BaseSolution() {

    override val day = 21
    
    override fun task1(): String {
        val result = food.sumOf { food -> food.ingredients.filter { !detectedAllergens.containsKey(it) }.size }
        return result.toString()
    }

    override fun task2(): String {
        val result = detectedAllergens.entries.sortedBy { it.value.name }.joinToString(",") { it.key.name }
        return result
    }

    private val food = input().split("\r\n").map { line ->
        val parts = line.substring(0, line.length - 1).split(" (contains ")
        val ingredients = parts[0].split(" ").map { Ingredient(it) }.toSet()
        val allergens = parts[1].split(", ").map { Allergen(it) }.toSet()
        Food(ingredients, allergens)
    }

    private var detectedAllergens: Map<Ingredient, Allergen> = mutableMapOf()

    init {
        checkAllergens()
    }

    private fun checkAllergens() {

        var totalAllergensDetected = 0
        var newAllergensDetected: Int
        do {
            val allergensInFood = food.map { food -> food.allergens.map { it to food.ingredients } }
                .flatten()
                .groupBy { it.first }
                .mapValues { entry ->
                    entry.value.map { it.second }.reduce { acc, ingredients -> acc.intersect(ingredients) }
                }.mapValues { entry ->
                    entry.value.filter { !detectedAllergens.containsKey(it) || detectedAllergens[it] == entry.key }
                }

            detectedAllergens = allergensInFood
                .filter { it.value.size == 1 }
                .mapValues { it.value.single() }
                .map { (k, v) -> v to k }.toMap()

            newAllergensDetected = detectedAllergens.size - totalAllergensDetected
            totalAllergensDetected = detectedAllergens.size

        } while (newAllergensDetected > 0)
    }

    private data class Food(val ingredients: Set<Ingredient>, val allergens: Set<Allergen>)

    private data class Allergen(val name: String)

    private data class Ingredient(val name: String)
}