package year2024

import common.BaseSolution
import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.AllDirectedPaths
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

fun main() = println(SolutionDay21().result())

class SolutionDay21 : BaseSolution() {

    override val day = 21

    private val codes = input().split("\r\n").map { line -> Code(line) }

    override fun task1(): String {
        val result = calculateComplexities(2)
        return result.toString()
    }

    override fun task2(): String {
        val result = calculateComplexities(25)
        return result.toString()
    }

    private fun calculateComplexities(robots: Int): Long {
        val numKeyPad = NumKeyPad()
        val directionKeyPad = DirectionKeyPad()

        return codes.sumOf { code ->
            val numSequences = numKeyPad.findSequences(code.value)
            val sequenceCosts = numSequences.map { sequence -> directionKeyPad.costOfSequence(sequence, robots) }
            sequenceCosts.min() * code.number()
        }
    }

    private abstract class KeyPad(
        private val moveMap: Map<String, String>,
        keys: List<String>,
    ) {
        protected val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
        private val algorithm = BidirectionalDijkstraShortestPath(graph)

        init {
            keys.forEach { key -> graph.addVertex(key) }
        }

        protected fun shortestSequences(from: String, to: String): List<String> {
            val shortestSequenceLength = algorithm.getPath(from, to).length
            val shortestSequences = shortestSequences(from, to, shortestSequenceLength)
            return shortestSequences.map { path ->
                path.edgeList.joinToString("") { edge -> moveMap[edge.toString()]!! } + "A"
            }
        }

        private fun shortestSequences(
            from: String,
            to: String,
            shortestPathLength: Int
        ): List<GraphPath<String, DefaultEdge>> {
            val sequences = AllDirectedPaths(graph).getAllPaths(from, to, true, shortestPathLength)
            return sequences ?: throw RuntimeException("No path found")
        }
    }

    private class DirectionKeyPad : KeyPad(
        mapOf(
            "(A : ^)" to "<",
            "(^ : A)" to ">",
            "(A : >)" to "v",
            "(> : A)" to "^",
            "(^ : v)" to "v",
            "(v : ^)" to "^",
            "(> : v)" to "<",
            "(v : >)" to ">",
            "(v : <)" to "<",
            "(< : v)" to ">",
        ),
        listOf("^", "v", "<", ">", "A")
    ) {
        init {
            graph.addEdge("A", "^")
            graph.addEdge("^", "A")
            graph.addEdge("A", ">")
            graph.addEdge(">", "A")
            graph.addEdge("^", "v")
            graph.addEdge("v", "^")
            graph.addEdge(">", "v")
            graph.addEdge("v", ">")
            graph.addEdge("v", "<")
            graph.addEdge("<", "v")
        }

        private val cache = mutableMapOf<String, Long>()

        fun costOfSequence(sequence: String, iteration: Int): Long {
            if (iteration == 0) {
                return sequence.length.toLong()
            }

            val digits = sequence.toList()
            var totalCost = 0L
            for (digitIdx in digits.indices) {
                val digit = digits[digitIdx].toString()
                val prevDigit = digits.getOrElse(digitIdx - 1) { "A" }.toString()
                totalCost += costOfSequence(prevDigit, digit, iteration)
            }

            return totalCost
        }

        private fun costOfSequence(prevDigit: String, digit: String, iteration: Int): Long {
            val cacheKey = "[$iteration] $prevDigit to $digit"

            if (cache.containsKey(cacheKey)) {
                return cache[cacheKey]!!
            } else if (prevDigit == digit) {
                return costOfSequence("A", iteration - 1)
            }

            val shortestPaths = shortestSequences(prevDigit, digit)
            val minSequenceCost = shortestPaths.minOf { sequence -> costOfSequence(sequence, iteration - 1) }
            cache[cacheKey] = minSequenceCost
            return minSequenceCost
        }
    }

    private class NumKeyPad : KeyPad(
        mapOf(
            "(9 : 8)" to "<",
            "(8 : 9)" to ">",
            "(9 : 6)" to "v",
            "(6 : 9)" to "^",
            "(8 : 7)" to "<",
            "(7 : 8)" to ">",
            "(8 : 5)" to "v",
            "(5 : 8)" to "^",
            "(7 : 4)" to "v",
            "(4 : 7)" to "^",
            "(6 : 5)" to "<",
            "(5 : 6)" to ">",
            "(6 : 3)" to "v",
            "(3 : 6)" to "^",
            "(5 : 4)" to "<",
            "(4 : 5)" to ">",
            "(5 : 2)" to "v",
            "(2 : 5)" to "^",
            "(4 : 1)" to "v",
            "(1 : 4)" to "^",
            "(3 : 2)" to "<",
            "(2 : 3)" to ">",
            "(3 : A)" to "v",
            "(A : 3)" to "^",
            "(2 : 1)" to "<",
            "(1 : 2)" to ">",
            "(2 : 0)" to "v",
            "(0 : 2)" to "^",
            "(A : 0)" to "<",
            "(0 : A)" to ">",
        ),
        listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A")

    ) {
        init {
            graph.addEdge("9", "8")
            graph.addEdge("8", "9")
            graph.addEdge("9", "6")
            graph.addEdge("6", "9")
            graph.addEdge("8", "7")
            graph.addEdge("7", "8")
            graph.addEdge("8", "5")
            graph.addEdge("5", "8")
            graph.addEdge("7", "4")
            graph.addEdge("4", "7")
            graph.addEdge("6", "5")
            graph.addEdge("5", "6")
            graph.addEdge("6", "3")
            graph.addEdge("3", "6")
            graph.addEdge("5", "4")
            graph.addEdge("4", "5")
            graph.addEdge("5", "2")
            graph.addEdge("2", "5")
            graph.addEdge("4", "1")
            graph.addEdge("1", "4")
            graph.addEdge("3", "2")
            graph.addEdge("2", "3")
            graph.addEdge("3", "A")
            graph.addEdge("A", "3")
            graph.addEdge("2", "1")
            graph.addEdge("1", "2")
            graph.addEdge("2", "0")
            graph.addEdge("0", "2")
            graph.addEdge("0", "A")
            graph.addEdge("A", "0")
        }

        fun findSequences(sequence: String): List<String> {
            var sequences = emptyList<String>()

            sequence.forEachIndexed { index, digit ->
                val from = sequence.getOrElse(index - 1) { 'A' }.toString()
                val to = digit.toString()

                val nextSequence = if (from == to) {
                    listOf("A")
                } else {
                    shortestSequences(from, to)
                }

                sequences = if (sequences.isEmpty()) {
                    nextSequence
                } else {
                    sequences.map { curr -> nextSequence.map { new -> curr + new } }.flatten()
                }
            }

            return sequences
        }
    }

    private data class Code(val value: String) {
        fun number() = value.substring(0, value.length - 1).toInt()
    }
}