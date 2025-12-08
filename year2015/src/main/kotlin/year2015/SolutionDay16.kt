package year2015

import common.BaseSolution

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16

    private val aunts = input().split("\r\n").map { line ->
        val sueIdRegex = """Sue (\d+):""".toRegex()
        val (id) = sueIdRegex.find(line)!!.destructured
        val propRegex = """(\w+): (\d+)""".toRegex()
        val lineWithProps = line.substringAfter(": ")
        val props = propRegex.findAll(lineWithProps, 0).map { match ->
            val (name, count) = match.destructured
            Property.from(name) to count.toInt()
        }.toMap()
        Aunt(id.toInt(), props.toMutableMap())
    }

    private val searchAuntProps = mapOf(
        Property.Children to 3,
        Property.Cats to 7,
        Property.Samoyeds to 2,
        Property.Pomeranians to 3,
        Property.Akitas to 0,
        Property.Vizslas to 0,
        Property.Goldfish to 5,
        Property.Trees to 3,
        Property.Cars to 2,
        Property.Perfumes to 1,
    )

    override fun task1(): String {
        val aunt = aunts.single { it.matches1(searchAuntProps) }
        return aunt.id.toString()
    }

    override fun task2(): String {
        val aunt = aunts.single { it.matches2(searchAuntProps) }
        return aunt.id.toString()
    }

    private data class Aunt(val id: Int, val props: MutableMap<Property, Int>) {

        fun matches1(props: Map<Property, Int>): Boolean {
            return props.all { (prop, count) -> !this.props.contains(prop) || this.props[prop] == count }
        }

        fun matches2(props: Map<Property, Int>): Boolean {
            return props.all { (prop, count) ->
                !this.props.contains(prop) || (
                        when (prop) {
                            Property.Cats, Property.Trees -> this.props[prop]!! > count
                            Property.Pomeranians, Property.Goldfish -> this.props[prop]!! < count
                            else -> this.props[prop]!! == count
                        })
            }
        }
    }

    private enum class Property {
        Children, Cats, Samoyeds, Pomeranians, Akitas, Vizslas, Goldfish, Trees, Cars, Perfumes;

        companion object {
            fun from(text: String): Property = Property.entries.single { it.name.equals(text, true) }
        }
    }
}