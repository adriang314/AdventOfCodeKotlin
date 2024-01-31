package year2020

import common.BaseSolution

fun main() = println(SolutionDay07().result())

class SolutionDay07 : BaseSolution() {

    override val day = 7
    override val year = 2020

    override fun task1(): String {
        val result = bags.count { it.contains("shiny gold") } - 1
        return result.toString()
    }

    override fun task2(): String {
        val shinyGold = bags.first { it.name == "shiny gold" }
        val result = shinyGold.totalCount() - 1
        return result.toString()
    }

    private val bagRegex = Regex("(\\d+) (\\w+) (\\w+) bag")
    private var bags: List<Bag>

    init {
        val lines = input().split("\r\n")
        bags = lines.map { line ->
            val parts = line.split(" bags contain ")
            val name = parts[0].trim()
            val contains = parts[1].split(",").mapNotNull {
                bagRegex.find(it)?.let { result ->
                    val (count, type1, type2) = result.destructured
                    Bag("$type1 $type2") to count.toInt()
                }
            }.toMap()
            Bag(name, contains = contains)
        }

        bags.forEach { bag ->
            bag.contains = bag.contains.map { containingBag ->
                bags.first { it.name == containingBag.key.name } to containingBag.value
            }.toMap()
        }
    }

    data class Bag(val name: String, var contains: Map<Bag, Int> = emptyMap()) {

        fun contains(name: String): Boolean {
            if (this.name == name)
                return true
            return contains.any { it.key.contains(name) }
        }

        fun totalCount(): Int {
            if (contains.isEmpty())
                return 1
            return contains.entries.sumOf { it.value * it.key.totalCount() } + 1
        }

        override fun toString() = "$name - ${contains.keys.joinToString(", ") { it.name }}"
    }
}