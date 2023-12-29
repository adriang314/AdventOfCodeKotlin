package year2023

import java.util.*

fun main() {
    println("${SolutionDay17()}")
}

class SolutionDay17 : BaseSolution() {

    override val day = 17

    override fun task1(): String {
        with(Graph(edges, Task.One)) {
            dijkstra(nodes[0][0].name)
            return this.graph[nodes.last.last.name]!!.dist.values.distinct().sorted().toString()
        }
    }

    override fun task2(): String {
        with(Graph(edges, Task.Two)) {
            dijkstra(nodes[0][0].name)
            return this.graph[nodes.last.last.name]!!.dist.values.distinct().sorted().toString()
        }
    }

    private var edges: LinkedList<Edge>
    private var nodes: List<List<Node>>

    init {
        val rawLines = input().split("\n")
        nodes = rawLines.mapIndexed { rowIdx, s -> Line(s, rowIdx).nodes }
        edges = LinkedList<Edge>()
        for (i in nodes.indices) {
            for (j in 0..<nodes[i].size) {
                val top = nodes.getOrNull(i - 1)?.getOrNull(j)
                val bottom = nodes.getOrNull(i + 1)?.getOrNull(j)
                val left = nodes.getOrNull(i)?.getOrNull(j - 1)
                val right = nodes.getOrNull(i)?.getOrNull(j + 1)
                val current = nodes[i][j]

                if (top != null)
                    edges.add(Edge(current.name, top.name, top.heat, Direction.Up))
                if (bottom != null)
                    edges.add(Edge(current.name, bottom.name, bottom.heat, Direction.Down))
                if (left != null)
                    edges.add(Edge(current.name, left.name, left.heat, Direction.Left))
                if (right != null)
                    edges.add(Edge(current.name, right.name, right.heat, Direction.Right))
            }
        }
    }

    private class Line(line: String, val rowIdx: Int) {
        val nodes = line.filter { c -> c.isDigit() }.mapIndexed { colIdx, c -> Node(rowIdx, colIdx, c.digitToInt()) }
    }

    class Node(rowIdx: Int, colIdx: Int, val heat: Int) {
        val name = "[$rowIdx,$colIdx]"
    }
}

enum class Direction { Left, Right, Up, Down }

class Edge(val v1: String, val v2: String, val dist: Int, val direction: Direction)

data class Distance(val value: Int, val direction: Direction)

data class Step(var count: Int, var direction: Direction? = null)

/** One vertex of the graph, complete with mappings to neighbouring vertices */
class Vertex(private val name: String) : Comparable<Vertex> {

    var dist: MutableMap<Step, Int> = mutableMapOf()  // 1, 2, 3,
    var previous: Vertex? = null
    val neighbours = HashMap<Vertex, Distance>()

    fun minDistance() = dist.values.minOrNull() ?: Int.MAX_VALUE

    fun isAlternativeTask1(prev: Vertex, distance: Distance): List<Pair<Step, Int>> {
        val update = LinkedList<Pair<Step, Int>>()
        prev.dist.forEach { (steps, prevDistance) ->
            val newDistance = distance.value + prevDistance
            val newDirection = distance.direction
            if (steps.direction == distance.direction) {
                if (steps.count < 3) {
                    val newStep = Step(steps.count + 1).apply { direction = newDirection }
                    val currDist = dist[newStep] ?: Int.MAX_VALUE
                    if (newDistance < currDist) update.add(Pair(newStep, newDistance))
                }
            } else {
                val newStep = Step(1).apply { direction = newDirection }
                val currDist = dist[newStep] ?: Int.MAX_VALUE
                if (newDistance < currDist) update.add(Pair(newStep, newDistance))
            }
        }
        return update
    }

    fun isAlternativeTask2(prev: Vertex, distance: Distance): List<Pair<Step, Int>> {
        val update = LinkedList<Pair<Step, Int>>()
        prev.dist.forEach { (steps, prevDistance) ->
            val newDistance = distance.value + prevDistance
            val newDirection = distance.direction
            if (steps.direction == distance.direction || steps.direction == null) {
                if (steps.count < 10) {
                    val newStep = Step(steps.count + 1).apply { direction = newDirection }
                    val currDist = dist[newStep] ?: Int.MAX_VALUE
                    if (newDistance < currDist) update.add(Pair(newStep, newDistance))
                }
            } else {
                if (steps.count >= 4) {
                    val newStep = Step(1).apply { direction = newDirection }
                    val currDist = dist[newStep] ?: Int.MAX_VALUE
                    if (newDistance < currDist) update.add(Pair(newStep, newDistance))
                }
            }
        }
        return update
    }

    override fun compareTo(other: Vertex): Int {
        if (this.dist.isEmpty() && other.dist.isEmpty())
            return name.compareTo(other.name)
        if (this.dist.isEmpty() && other.dist.isNotEmpty())
            return 1
        if (this.dist.isNotEmpty() && other.dist.isEmpty())
            return -1
        val minDist = minDistance()
        val otherMinDist = other.minDistance()
        if (minDist == otherMinDist) return name.compareTo(other.name)
        return minDist.compareTo(otherMinDist)
    }

    override fun toString() = "($name, $dist)"
}

enum class Task { One, Two }

class Graph(edges: List<Edge>, private val task: Task) {
    // mapping of vertex names to Vertex objects, built from a set of Edges
    val graph = HashMap<String, Vertex>(edges.size)

    init {
        // one pass to find all vertices
        for (e in edges) {
            if (!graph.containsKey(e.v1)) graph[e.v1] = Vertex(e.v1)
            if (!graph.containsKey(e.v2)) graph[e.v2] = Vertex(e.v2)
        }

        // another pass to set neighbouring vertices
        for (e in edges) {
            graph[e.v1]!!.neighbours[graph[e.v2]!!] = Distance(e.dist, e.direction)
        }
    }

    /** Runs dijkstra using a specified source vertex */
    fun dijkstra(startName: String) {
        if (!graph.containsKey(startName)) {
            println("Graph doesn't contain start vertex '$startName'")
            return
        }
        val source = graph[startName]
        val q = sortedSetOf<Vertex>()

        // set-up vertices
        for (v in graph.values) {
            v.previous = if (v == source) source else null
            v.dist = if (v == source) mutableMapOf(Step(1) to 0) else mutableMapOf()
            q.add(v)
        }

        dijkstra(q)
    }

    /** Implementation of dijkstra's algorithm using a binary heap */
    private fun dijkstra(q: TreeSet<Vertex>) {
        while (!q.isEmpty()) {
            // vertex with the shortest distance (first iteration will return source)
            val prev = q.pollFirst()!!
            // if distance is infinite we can ignore 'u' (and any other remaining vertices)
            // since they are unreachable
            if (prev.minDistance() == Int.MAX_VALUE)
                break

            //look at distances to each neighbour
            for (neighbour in prev.neighbours) {
                val neighbourKey = neighbour.key // the neighbour in this iteration

                val alternative = when (task) {
                    Task.One -> neighbourKey.isAlternativeTask1(prev, neighbour.value)
                    Task.Two -> neighbourKey.isAlternativeTask2(prev, neighbour.value)
                }

                if (alternative.isNotEmpty()) {
                    q.remove(neighbourKey)
                    alternative.forEach {
                        if ((neighbourKey.dist[it.first] ?: Int.MAX_VALUE) > it.second)
                            neighbourKey.dist[it.first] = it.second
                    }
                    neighbourKey.previous = prev
                    q.add(neighbourKey)
                }
            }
        }
    }
}
