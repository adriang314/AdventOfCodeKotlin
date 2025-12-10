package year2025

import com.microsoft.z3.Context
import com.microsoft.z3.Status
import common.BaseSolution
import java.util.*

fun main() = println(SolutionDay10().result())

class SolutionDay10 : BaseSolution() {

    override val day = 10

    private val machines = input().split("\r\n").map { line ->
        val lights = line.substring(1, line.indexOf(']')).map { it == '#' }
        val buttonRegex = """\((\S+)\)""".toRegex()
        val buttons = buttonRegex.findAll(line).mapIndexed { idx, matchResult ->
            val (toggles) = matchResult.destructured
            Button(idx, toggles.split(",").map { it.toInt() })
        }.toList()
        val voltageString = line.substring(line.indexOf('{') + 1, line.indexOf('}'))
        val voltage = voltageString.split(",").map { it.toInt() }

        Machine(buttons, lights, voltage)
    }

    override fun task1(): String {
        val result = machines.sumOf { it.findMinButtonPressForLights() }
        return result.toString()
    }

    override fun task2(): String {
        val result = machines.sumOf { it.findMinButtonPressForVoltage() }
        return result.toString()
    }

    private data class Machine(val buttons: List<Button>, val requiredLights: List<Boolean>, val requiredVoltage: List<Int>) {

        fun findMinButtonPressForLights(): Int {
            val statuses = LinkedList(listOf(LightStatus(emptyList(), requiredLights.map { false })))
            while (true) {
                val status = statuses.removeFirst()
                val nextStatuses = buttons.map { button -> status.press(button) }
                val result = nextStatuses.firstOrNull { it.lights == requiredLights }
                if (result != null)
                    return result.pressed.size

                statuses.addAll(nextStatuses)
            }
        }

        fun findMinButtonPressForVoltage(): Int = Context().use { ctx ->
            // Use optimize instead of Solver to find minimum
            val optimize = ctx.mkOptimize()

            // Define integer variables
            val variables = buttons.map { "b${it.id}" }.map { ctx.mkIntConst(it) }

            // Add non-negativity constraints
            variables.forEach { variable -> optimize.Add(ctx.mkGe(variable, ctx.mkInt(0))) }

            // Add equations
            requiredVoltage.forEachIndexed { idx, vol ->
                val buttonIds = buttons.filter { it.toggles.contains(idx) }.map { it.id }
                val equationVars = variables.filterIndexed { idx, _ -> buttonIds.contains(idx) }.toTypedArray()
                optimize.Add(ctx.mkEq(ctx.mkAdd(*equationVars), ctx.mkInt(vol)))
            }

            // Minimize the sum of variables
            val sumExpr = ctx.mkAdd(*variables.toTypedArray())
            optimize.MkMinimize(sumExpr)

            val calculated = optimize.Check() == Status.SATISFIABLE
            if (!calculated)
                throw IllegalStateException("Cannot find minimum button press for given voltage")

            val solution = variables.associateWith { optimize.model.eval(it, true).toString().toInt() }
            return solution.values.sum()
        }
    }

    private data class LightStatus(val pressed: List<Button>, var lights: List<Boolean>) {
        fun press(button: Button): LightStatus {
            return LightStatus(pressed.plus(button), lights.mapIndexed { idx, isOn -> if (button.toggles.contains(idx)) !isOn else isOn })
        }
    }

    private data class Button(val id: Int, val toggles: List<Int>)
}