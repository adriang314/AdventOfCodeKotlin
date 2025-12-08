package year2025

import common.BaseSolution
import common.Point3D
import java.util.LinkedList

fun main() = println(SolutionDay08().result())

class SolutionDay08 : BaseSolution() {

    override val day = 8

    private val points3D = input().split("\r\n").map { line ->
        val parts = line.split(",")
        Point3D(parts[0].toLong(), parts[1].toLong(), parts[2].toLong())
    }

    private val sortedConnections: List<Point3DConnection>

    init {
        val tmp = LinkedList<Point3DConnection>()
        for (i in points3D.indices) {
            for (j in i + 1 until points3D.size) {
                val p1 = points3D[i]
                val p2 = points3D[j]
                tmp.add(Point3DConnection(p1.euclideanDistance(p2), p1, p2))
            }
        }

        sortedConnections = tmp.sorted()
    }

    override fun task1(): String {
        var circuits = listOf<Circuit>()
        sortedConnections.take(1000).forEach {
            circuits = merge(circuits.plus(Circuit(setOf(it.p1, it.p2))))
        }

        val threeBiggestCircuits = circuits.reversed().take(3).map { it.points.size }
        val result = threeBiggestCircuits.fold(1L) { acc, circuitSize -> acc * circuitSize }
        return result.toString()
    }

    override fun task2(): String {
        var circuits = listOf<Circuit>()
        var i = 0
        lateinit var lastConnection: Point3DConnection
        while (circuits.size != 1 || circuits[0].points.size != points3D.size) {
            val connection = sortedConnections[i++]
            lastConnection = connection
            circuits = merge(circuits.plus(Circuit(setOf(connection.p1, connection.p2))))
        }

        val res = lastConnection.p1.x * lastConnection.p2.x
        return res.toString()
    }

    private fun merge(list: List<Circuit>): List<Circuit> {
        val circuits = list.toMutableList()
        var merged = true
        while (merged) {
            merged = false
            loop@ for (i in circuits.indices) {
                for (j in i + 1 until circuits.size) {
                    val c1 = circuits[i]
                    val c2 = circuits[j]
                    if (c1.points.intersect(c2.points).isNotEmpty()) {
                        val newCircuit = Circuit(c1.points.union(c2.points))
                        circuits.removeAt(j)
                        circuits.removeAt(i)
                        circuits.add(newCircuit)
                        merged = true
                        break@loop
                    }
                }
            }
        }
        return circuits.sorted()
    }

    private data class Circuit(val points: Set<Point3D>) : Comparable<Circuit> {
        override fun compareTo(other: Circuit): Int {
            return points.size.compareTo(other.points.size)
        }
    }

    private data class Point3DConnection(val distance: Double, val p1: Point3D, val p2: Point3D) : Comparable<Point3DConnection> {
        override fun compareTo(other: Point3DConnection): Int {
            return distance.compareTo(other.distance)
        }
    }
}