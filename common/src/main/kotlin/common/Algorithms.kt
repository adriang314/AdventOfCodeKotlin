package common

import java.util.PriorityQueue

data class GraphNode(val position: Position, val neighbors: MutableMap<GraphNode, Int> = mutableMapOf())

class Graph {
    private val nodes = mutableMapOf<Position, GraphNode>()

    fun addNode(position: Position): GraphNode {
        return nodes.getOrPut(position) { GraphNode(position) }
    }

    fun addEdge(from: Position, to: Position, weight: Int = 1) {
        val fromNode = addNode(from)
        val toNode = addNode(to)
        fromNode.neighbors[toNode] = weight
    }

    fun getNode(position: Position): GraphNode? {
        return nodes[position]
    }

    fun initializeFromText(input: String) {
        val lines = input.lines()
        val height = lines.size
        val width = lines[0].length

        for (y in lines.indices) {
            for (x in lines[y].indices) {
                if (lines[y][x] == '.') {
                    val currentNode = addNode(Position(x, y))
                    if (x > 0 && lines[y][x - 1] == '.') {
                        val leftNode = addNode(Position(x - 1, y))
                        currentNode.neighbors[leftNode] = 1
                        leftNode.neighbors[currentNode] = 1
                    }
                    if (y > 0 && lines[y - 1][x] == '.') {
                        val topNode = addNode(Position(x, y - 1))
                        currentNode.neighbors[topNode] = 1
                        topNode.neighbors[currentNode] = 1
                    }
                }
            }
        }
    }
}

class Algorithms {

    fun getPath(start: GraphNode, target: GraphNode): List<GraphNode> {
        val distances = mutableMapOf<GraphNode, Int>().withDefault { Int.MAX_VALUE }
        val previousNodes = mutableMapOf<GraphNode, GraphNode?>()
        val priorityQueue = PriorityQueue(compareBy<Pair<GraphNode, Int>> { it.second })

        distances[start] = 0
        priorityQueue.add(start to 0)

        while (priorityQueue.isNotEmpty()) {
            val (currentNode, currentDistance) = priorityQueue.poll()

            if (currentNode == target) break

            for ((neighbor, weight) in currentNode.neighbors) {
                val distance = currentDistance + weight
                if (distance < distances.getValue(neighbor)) {
                    distances[neighbor] = distance
                    previousNodes[neighbor] = currentNode
                    priorityQueue.add(neighbor to distance)
                }
            }
        }

        return generatePath(previousNodes, target)
    }

    private fun generatePath(
        previousNodes: Map<GraphNode, GraphNode?>,
        target: GraphNode
    ): List<GraphNode> {
        val path = mutableListOf<GraphNode>()
        var currentNode: GraphNode? = target

        while (currentNode != null) {
            path.add(currentNode)
            currentNode = previousNodes[currentNode]
        }

        return path.reversed()
    }
}