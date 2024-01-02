package year2023

import common.BaseSolution

fun main() = println(SolutionDay19().result())

class SolutionDay19 : BaseSolution() {

    override val day = 19

    override fun task1(): String {
        val acceptedParts = workflows.acceptedPaths().distinct()
        val result = acceptedParts.sumOf { it.sumOfPartItems }
        return result.toString()
    }

    override fun task2(): String {
        val part = Part(1..4000, 1..4000, 1..4000, 1..4000)
        val acceptedParts = workflows.acceptedPaths(listOf(part))
        val result = acceptedParts.sumOf { it.totalDistinctParts }
        return result.toString()
    }

    private var workflows: Workflows

    init {
        val rawLines = input().split("\r\n", "\n")
        val emptyLine = rawLines.indexOfFirst { it.isEmpty() }
        val flows = rawLines.filterIndexed { index, _ -> index < emptyLine }
        val rules = rawLines.filterIndexed { index, _ -> index > emptyLine }
        workflows = Workflows(flows, rules)
    }

    class Workflows(flows: List<String>, parts: List<String>) {
        private val partRegex = Regex("x=(\\w+),m=(\\w+),a=(\\w+),s=(\\w+)")
        private val flowRegex = Regex("(\\w+)\\{(.*)}")
        private val parts: List<Part>
        private val workflows: List<Workflow>
        private val startWorkflow: Workflow
        private var workflowsTree: WorkflowsTree

        init {
            this.parts = parts.map {
                val match = partRegex.find(it)!!
                val x = match.groupValues[1].toInt()
                val m = match.groupValues[2].toInt()
                val a = match.groupValues[3].toInt()
                val s = match.groupValues[4].toInt()
                Part(x..x, m..m, a..a, s..s)
            }

            this.workflows = flows.map {
                val match = flowRegex.find(it)!!
                val name = match.groupValues[1]
                val conditions = match.groupValues[2]
                Workflow(name, conditions.split(",").map { text -> Condition(text) })
            }

            this.startWorkflow = this.workflows.first { it.name == "in" }
            this.workflowsTree = WorkflowsTree(startWorkflow, this.workflows)
        }

        fun acceptedPaths() = acceptedPaths(this.parts)

        fun acceptedPaths(parts: List<Part>) =
            parts.map { part ->
                val allPaths = workflowsTree.paths(part)
                allPaths.filter { it.workflowNode.name == "A" && it.part.sumOfPartItems > 0L }.map { it.part }
            }.flatten()
    }

    class Condition(private val text: String) {
        val name: String
        private val formula: String?
        private val targetPart: Char?
        private val operator: Char?
        private val targetPartValue: Int?

        companion object {
            private val regex = Regex("(\\d+)")
        }

        init {
            val split = text.split(":")
            name = split.last()
            formula = if (split.size > 1) split.first() else null
            targetPart = formula?.first()
            operator = formula?.getOrNull(1)
            targetPartValue = if (formula != null) regex.find(formula)!!.groupValues[1].toInt() else null
        }

        override fun toString() = text

        fun applyFormulaOnPart(part: Part): Pair<Part, Part> {
            if (formula == null) {
                if (name == "A") return Pair(part, part)
                if (name == "R") return Pair(Part.EMPTY, Part.EMPTY)
                return Pair(part, part)
            }
            val value = targetPartValue!!
            if (targetPart == 'a') return applyFormulaOnPart(value, part, part.a, part::withA)
            if (targetPart == 'x') return applyFormulaOnPart(value, part, part.x, part::withX)
            if (targetPart == 's') return applyFormulaOnPart(value, part, part.s, part::withS)
            if (targetPart == 'm') return applyFormulaOnPart(value, part, part.m, part::withM)

            throw Exception("Unknown formula")
        }

        private fun applyFormulaOnPart(value: Int, part: Part, rng: IntRange, newPart: (IntRange) -> Part) =
            if (value in rng) {
                if (operator!! == '>') Pair(newPart(value + 1..rng.last), newPart(rng.first..value))
                else Pair(newPart(rng.first..<value), newPart(value..rng.last))
            } else {
                if (operator!! == '>')
                    if (value > rng.last) Pair(Part.EMPTY, part) else Pair(part, Part.EMPTY)
                else
                    if (value > rng.last) Pair(part, Part.EMPTY) else Pair(Part.EMPTY, part)
            }
    }

    data class Iteration(val workflowNode: WorkflowNode, val part: Part, val depth: Int)

    class WorkflowsTree(startWorkflow: Workflow, private val workflows: List<Workflow>) {
        private val rootNode = WorkflowNode(startWorkflow.name, null)

        init {
            buildWorkflowNodes(rootNode, startWorkflow)
        }

        fun paths(part: Part): List<Iteration> {
            val stack = ArrayDeque<Iteration>().apply { this.add(Iteration(rootNode, part, 0)) }
            val finalPaths = mutableListOf<Iteration>()
            val currPath = mutableListOf<Iteration>()

            while (stack.isNotEmpty()) {
                val currentNode = stack.removeFirst()
                currPath.removeIf { it.depth >= currentNode.depth }
                currPath.add(currentNode)

                var currSplit: Pair<Part, Part> = Pair(currentNode.part, currentNode.part)
                for (ch in currentNode.workflowNode.childWorkflows) {
                    currSplit = ch.condition!!.applyFormulaOnPart(currSplit.second)
                    stack.add(0, Iteration(ch, currSplit.first, currentNode.depth + 1))
                }

                if (currentNode.workflowNode.childWorkflows.isEmpty())
                    finalPaths.add(currPath.last())
            }

            return finalPaths
        }

        private fun buildWorkflowNodes(node: WorkflowNode, workflow: Workflow) {
            workflow.conditions.forEach { condition ->
                val conWorkflowNode = WorkflowNode(condition.name, condition)
                node.childWorkflows.add(conWorkflowNode)
                val childFlow = workflows.firstOrNull { it.name == condition.name }
                childFlow?.let { buildWorkflowNodes(conWorkflowNode, it) }
            }
        }
    }

    data class Part(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange) {
        fun withA(a: IntRange) = Part(x, m, a, s)
        fun withX(x: IntRange) = Part(x, m, a, s)
        fun withS(s: IntRange) = Part(x, m, a, s)
        fun withM(m: IntRange) = Part(x, m, a, s)

        val sumOfPartItems =
            if (x.isEmpty() || m.isEmpty() || a.isEmpty() || s.isEmpty()) 0L
            else 0L + x.last + m.last + a.last + s.last

        val totalDistinctParts =
            if (x.isEmpty() || m.isEmpty() || a.isEmpty() || s.isEmpty()) 0L
            else 1L * (x.last - x.first + 1) * (m.last - m.first + 1) * (a.last - a.first + 1) * (s.last - s.first + 1)

        companion object {
            val EMPTY = Part(IntRange.EMPTY, IntRange.EMPTY, IntRange.EMPTY, IntRange.EMPTY)
        }
    }

    data class WorkflowNode(
        val name: String,
        val condition: Condition?,
        val childWorkflows: MutableList<WorkflowNode> = mutableListOf()
    )

    data class Workflow(val name: String, val conditions: List<Condition>)
}