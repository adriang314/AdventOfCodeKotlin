package year2018

import common.BaseSolution
import common.Direction
import common.Position
import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import java.util.*

fun main() = println(SolutionDay20().result())

class SolutionDay20 : BaseSolution() {

    override val day = 20

    private val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    private val algorithm = BidirectionalDijkstraShortestPath(graph)
    private var shortestPathsToRooms: Map<String, GraphPath<String, DefaultEdge>>

    init {
        val anyFormulaInParenthesis = Regex("(\\([^()]*\\))")
        val directions = mutableMapOf<Int, Directions>()

        var id = 0
        var currRoute = input().substring(1, input().length - 1)
        while (true) {
            val matches = anyFormulaInParenthesis.find(currRoute)
            if (matches == null) {
                directions[++id] = resolveDirections(currRoute, directions)
                break
            }

            val (valueWithParenthesis) = matches.destructured
            val cleanValue = valueWithParenthesis.substring(1, valueWithParenthesis.length - 1)
            directions[++id] = resolveDirections(cleanValue, directions)

            currRoute = currRoute.replaceFirst(valueWithParenthesis, "#$id;")
        }

        val startPosition = Position(0,0)
        findRooms(directions.maxBy { it.key }.value, startPosition)

        shortestPathsToRooms = graph.vertexSet().associateWith { endPosition ->
            val path = algorithm.getPath(startPosition.toString(), endPosition)
            path
        }
    }

    override fun task1(): String {
        val longest = shortestPathsToRooms.values.maxOf { it.length }
        return longest.toString()
    }

    override fun task2(): String {
        val atLeast1000 = shortestPathsToRooms.values.count { it.length >= 1000 }
        return atLeast1000.toString()
    }

    private class Journey(val nextDirections: Directions, val previous: Journey? = null)

    private fun findRooms(allDirections: Directions, startPosition: Position) {
        val startJourney = Journey(allDirections)
        val queue = LinkedList<Journey>().apply { add(startJourney) }
        val currentPositions = mutableMapOf<Journey, Position>()

        currentPositions[startJourney] = startPosition
        graph.addVertex(startPosition.toString())

        while (queue.isNotEmpty()) {
            val journey = queue.poll()
            val nextDirections = journey.nextDirections
            val currentPosition = journey.previous?.let { currentPositions[it]!! } ?: startPosition

            when (nextDirections) {
                is RegularDirections -> {
                    var lastPosition: Position? = null
                    nextDirections.value
                        .asSequence()
                        .map { Direction.from(it) }
                        .scan(currentPosition) { currPosition, direction -> currPosition.next(direction) }
                        .zipWithNext { pos1, pos2 -> Pair(pos1, pos2) }
                        .forEach {
                            graph.addVertex(it.first.toString())
                            graph.addVertex(it.second.toString())
                            graph.addEdge(it.first.toString(), it.second.toString())
                            lastPosition = it.second
                        }
                    currentPositions[journey] = lastPosition ?: currentPosition
                }

                is AndDirections -> {
                    val newJourneys = mutableListOf<Journey>()
                    nextDirections.list.forEach { andPattern ->
                        val newJourney = Journey(andPattern, newJourneys.lastOrNull() ?: journey.previous)
                        newJourneys.add(newJourney)
                        queue.add(newJourney)
                    }
                }

                is OrDirections -> {
                    nextDirections.list.forEach { orPattern ->
                        queue.add(Journey(orPattern, journey.previous))
                        currentPositions[journey] = currentPosition
                    }
                }
            }
        }
    }

    private fun resolveDirections(pattern: String, patterns: Map<Int, Directions>): Directions {
        return if (pattern.contains("|"))
            OrDirections(pattern.split("|").map { resolveDirections(it, patterns) })
        else if (!pattern.contains('#'))
            RegularDirections(pattern)
        else {
            resolveAndDirections(pattern, patterns)
        }
    }

    private fun resolveAndDirections(pattern: String, patterns: Map<Int, Directions>): AndDirections {
        var tmpPattern = pattern
        val andDirections = mutableListOf<Directions>()
        while (tmpPattern.isNotEmpty()) {
            if (tmpPattern.startsWith("#")) {
                val refEnd = tmpPattern.indexOfFirst { it == ';' }
                val refId = tmpPattern.substring(1, refEnd).toInt()
                andDirections.add(patterns[refId]!!)
                tmpPattern = tmpPattern.substring(refEnd + 1)
            } else {
                val refStart = tmpPattern.indexOfFirst { it == '#' }
                if (refStart == -1) {
                    andDirections.add(RegularDirections(tmpPattern))
                    break
                }

                andDirections.add(RegularDirections(tmpPattern.substring(0, refStart)))
                tmpPattern = tmpPattern.substring(refStart)
            }
        }

        return AndDirections(andDirections)
    }

    private sealed interface Directions

    private data class RegularDirections(val value: String) : Directions

    private data class AndDirections(val list: List<Directions>) : Directions

    private data class OrDirections(val list: List<Directions>) : Directions
}
