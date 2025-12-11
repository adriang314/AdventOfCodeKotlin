package year2025

import common.BaseSolution
import org.jgrapht.alg.shortestpath.AllDirectedPaths
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import java.util.LinkedList

fun main() = println(SolutionDay11().result())

class SolutionDay11 : BaseSolution() {

    override val day = 11

    private val devices = input().split("\r\n").map { line ->
        val name = line.substringBefore(':')
        val connections = line.substringAfter(':').trim().split(" ")
        Device(name, connections)
    }.plus(Device("out"))

    override fun task1(): String {
        val paths = LinkedList(listOf(Path(emptySet(), "you")))
        val results = LinkedList<Path>()
        while (paths.isNotEmpty()) {
            val path = paths.removeFirst()
            val lastDeviceName = path.lastDeviceName
            if (lastDeviceName == "out") {
                results.add(path)
            } else {
                val nextDeviceNames = devices.first { it.name == lastDeviceName }.output
                nextDeviceNames.forEach { paths.add(path.add(it)) }
            }
        }

        return results.size.toString()
    }

    override fun task2(): String {
        val graph = SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
        devices.forEach { graph.addVertex(it.name) }
        devices.forEach { device -> device.output.forEach { outputName -> graph.addEdge(device.name, outputName) } }

        val part1 = AllDirectedPaths(graph).getAllPaths("svr", "fft", true, 20)
        val part2 = AllDirectedPaths(graph).getAllPaths("fft", "dac", true, 20)
        val part3 = AllDirectedPaths(graph).getAllPaths("dac", "out", true, 20)

        val result = 1L * part1.size * part2.size * part3.size
        return result.toString()
    }

    /**
     * Usage:
     * create an input file name and execute below command line to generate svg with graph visualization
     * .\dot.exe -Tsvg input.txt > output.svg
     */
    private fun buildGraphvizInput(): String {
        val graphEdges = devices.flatMap { device -> device.output.map { outputName -> "${device.name} -> ${outputName};" } }.joinToString("\n")
        val input =
            """
               |digraph {
               |$graphEdges
               |}
            """
        return input.trimMargin()
    }

    private data class Path(val deviceNames: Set<String>, val lastDeviceName: String) {
        fun add(device: String) = Path(deviceNames.plus(device), device)
    }

    private data class Device(val name: String, var output: List<String> = emptyList())
}