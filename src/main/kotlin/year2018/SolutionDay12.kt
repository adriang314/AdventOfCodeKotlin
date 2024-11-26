package year2018

import common.BaseSolution
import java.util.LinkedList

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {

    override val day = 12
    override val year = 2018

    private val initStateRegex = Regex("^initial state: ([#.]+)$")
    private val noteRegex = Regex("^([#.]+) => ([#.])$")
    private val initialState: String
    private val notes: List<Note>
    private val rules: Rules

    init {
        val lines = input().split("\r\n").filter { it.isNotEmpty() }

        val (initStateValue) = initStateRegex.find(lines.first())!!.destructured
        initialState = initStateValue

        notes = lines.drop(1).map {
            val (from, to) = noteRegex.find(it)!!.destructured
            Note(from, to[0])
        }

        // no need to use rules that do NOT change plant state
        rules = Rules(notes.filter { it.isChange }.map { PlantSpreadRule(it.id, it.result) }.toHashSet())
    }

    override fun task1(): String {
        val plantation = Plantation(initialState)

        repeat(20) {
            plantation.nextGeneration(rules)
        }

        return plantation.value().toString()
    }

    override fun task2(): String {
        val plantation = Plantation(initialState)

        // after certain generations position of every live plant moves always by +1
        val initialGeneration = 1_000
        val requiredGeneration = 50_000_000_000L
        val missingGeneration = requiredGeneration - initialGeneration

        repeat(1_000) {
            plantation.nextGeneration(rules)
        }

        val result = plantation.value() + plantation.map.size * missingGeneration
        return result.toString()
    }

    // key = position, value = plant state - keeping only live
    private class Plantation(val map: MutableMap<Int, Int>) {
        constructor(initialState: String) :
                this(initialState.mapIndexed { idx, c -> idx to Plant.state(c) }
                    .filter { it.second == Plant.LIVE }
                    .toMap().toMutableMap())

        var minPosition = map.keys.min()
        var maxPosition = map.keys.max()

        fun value() = map.filter { it.value == Plant.LIVE }.map { it.key.toLong() }.sum()

        fun nextGeneration(rules: Rules) {
            val changes = LinkedList<() -> Unit>()
            (minPosition - 4..maxPosition + 4).forEach { position ->
                val id = Note.calcId(
                    map.getOrDefault(position, Plant.DEAD),
                    map.getOrDefault(position + 1, Plant.DEAD),
                    map.getOrDefault(position + 2, Plant.DEAD),
                    map.getOrDefault(position + 3, Plant.DEAD),
                    map.getOrDefault(position + 4, Plant.DEAD),
                )
                val rule = rules.set.firstOrNull { it.id == id }
                if (rule != null)
                    changes.add { reverseState(position + 2) }
            }

            changes.forEach { it.invoke() }

            map.entries.removeIf { it.value == Plant.DEAD }

            minPosition = map.keys.min()
            maxPosition = map.keys.max()
        }

        override fun toString() = "[${map.size}] " + map.map { it.key }.sorted().joinToString(",")

        private fun reverseState(position: Int) {
            map[position] = if (map.getOrDefault(position, Plant.DEAD) == Plant.LIVE) Plant.DEAD else Plant.LIVE
        }
    }

    private class Plant {
        companion object {
            fun state(char: Char) = if (char == '#') LIVE else DEAD
            const val LIVE = 1
            const val DEAD = 0
        }
    }

    private data class Note(val from: String, val to: Char) {
        val id = calcId(*from.map { Plant.state(it) }.toIntArray())
        val result = Plant.state(to)
        val isChange = from[2] != to

        companion object {
            fun calcId(vararg from: Int): Int {
                var tmpId = 0
                if (from[0] == Plant.LIVE) tmpId += 10_000
                if (from[1] == Plant.LIVE) tmpId += 1_000
                if (from[2] == Plant.LIVE) tmpId += 100
                if (from[3] == Plant.LIVE) tmpId += 10
                if (from[4] == Plant.LIVE) tmpId += 1
                return tmpId
            }
        }
    }

    private data class Rules(val set: HashSet<PlantSpreadRule>)

    private data class PlantSpreadRule(val id: Int, val result: Int)
}
