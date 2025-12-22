package year2017

import common.BaseSolution
import java.util.*

fun main() = println(SolutionDay24().result())

class SolutionDay24 : BaseSolution() {

    override val day = 24

    private val components = input().split("\r\n").map { line ->
        val ports = line.split("/")
        Component(ports[0].toInt(), ports[1].toInt())
    }

    private val zeroPinPortComponents = components.filter { (port1, port2) -> port1 == 0 || port2 == 0 }

    override fun task1(): String {
        val result = zeroPinPortComponents.maxOf { start -> BridgeBuilder(components.toSet()).build(start).maxOf { it.strength() } }
        return result.toString()
    }

    override fun task2(): String {
        val result = zeroPinPortComponents.maxOf { start ->
            val bridges = BridgeBuilder(components.toSet()).build(start)
            val maxComponents = bridges.maxOf { it.componentsUsed.size }
            bridges.filter { it.componentsUsed.size == maxComponents }.maxOf { it.strength() }
        }

        return result.toString()
    }

    private class BridgeBuilder(private val components: Set<Component>) {

        fun build(start: Component): List<Bridge> {
            val bridgesUnderConstruction = LinkedList(listOf(Bridge(setOf(start), start.port2)))
            val bridgesConstructed = mutableListOf<Bridge>()

            while (bridgesUnderConstruction.isNotEmpty()) {
                val currentBridge = bridgesUnderConstruction.removeFirst()
                val nextComponents = nextComponents(currentBridge.port, components.minus(currentBridge.componentsUsed))
                if (nextComponents.isEmpty()) {
                    bridgesConstructed.add(currentBridge)
                }

                nextComponents.forEach { nextComponent -> bridgesUnderConstruction.add(currentBridge.add(nextComponent)) }
            }

            return bridgesConstructed
        }

        private fun nextComponents(port: Int, availableComponents: Set<Component>): List<Component> {
            return availableComponents.filter { it.port1 == port || it.port2 == port }
        }
    }

    private data class Bridge(val componentsUsed: Set<Component>, val port: Int) {
        fun strength() = componentsUsed.sumOf { it.port1 + it.port2 }

        fun add(component: Component) = Bridge(componentsUsed.plus(component), if (component.port1 == port) component.port2 else component.port1)
    }

    private data class Component(val port1: Int, val port2: Int)
}