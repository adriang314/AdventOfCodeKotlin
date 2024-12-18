package year2019

import common.BaseSolution
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph

fun main() = println(SolutionDay06().result())

class SolutionDay06 : BaseSolution() {

    override val day = 6
    
    private val orbits = input().split("\r\n").map {
        val parts = it.split(")")
        Orbit(parts[0], parts[1])
    }

    override fun task1(): String {
        val map = Map(orbits)
        val orbits = map.allOrbits()
        return orbits.toString()
    }

    override fun task2(): String {
        val map = Map(orbits)
        val orbits = map.pathToSanta()
        return orbits.toString()
    }

    private data class Orbit(val centralObject: String, val orbitingObject: String)

    private class Map(val orbits: List<Orbit>) {

        private val orbitingObjects = orbits.groupBy { it.orbitingObject }
        private val myOrbit = orbits.first { it.orbitingObject == "YOU" }
        private val santaOrbit = orbits.first { it.orbitingObject == "SAN" }

        fun pathToSanta(): Int {
            val graph = SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
            orbits.forEach {
                graph.addVertex(it.centralObject)
                graph.addVertex(it.orbitingObject)
                graph.addEdge(it.centralObject, it.orbitingObject)
            }

            val path = DijkstraShortestPath(graph).getPath(myOrbit.centralObject, santaOrbit.centralObject)
            return path.length
        }

        fun allOrbits() = orbits.sumOf { allOrbitsCount(it) }

        private fun allOrbitsCount(orbit: Orbit): Int =
            1 + orbitingObjects.getOrDefault(orbit.centralObject, emptyList()).sumOf { allOrbitsCount(it) }
    }
}