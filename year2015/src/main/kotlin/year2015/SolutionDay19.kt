package year2015

import common.BaseSolution
import common.replaceAll
import common.replaceOne
import java.util.LinkedList

fun main() = println(SolutionDay19().result())

class SolutionDay19 : BaseSolution() {

    override val day = 19

    private val initialMolecule = input().split("\r\n").last()
    private val replacements = Replacements(initialMolecule, input().split("\r\n").dropLast(2).map { line ->
        val (from, to) = """(\w+) => (\w+)""".toRegex().find(line)!!.destructured
        Replacement(from, to)
    }.sortedByDescending { it.to.length })

    override fun task1(): String {
        val result = replacements.distinctMolecules()
        return result.toString()
    }

    override fun task2(): String {
        val statusQueue = LinkedList(listOf(ReplacementStatus(0, initialMolecule)))
        val statusMap = mutableSetOf<ReplacementStatus>()

        while (true) {
            val currentStatus = statusQueue.removeFirst()
            if (currentStatus.isFinal())
                return currentStatus.steps.toString()

            replacements.getNextReplacements(currentStatus).filter { !statusMap.contains(it) }.forEach {
                statusQueue.add(it)
                statusMap.add(it)
            }
        }
    }

    private data class ReplacementStatus(val steps: Int, val molecule: String) {

        fun isFinal() = molecule == "e"

        fun next(replacement: Replacement): ReplacementStatus =
            molecule.replaceAll(replacement.to, replacement.from).let {
                ReplacementStatus(steps + it.second, it.first)
            }
    }

    private data class Replacements(val molecule: String, val items: List<Replacement>) {

        fun getNextReplacements(status: ReplacementStatus): List<ReplacementStatus> {
            // to reduce number of options - checking optimistically
            val threshold = 2
            var nextReplacements = items.filter { status.molecule.contains(it.to) }.sortedByDescending { it.to.length }
            if (nextReplacements.size > threshold)
                nextReplacements = nextReplacements.take(threshold)

            return nextReplacements.map { status.next(it) }
        }

        fun distinctMolecules(): Int =
            items.flatMap { replacement -> molecule.replaceOne(replacement.from, replacement.to) }.toSet().size
    }

    private data class Replacement(val from: String, val to: String)
}