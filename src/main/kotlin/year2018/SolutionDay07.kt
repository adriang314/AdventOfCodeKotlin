package year2018

import common.BaseSolution
import kotlin.math.max

fun main() = println(SolutionDay07().result())

class SolutionDay07 : BaseSolution() {

    override val day = 7
    override val year = 2018

    private val instructionRegex = Regex("^Step (\\S) must be finished before step (\\S) can begin\\.$")
    private val instructions = input().split("\r\n").map { line ->
        val (parent, child) = instructionRegex.find(line)!!.destructured
        Instruction(parent, child)
    }

    override fun task1(): String {
        val steps = Steps(instructions)
        val completed = mutableListOf<String>()
        var queue = steps.root

        while (queue.isNotEmpty()) {
            val nextStep = queue.first()
            completed.add(nextStep.name)
            queue = steps.filterAndSort { it.canBeWorkedOn(completed) }
        }

        return completed.joinToString("")
    }

    override fun task2(): String {
        val steps = Steps(instructions)
        val completed = mutableListOf<String>()
        val workers = Workers(5)
        var seconds = 0
        var queue = steps.root

        while (completed.size != steps.count) {

            while (workers.isAnyFree() && queue.any { workers.isNotWorkedOn(it) }) {
                val nextStep = queue.first { workers.isNotWorkedOn(it) }
                workers.assignWork(nextStep)
            }

            seconds++

            workers.processWork { step -> completed.add(step.name) }

            queue = steps.filterAndSort { it.canBeWorkedOn(completed) && workers.isNotWorkedOn(it) }
        }

        return seconds.toString()
    }

    private class Workers(total: Int) {
        private val workers = (1..total).map { Worker(it) }

        fun isAnyFree() = workers.any { it.isFree() }

        fun isNotWorkedOn(step: Step) = workers.all { it.isNotWorkedOn(step) }

        fun assignWork(step: Step) = workers.first { it.isFree() }.assignWork(step)

        fun processWork(onComplete: (Step) -> Unit) = workers.forEach { it.processWork(onComplete) }
    }

    private data class Worker(val id: Int) {
        private var currentWorkTimeToComplete: Int = 0
        private var currentStep: Step? = null

        fun isFree() = currentStep == null

        fun isNotWorkedOn(step: Step) = currentStep !== step

        fun assignWork(step: Step) {
            this.currentWorkTimeToComplete = step.timeToCompleteInSec
            this.currentStep = step
        }

        fun processWork(onComplete: (Step) -> Unit) {
            if (currentStep == null)
                return

            currentWorkTimeToComplete = max(0, currentWorkTimeToComplete - 1)
            if (currentWorkTimeToComplete == 0) {
                onComplete(currentStep!!)
                currentStep = null
            }
        }
    }

    private class Steps(instructions: List<Instruction>) {
        private val steps = instructions.map { it.childStep }.union(instructions.map { it.parentStep }).toSet()
            .sortedBy { it }.mapIndexed { i, stepName -> Step(stepName, 60 + i + 1) }
        val count = steps.size
        val root: List<Step>

        init {
            instructions.forEach { instruction ->
                val parentStep = steps.first { it.name == instruction.parentStep }
                val childStep = steps.first { it.name == instruction.childStep }
                childStep.parentSteps.add(parentStep)
            }

            root = steps.filter { it.parentSteps.isEmpty() }.sortedBy { it.name }
        }

        fun filterAndSort(predicate: (Step) -> Boolean) = steps.filter { predicate(it) }.sortedBy { it.name }
    }

    private data class Step(val name: String, val timeToCompleteInSec: Int) {
        val parentSteps: MutableSet<Step> = mutableSetOf()
        fun canBeWorkedOn(completed: List<String>) =
            parentSteps.all { completed.contains(it.name) } && !completed.contains(name)
    }

    private data class Instruction(val parentStep: String, val childStep: String)
}