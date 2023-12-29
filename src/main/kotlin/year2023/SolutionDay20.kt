package year2023

import common.BaseSolution

private var iteration = 0L
private var iterationBh = 0L
private var iterationSh = 0L
private var iterationJf = 0L
private var iterationMz = 0L

fun main() {
    println("${SolutionDay20()}")
}

class SolutionDay20 : BaseSolution() {

    override val day = 20

    override fun task1(): String {
        val rawLines = input().split("\r\n", "\n")
        val input = Input(rawLines)
        val modules = input.modules
        val broadcast = modules.filterIsInstance<Broadcast>().first
        for (i in 0..<1000)
            broadcast.pushButton()
        val highPulses = modules.sumOf { it.highPulseSent }
        val lowPulses = modules.sumOf { it.lowPulseSent }
        val result = highPulses * lowPulses
        return result.toString()
    }

    override fun task2(): String {
        val rawLines = input().split("\r\n", "\n")
        val input = Input(rawLines)
        val modules = input.modules
        val broadcast = modules.filterIsInstance<Broadcast>().first
        while (iterationBh == 0L || iterationJf == 0L || iterationSh == 0L || iterationMz == 0L) {
            iteration++
            broadcast.pushButton()
        }

        val result = findLCMOfListOfNumbers(listOf(iterationJf, iterationMz, iterationSh, iterationBh))
        return result.toString()
    }

    private fun findLCMOfListOfNumbers(numbers: List<Long>): Long {
        var result = numbers[0]
        for (i in 1..<numbers.size) {
            result = findLCM(result, numbers[i])
        }
        return result
    }

    private fun findLCM(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLcm = a * b
        var lcm = larger
        while (lcm <= maxLcm) {
            if (lcm % a == 0L && lcm % b == 0L) {
                return lcm
            }
            lcm += larger
        }
        return maxLcm
    }

    class Input(lines: List<String>) {

        val modules = mutableListOf<Module>()

        init {
            lines.forEach {
                val splitResult = it.split("->")
                val name = splitResult[0].trim().replace("%", "").replace("&", "")
                val connections = splitResult[1].split(",").map { conn -> conn.trim() }
                if (it.startsWith("broadcaster"))
                    modules.add(Broadcast(connections))
                else if (it.startsWith("&"))
                    modules.add(Conjunction(name, connections))
                else if (it.startsWith("%"))
                    modules.add(FlipFlop(name, connections))
            }

            val finalModules = mutableListOf<FinalModule>()
            modules.forEach { module ->
                module.outConnectionNames.forEach { outConnectionName ->
                    var outConnection = modules.firstOrNull { it.name == outConnectionName }
                        ?: finalModules.firstOrNull { it.name == outConnectionName }

                    if (outConnection == null) {
                        val finalModule = FinalModule(outConnectionName)
                        finalModules.add(finalModule)
                        outConnection = finalModule
                    }

                    outConnection.let { module.outConnections.add(it) }
                }
            }

            modules.addAll(finalModules)

            modules.filterIsInstance<Conjunction>().forEach { conjunctionModule ->
                modules.filter { module -> module.outConnectionNames.contains(conjunctionModule.name) }
                    .forEach {
                        conjunctionModule.inConnections.add(it.name)
                        conjunctionModule.inConnectionsState[it.name] = Pulse.Low
                    }
            }
        }
    }

    class Iteration(val source: Module?, val target: Module, val pulse: Pulse)

    class Broadcast(outConnectionNames: List<String>) : Module("broadcaster", outConnectionNames) {
        fun pushButton() {
            val stack = ArrayDeque<List<Iteration>>()
            stack.add(listOf(Iteration(null, this, Pulse.Low)))

            while (stack.isNotEmpty()) {
                val currentIteration = stack.removeFirst()
                val newIteration = currentIteration.map {
                    it.target.updateState(it.source?.name ?: "", it.pulse)
                    val newPulse = it.target.sendPulse(it.pulse)
                    if (newPulse == null)
                        emptyList()
                    else {
                        val newTargets = it.target.outConnections
                        newTargets.map { newTarget -> Iteration(it.target, newTarget, newPulse) }
                    }
                }.flatten()

                if (newIteration.isNotEmpty())
                    stack.add(newIteration)
            }
        }

        override fun updateState(pulseFrom: String, pulse: Pulse) {
            // nothing to update
        }

        override fun sendPulse(pulse: Pulse): Pulse {
            handlePulseSent(pulse)
            return Pulse.Low
        }
    }

    class FlipFlop(name: String, outConnectionNames: List<String>) : Module(name, outConnectionNames) {
        private var lastPulse = Pulse.Low

        override fun updateState(pulseFrom: String, pulse: Pulse) {
            if (pulse == Pulse.Low)
                lastPulse = lastPulse.opposite()
        }

        override fun sendPulse(pulse: Pulse): Pulse? {
            handlePulseSent(pulse)
            return if (pulse == Pulse.Low) lastPulse else null
        }
    }

    class Conjunction(name: String, outConnectionNames: List<String>) : Module(name, outConnectionNames) {
        val inConnections = mutableListOf<String>()
        val inConnectionsState = mutableMapOf<String, Pulse>()

        override fun updateState(pulseFrom: String, pulse: Pulse) {
            inConnectionsState[pulseFrom] = pulse
        }

        override fun sendPulse(pulse: Pulse): Pulse {
            val highStates = inConnectionsState.count { it.value == Pulse.High }
            val pulseToSent = if (highStates == inConnectionsState.size) Pulse.Low else Pulse.High

            if (this.name == "mf") {
                if (inConnectionsState["bh"] == Pulse.High) {
                    if (iterationBh == 0L)
                        iterationBh = iteration
                }

                if (inConnectionsState["jf"] == Pulse.High) {
                    if (iterationJf == 0L)
                        iterationJf = iteration
                }

                if (inConnectionsState["sh"] == Pulse.High) {
                    if (iterationSh == 0L)
                        iterationSh = iteration
                }

                if (inConnectionsState["mz"] == Pulse.High) {
                    if (iterationMz == 0L)
                        iterationMz = iteration
                }
            }

            handlePulseSent(pulse)
            return pulseToSent
        }
    }

    class FinalModule(name: String) : Module(name, emptyList()) {

        override fun updateState(pulseFrom: String, pulse: Pulse) {
            // nothing
        }

        override fun sendPulse(pulse: Pulse): Pulse? {
            handlePulseSent(pulse)
            return null
        }
    }

    abstract class Module(val name: String, val outConnectionNames: List<String>) {

        val outConnections = mutableListOf<Module>()
        var highPulseSent = 0L
        var lowPulseSent = 0L

        abstract fun updateState(pulseFrom: String, pulse: Pulse)

        fun handlePulseSent(pulse: Pulse) {
            if (pulse == Pulse.Low) lowPulseSent++ else highPulseSent++
        }

        abstract fun sendPulse(pulse: Pulse): Pulse?

        override fun toString() = "$name $outConnectionNames"
    }

    enum class Pulse {
        High, Low;

        fun opposite() = if (this == High) Low else High
    }
}