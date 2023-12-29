package year2023

import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph

fun main() {
    println("${SolutionDay25()}")
}

class SolutionDay25 : BaseSolution() {

    override val day = 25

    override fun task1(): String {
        val rawLines = input().split("\r\n", "\n")
        val graphParts = rawLines.map { Line(it) }.map { Pair(it.vertices, it.edges) }
        val edges = graphParts.map { it.second }.flatten().sortedBy { it.v1.name }
        val vertices = graphParts.map { it.first }.flatten().distinct().sortedBy { it.name }

        val graph = SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
        vertices.forEach { graph.addVertex(it.name) }
        edges.forEach { graph.addEdge(it.v1.name, it.v2.name) }

        val finder = StoerWagnerMinimumCut(graph)
        val minCut = finder.minCut()
        val result = minCut.size * (vertices.size - minCut.size)
        return result.toString()
    }

    override fun task2(): String {
        return ""
    }

    class Line(l: String) {
        val vertices: List<Vertex>
        val edges: List<Edge>

        init {
            val result = regex.find(l)!!
            val sourceVertex = Vertex(result.groupValues[1])
            val targetVertices = result.groupValues[2].split(" ").map { Vertex(it) }

            vertices = listOf(sourceVertex).plus(targetVertices)
            edges = targetVertices.map { Edge(sourceVertex, it) }
        }

        companion object {
            private val regex = Regex("^(\\w+): (.*)$")
        }
    }

    data class Vertex(var name: String)

    data class Edge(var v1: Vertex, var v2: Vertex)
}