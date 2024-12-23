package year2024

import common.BaseSolution
import org.jgrapht.alg.clique.BronKerboschCliqueFinder
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

fun main() = println(SolutionDay23().result())

class SolutionDay23 : BaseSolution() {

    override val day = 23

    private val regex = Regex("(\\S+)-(\\S+)")
    private val connections = input().split("\r\n").map { line ->
        val (comp1, comp2) = regex.find(line)!!.destructured
        Connection(comp1, comp2)
    }
    private val gameFinder = GameFinder(connections)

    override fun task1(): String {
        val result = gameFinder.threePlayerGames.filter { it.anyCompStartsWithT }.size
        return result.toString()
    }

    override fun task2(): String {
        val result = gameFinder.largestGame.sorted().joinToString(",")
        return result
    }

    private class GameFinder(connections: List<Connection>) {
        private val computers = connections.map { it.comp1 }.plus(connections.map { it.comp2 }).toSet().toList()
        private val connectionsMap = connections.map { it.id }.toSet()

        val largestGame: Set<String>
        val threePlayerGames = mutableListOf<Game>()

        init {
            for (comp1Idx in computers.indices) {
                for (comp2Idx in comp1Idx + 1..computers.size - 2) {
                    for (comp3Idx in comp2Idx + 1..<computers.size) {
                        val comp1 = computers[comp1Idx]
                        val comp2 = computers[comp2Idx]
                        val comp3 = computers[comp3Idx]

                        if (connectionsMap.contains(Connection(comp1, comp2).id) &&
                            connectionsMap.contains(Connection(comp2, comp3).id) &&
                            connectionsMap.contains(Connection(comp1, comp3).id)
                        ) {
                            threePlayerGames.add(Game(comp1, comp2, comp3))
                        }
                    }
                }
            }

            val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
            computers.forEach { graph.addVertex(it) }
            connections.forEach {
                graph.addEdge(it.comp1, it.comp2)
                graph.addEdge(it.comp2, it.comp1)
            }

            val algorithm = BronKerboschCliqueFinder(graph)
            largestGame = algorithm.maximumIterator().next()
        }
    }

    private data class Game(val comp1: String, val comp2: String, val comp3: String) {
        val anyCompStartsWithT = comp1.startsWith("t") || comp2.startsWith("t") || comp3.startsWith("t")
    }

    private data class Connection(val comp1: String, val comp2: String) {
        val id = if (comp1 < comp2) comp1 + comp2 else comp2 + comp1
    }
}