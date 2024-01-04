package year2022

import common.BaseSolution
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16
    override val year = 2022

    override fun task1(): String {
        maxPath = EnabledValves(emptyMap())
        calculatePaths(start, EnabledValves(mapOf(start to 0)), 0)
        return maxPath.totalPressure30.toString()
    }

    override fun task2(): String {
        maxPath = EnabledValves(emptyMap())
        calculatePaths(start, start, EnabledValves(mapOf(start to 0)), 0, 0)
        return maxPath.totalPressure26.toString()
    }

    private val valves: List<Valve>
    private val valvePaths: Map<Valve, List<Pair<Valve, Int>>>
    private val start: Valve
    private var maxPath = EnabledValves(emptyMap())

    private fun calculatePaths(
        valveMe: Valve,
        valveElephant: Valve,
        enabledValves: EnabledValves,
        timeMe: Int,
        timeElephant: Int
    ) {
        if (timeMe >= 7 && timeElephant >= 7) {
            val currPressure = enabledValves.totalPressure26
            if (currPressure < maxPath.totalPressure26 * 0.85)
                return
        }

        if (timeMe > 26 && timeElephant > 26) {
            checkMaxPressure26(enabledValves)
            return
        }

        val valvesMe = valvePaths[valveMe]!!.filter { !enabledValves.list.containsKey(it.first) }.sortedBy { it.second }
        if (valvesMe.isEmpty()) {
            checkMaxPressure26(enabledValves)
            return
        }

        val valvesElephant =
            valvePaths[valveElephant]!!.filter { !enabledValves.list.containsKey(it.first) }.sortedBy { it.second }
        if (valvesElephant.isEmpty()) {
            checkMaxPressure26(enabledValves)
            return
        }

        for (i in valvesMe.indices)
            for (j in valvesElephant.indices) {
                val v1 = valvesMe[i]
                val v2 = valvesElephant[j]
                if (v1 == v2)
                    continue

                val newTimeMe = timeMe + v1.second + 1
                val newTimeElephant = timeElephant + v2.second + 1

                val newEnabledValves = enabledValves.plus(v1.first, newTimeMe).plus(v2.first, newTimeElephant)
                calculatePaths(v1.first, v2.first, newEnabledValves, newTimeMe, newTimeElephant)
            }
    }

    private fun calculatePaths(
        valveMe: Valve,
        enabledValves: EnabledValves,
        timeMe: Int,
    ) {
        if (timeMe > 30) {
            checkMaxPressure30(enabledValves)
            return
        }

        val availableValvesMe = valvePaths[valveMe]!!.filter { !enabledValves.list.containsKey(it.first) }
        if (availableValvesMe.isEmpty()) {
            checkMaxPressure30(enabledValves)
            return
        }

        availableValvesMe.map {
            val time = timeMe + it.second + 1
            calculatePaths(it.first, enabledValves.plus(it.first, time), time)
        }
    }

    private fun checkMaxPressure30(enabledValves: EnabledValves) {
        if (enabledValves.totalPressure30 > maxPath.totalPressure30)
            maxPath = enabledValves
    }

    private fun checkMaxPressure26(enabledValves: EnabledValves) {
        if (enabledValves.totalPressure26 > maxPath.totalPressure26)
            maxPath = enabledValves
    }

    init {
        val lines = input().split("\r\n")
        val regex = Regex("Valve (\\w{2}) has flow rate=(\\d+); tunnels? leads? to valves? (.*)$")
        val tmpValves = lines.map {
            val match = regex.find(it)!!
            Valve(match.groupValues[1], match.groupValues[2].toInt(), match.groupValues[3])
        }

        tmpValves.forEach { valve ->
            valve.neighbours = valve.tunnels
                .split(", ")
                .map { valveName -> Neighbour(tmpValves.first { it.name == valveName }, 1) }
                .toMutableList()
        }

        start = tmpValves.first { it.name == "AA" }

        tmpValves.filter { it == start || it.rate > 0 }.forEach { valve ->
            val zeroRateNeighbours = valve.neighbours.filter { it.valve.rate == 0 && it.valve != start }
            zeroRateNeighbours.forEach { zeroRateNeighbor ->
                val validNeighbour = findValidNeighbour(valve, zeroRateNeighbor.valve, zeroRateNeighbor.length)
                valve.neighbours.add(validNeighbour)
                valve.neighbours.remove(zeroRateNeighbor)
            }
        }

        valves = tmpValves.filter { it.rate > 0 || it == start }

        val graph = SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
        valves.forEach { graph.addVertex(it.name) }
        valves.forEach { valve ->
            valve.neighbours.forEach {
                graph.addEdge(valve.name, it.valve.name)
                graph.setEdgeWeight(valve.name, it.valve.name, it.length.toDouble())
            }
        }
        val dijkstraShortestPath = DijkstraShortestPath(graph)

        val tmpValvePaths = mutableMapOf<Valve, List<Pair<Valve, Int>>>()
        for (i in valves.indices)
            for (j in valves.indices) {
                if (i == j)
                    continue
                val valve1 = valves[i]
                val valve2 = valves[j]
                val path = dijkstraShortestPath.getPath(valve1.name, valve2.name)
                tmpValvePaths.compute(valve1) { _, curr ->
                    val pair = Pair(valve2, path.weight.toInt())
                    curr?.plus(pair) ?: listOf(pair)
                }
            }

        valvePaths = tmpValvePaths
    }

    private data class EnabledValves(val list: Map<Valve, Int>) {

        val totalPressure26 = list.filter { it.value <= 26 }.map { it.key.rate * (26 - it.value) }.sum()
        val totalPressure30 = list.filter { it.value <= 30 }.map { it.key.rate * (30 - it.value) }.sum()

        fun plus(valve: Valve, time: Int) = EnabledValves(this.list.plus(valve to time))
    }

    private fun findValidNeighbour(valve: Valve, zeroRateNeighbour: Valve, length: Int): Neighbour {
        val nextNeighbour = zeroRateNeighbour.neighbours.first { it.valve != valve }
        return if (nextNeighbour.valve.rate > 0 || nextNeighbour.valve == start)
            Neighbour(nextNeighbour.valve, length + 1)
        else
            findValidNeighbour(zeroRateNeighbour, nextNeighbour.valve, length + 1)
    }

    data class Valve(val name: String, val rate: Int, val tunnels: String) {
        var neighbours = mutableListOf<Neighbour>()

        override fun toString() = name
    }

    data class Neighbour(val valve: Valve, val length: Int)
}