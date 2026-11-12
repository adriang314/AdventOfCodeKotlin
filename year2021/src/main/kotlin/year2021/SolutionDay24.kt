package year2021

import com.microsoft.z3.Context
import com.microsoft.z3.Status
import common.*

fun main() = println(SolutionDay24().result())

class SolutionDay24 : BaseSolution() {

    override val day = 24

    override fun task1(): String = solve(maximize = true)

    override fun task2(): String = solve(maximize = false)

    private fun solve(maximize: Boolean): String {
        Context().use { ctx ->
            val solver = ctx.mkSolver()
            val digits = (1..14).associateWith { ctx.mkIntConst("w$it") }

            // Each digit must be between 1 and 9
            digits.values.forEach {
                solver.add(ctx.mkGe(it, ctx.mkInt(1)), ctx.mkLe(it, ctx.mkInt(9)))
            }

            val stack = ArrayDeque<Pair<Int, Int>>()
            val equations = mutableListOf<Pair<Int, Int>>()

            for (i in 1..14) {
                val operation = operationGroups[i - 1]

                if (operation.paramA == 1) {
                    stack.addLast(i to operation.paramC)
                } else if (operation.paramA == 26) {
                    val (previousIndex, previousC) = stack.removeLast()
                    equations.add(i to previousIndex)

                    val current = digits[i]!!
                    val previous = digits[previousIndex]!!

                    // w[i] = w[previousIndex] + previousC + paramB
                    solver.add(ctx.mkEq(current, ctx.mkAdd(previous, ctx.mkInt(previousC + operation.paramB))))
                }
            }

            check(stack.isEmpty()) { "Invalid MONAD stack" }

            for (i in 1..14) {
                val digit = digits[i]!!
                var best = -1
                val candidates = if (maximize) 9 downTo 1 else 1..9
                for (candidate in candidates) {
                    solver.push()
                    solver.add(ctx.mkEq(digit, ctx.mkInt(candidate)))
                    val satisfiable = solver.check() == Status.SATISFIABLE
                    solver.pop()
                    if (satisfiable) {
                        best = candidate
                        break
                    }
                }

                check(best != -1) { "No solution found" }

                solver.add(ctx.mkEq(digit, ctx.mkInt(best)))
            }

            check(solver.check() == Status.SATISFIABLE)

            return (1..14).joinToString("") { solver.model.eval(digits[it]!!, true).toString() }
        }
    }

    private val operationGroups: List<OperationGroup>

    init {
        var i = 0
        val operations = input().split("\r\n").map { line ->
            val lineParts = line.split(" ")
            if (lineParts.size == 3)
                Operation(
                    lineParts[0],
                    if (lineParts[1] == "w") lineParts[1] + i else lineParts[1],
                    if (lineParts[2] == "w") lineParts[2] + i else lineParts[2]
                )
            else
                Operation(lineParts[0], "w" + ++i, "")
        }
        operationGroups = (0..<14).map { idx -> OperationGroup(operations.drop(idx * 18).take(18)) }
    }

    data class OperationGroup(val operations: List<Operation>) {
        val paramA = operations[4].arg2.toInt()
        val paramB = operations[5].arg2.toInt()
        val paramC = operations[15].arg2.toInt()

        override fun toString(): String = "A: $paramA B: $paramB C: $paramC"
    }

    data class Operation(val name: String, val arg1: String, val arg2: String)
}
