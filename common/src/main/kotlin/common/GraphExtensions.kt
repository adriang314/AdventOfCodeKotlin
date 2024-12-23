package common

import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleDirectedGraph
import org.jgrapht.graph.SimpleDirectedWeightedGraph

fun SimpleDirectedGraph<String, DefaultEdge>.addVertex(
    position: Position,
) {
    addVertex(position.toString())
}

fun SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>.addVertex(
    position: Position,
) {
    addVertex(position.toString())
}

fun SimpleDirectedGraph<String, DefaultEdge>.addEdge(
    from: Position,
    to: Position,
) {
    addEdge(from.toString(), to.toString())
}

fun SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>.addEdge(
    from: Position,
    to: Position,
) {
    addEdge(from.toString(), to.toString())
}

fun SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>.setEdgeWeight(
    from: Position,
    to: Position,
    weight: Double
) {
    setEdgeWeight(from.toString(), to.toString(), weight)
}

fun DijkstraShortestPath<String, DefaultWeightedEdge>.getPath(
    from: Position,
    to: Position
): GraphPath<String, DefaultWeightedEdge>? {
    return getPath(from.toString(), to.toString())
}

fun BidirectionalDijkstraShortestPath<String, DefaultEdge>.getPath(
    from: Position,
    to: Position
): GraphPath<String, DefaultEdge>? {
    return getPath(from.toString(), to.toString())
}