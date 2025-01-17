package year2017

import common.BaseSolution
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

fun main() = println(SolutionDay12().result())

class SolutionDay12 : BaseSolution() {

    override val day = 12

    private val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    private val connectivityInspector = ConnectivityInspector(graph)

    init {
        input().lines().map { line ->
            val parts = line.split(" <-> ")
            val from = parts[0]
            val to = parts[1].split(", ")

            graph.addVertex(from)
            to.forEach {
                graph.addVertex(it)
                if (from != it) {
                    graph.addEdge(from, it)
                }
            }
        }
    }

    override fun task1(): String {
        return connectivityInspector.connectedSetOf("0").size.toString()
    }

    override fun task2(): String {
        return connectivityInspector.connectedSets().size.toString()
    }
}