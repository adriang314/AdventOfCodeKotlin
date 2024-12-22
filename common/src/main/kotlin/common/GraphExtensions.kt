package common

import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph

fun SimpleDirectedGraph<String, DefaultEdge>.addEdge(
    from: Position,
    to: Position,
) {
    addEdge(from.toString(), to.toString())
}

fun BidirectionalDijkstraShortestPath<String, DefaultEdge>.getPath(
    from: Position,
    to: Position
): GraphPath<String, DefaultEdge> {
    return this.getPath(from.toString(), to.toString())
}