package year2019

import common.BaseSolution
import year2019.SolutionDay14.Material.Companion.FUEL
import year2019.SolutionDay14.Material.Companion.ORE
import kotlin.math.ceil

fun main() = println(SolutionDay14().result())

class SolutionDay14 : BaseSolution() {

    override val day = 14
    override val year = 2019

    private val reactions = Reactions(input().split("\r\n").map { line ->
        val split = line.split(" => ")
        val sources = split[0].split(", ").associate { src ->
            src.split(" ").let { parts -> Material(parts[1]) to parts[0].toLong() }
        }
        val provides = split[1].split(" ").let { parts -> Material(parts[1]) to parts[0].toLong() }

        Reaction(provides.first, provides.second, sources)
    })

    override fun task1(): String {
        val usage = MaterialUsage()
        usage.build(FUEL, 1L, reactions)
        val result = usage.used(ORE)
        return result.toString()
    }

    override fun task2(): String {
        val oreLimit = 1000000000000L
        var min = 4000000L
        var max = 5000000L
        var curr = min + ((max - min) / 2L)

        do {
            val usage = MaterialUsage()
            usage.build(FUEL, curr, reactions)
            val result = usage.used(ORE)
            if (result > oreLimit) {
                max = curr
            } else {
                min = curr
            }
            curr = min + ((max - min) / 2L)
        } while (curr != min && curr != max)

        return curr.toString()
    }

    private data class MaterialUsage(private val data: MutableMap<Material, Long> = mutableMapOf()) {

        fun used(material: Material) = data[material] ?: 0L

        fun build(material: Material, toProduceQuantity: Long, reactions: Reactions) {
            data.remove(material)

            val reaction = reactions.findReaction(material)
            val reactionCount = ceil((toProduceQuantity.toDouble() / reaction.quantity)).toLong()

            reaction.source.forEach { data.compute(it.key) { _, v -> (v ?: 0L) + it.value * reactionCount } }

            if (data.keys.all { it == ORE }) {
                return
            }

            val maxLevel = data.keys.maxOf { reactions.levelFor(it) }
            val maxLevelMaterial = data.filter { reactions.levelFor(it.key) == maxLevel }.entries.first()

            build(maxLevelMaterial.key, maxLevelMaterial.value, reactions)
        }
    }

    private data class Material(val name: String) {
        override fun toString(): String = name

        companion object {
            val ORE = Material("ORE")
            val FUEL = Material("FUEL")
        }
    }

    private data class Reaction(val provides: Material, val quantity: Long, val source: Map<Material, Long>)

    private data class Reactions(val all: List<Reaction>) {
        private val map = all.associateBy { it.provides }
        private val canBeProvidedFromOre = map.filter { it.value.source.all { src -> src.key == ORE } }
        private val levels = assignLevels()

        fun levelFor(material: Material) = if (material == ORE) -1 else levels[material]!!

        fun assignLevels(): Map<Material, Int> {
            val levels = mutableMapOf<Material, Int>()
            canBeProvidedFromOre.keys.forEach { levels[it] = 0 }
            var level = 0
            do {
                level++
                val nextLevel = map.filter { entry ->
                    !levels.containsKey(entry.key) && entry.value.source.keys.all { levels.containsKey(it) }
                }.keys
                nextLevel.forEach { levels[it] = level }
            } while (nextLevel.isNotEmpty())

            return levels
        }

        fun findReaction(material: Material) = map[material]!!
    }
}